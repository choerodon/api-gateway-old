package io.choerodon.gateway.filter;

import com.netflix.hystrix.exception.HystrixRuntimeException;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import io.choerodon.gateway.config.GatewayHelperProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandContext;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.util.StringUtils;
import org.springframework.web.util.UriUtils;
import org.springframework.web.util.WebUtils;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.net.URLDecoder;
import java.util.*;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_TOKEN;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.REQUEST_URI_KEY;

/**
 * 自定义的servlet filter
 * 负责将HTTP请求去除消息体后转发到gateway helper去权限校验，限流
 * gateway helper返回后将授权码和label加到消息头部
 * 再交给zuul去路由到真实服务
 *
 * @author zhipeng.zuo
 * @date 18-1-4
 */
public class GateWayHelperFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GateWayHelperFilter.class);

    private static final String ENCODING_UTF8 = "UTF-8";

    private GatewayHelperProperties gatewayHelperProperties;

    private RibbonCommandFactory<?> ribbonCommandFactory;

    private List<RibbonRequestCustomizer> requestCustomizers;

    private final AntPathMatcher matcher = new AntPathMatcher();

    /**
     * 构造器
     *
     * @param gatewayHelperProperties gatewayHelper的配置信息
     * @param requestCustomizers      spring内部requestCustomizers
     * @param ribbonCommandFactory    spring内部ribbon创建工厂
     */
    public GateWayHelperFilter(GatewayHelperProperties gatewayHelperProperties,
                               List<RibbonRequestCustomizer> requestCustomizers,
                               RibbonCommandFactory<?> ribbonCommandFactory) {
        this.gatewayHelperProperties = gatewayHelperProperties;
        this.requestCustomizers = requestCustomizers;
        this.ribbonCommandFactory = ribbonCommandFactory;
    }

    @Override
    public void init(FilterConfig filterConfig) {
        //不需要执行filter的初始化操作
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        boolean shouldSkipHelper = Arrays.stream(gatewayHelperProperties.getHelperSkipPaths())
                .anyMatch(t -> matcher.match(t, req.getRequestURI()));
        if (shouldSkipHelper) {
            chain.doFilter(req, res);
            return;
        }
        ClientHttpResponse clientHttpResponse = null;
        try {
            RibbonCommandContext commandContext = buildCommandContext(req);
            clientHttpResponse = forward(commandContext);
            if (clientHttpResponse.getStatusCode().is2xxSuccessful()) {
                request.setAttribute(HEADER_JWT, clientHttpResponse.getHeaders().getFirst(HEADER_TOKEN));
                chain.doFilter(request, res);
            } else {
                setGatewayHelperFailureResponse(clientHttpResponse, res);
            }
        } catch (ZuulException e) {
            res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
            res.setCharacterEncoding("utf-8");
            PrintWriter out = null;
            try {
                out = res.getWriter();
                out.println(e.getMessage());
                out.flush();
            } catch (IOException e1) {
                LOGGER.info("printWriter io error, {}", e);
            } finally {
                if (out != null) {
                    out.close();
                }
            }
        } finally {
            if (clientHttpResponse != null) {
                clientHttpResponse.close();
            }
        }
    }

    private void setGatewayHelperFailureResponse(ClientHttpResponse clientHttpResponse,
                                                 HttpServletResponse res) throws IOException {
        int statusCode = clientHttpResponse.getRawStatusCode();
        res.setStatus(statusCode);
        res.setCharacterEncoding("utf-8");
        PrintWriter out = null;
        try {
            out = res.getWriter();
            if (statusCode == HttpStatus.UNAUTHORIZED.value()) {
                res.setContentType("application/xhtml+xml");
                out.println("<oauth><error_description>Full authentication is required to access "
                        + "this resource</error_description><error>unauthorized</error></oauth>");
            } else {
                res.setContentType("text/plain");
                out.println("");
            }
            out.flush();
        } catch (IOException e) {
            LOGGER.info("printWriter io error, {}", e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    private String getVerb(HttpServletRequest request) {
        String method = request.getMethod();
        if (method == null) {
            return "GET";
        }
        return method;
    }

    private ClientHttpResponse forward(RibbonCommandContext context) throws ZuulException {
        RibbonCommand command = this.ribbonCommandFactory.create(context);
        try {
            return command.execute();
        } catch (HystrixRuntimeException ex) {
            throw new ZuulException(ex, "Forwarding gateway helper error", 500, ex.getMessage());
        }
    }

    private RibbonCommandContext buildCommandContext(HttpServletRequest req) {
        Boolean retryable = gatewayHelperProperties.isRetryable();
        String verb = getVerb(req);
        String uri = buildZuulRequestUri(req);
        MultiValueMap<String, String> headers = buildZuulRequestHeaders(req);
        MultiValueMap<String, String> params = buildZuulRequestQueryParams(req);
        InputStream requestEntity;
        long contentLength;
        String requestService = gatewayHelperProperties.getServiceId();
        requestEntity = new ByteArrayInputStream("".getBytes());
        contentLength = 0L;
        return new RibbonCommandContext(requestService, verb, uri, retryable, headers, params,
                requestEntity, this.requestCustomizers, contentLength);
    }


    private MultiValueMap<String, String> buildZuulRequestHeaders(HttpServletRequest request) {
        MultiValueMap<String, String> headers = new HttpHeaders();
        Enumeration<String> headerNames = request.getHeaderNames();
        if (headerNames != null) {
            while (headerNames.hasMoreElements()) {
                String name = headerNames.nextElement();
                if (isIncludedHeader(name)) {
                    Enumeration<String> values = request.getHeaders(name);
                    while (values.hasMoreElements()) {
                        String value = values.nextElement();
                        headers.add(name, value);
                    }
                }
            }
        }
        headers.set(HttpHeaders.ACCEPT_ENCODING, "gzip");
        return headers;
    }

    private boolean isIncludedHeader(String headerName) {
        String name = headerName.toLowerCase();
        switch (name) {
            case "host":
            case "connection":
            case "content-length":
            case "content-encoding":
            case "server":
            case "transfer-encoding":
            case "x-application-context":
                return false;
            default:
                return true;
        }
    }

    private MultiValueMap<String, String> buildZuulRequestQueryParams(HttpServletRequest request) {
        Map<String, List<String>> map = new HashMap<>(1 << 4);
        String queryString = request.getQueryString();
        if (StringUtils.isEmpty(queryString)) {
            queryString = "";
        }
        StringTokenizer st = new StringTokenizer(queryString, "&");
        int i;
        while (st.hasMoreTokens()) {
            String s = st.nextToken();
            i = s.indexOf('=');
            if (i > 0 && s.length() >= i + 1) {
                String name = s.substring(0, i);
                String value = s.substring(i + 1);
                try {
                    name = URLDecoder.decode(name, ENCODING_UTF8);
                    value = URLDecoder.decode(value, ENCODING_UTF8);
                } catch (Exception e) {
                    LOGGER.info("Uri decode error, {}", e.getMessage());
                }
                map.computeIfAbsent(name, k -> new LinkedList<>()).add(value);
            } else if (i == -1) {
                String name = s;
                String value = "";
                try {
                    name = URLDecoder.decode(name, ENCODING_UTF8);
                } catch (Exception e) {
                    LOGGER.info("Uri decode error, {}", e.getMessage());
                }
                map.computeIfAbsent(name, k -> new LinkedList<>()).add(value);
            }
        }
        MultiValueMap<String, String> params = new LinkedMultiValueMap<>();

        for (Map.Entry<String, List<String>> entry : map.entrySet()) {
            for (String value : entry.getValue()) {
                params.add(entry.getKey(), value);
            }
        }
        return params;
    }

    private String buildZuulRequestUri(HttpServletRequest request) {
        RequestContext context = RequestContext.getCurrentContext();
        String uri = request.getRequestURI();
        String contextUri = (String) context.get(REQUEST_URI_KEY);
        if (contextUri != null) {
            try {
                String encoding = request.getCharacterEncoding() != null ? request.getCharacterEncoding()
                        : WebUtils.DEFAULT_CHARACTER_ENCODING;
                uri = UriUtils.encodePath(contextUri, encoding).replace("//", "/");
            } catch (Exception e) {
                LOGGER.info("buildZuulRequestUri error, {}", e.getMessage());
            }
        }
        return uri;
    }

    @Override
    public void destroy() {
        //此过滤取退出不需要关闭额外资源
    }
}
