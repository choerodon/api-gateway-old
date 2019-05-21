package io.choerodon.gateway.filter.route;

import io.choerodon.gateway.domain.ResponseContext;
import io.choerodon.gateway.helper.AuthenticationHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;


/**
 * 自定义的servlet filter
 * 负责将HTTP请求去除消息体后转发到gateway helper去权限校验，限流
 * gateway helper返回后将授权码和label加到消息头部
 * 再交给zuul去路由到真实服务
 *
 * @author flyleft
 */
public class GateWayHelperFilter implements Filter {

    private static final Logger LOGGER = LoggerFactory.getLogger(GateWayHelperFilter.class);

    private static final int GATEWAY_HELPER_NOT_PASS = 403;

    private static final String CONFIG_ENDPOINT = "/choerodon/config";

    private AuthenticationHelper gatewayHelper;

    /**
     * 构造器
     *
     * @param gatewayHelper           鉴权helper
     */
    public GateWayHelperFilter(AuthenticationHelper gatewayHelper) {
        this.gatewayHelper = gatewayHelper;

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
        if (CONFIG_ENDPOINT.equals(req.getRequestURI())) {
            chain.doFilter(request, res);
            return;
        }
        ResponseContext responseContext = gatewayHelper.authentication(req);

        if (responseContext.getHttpStatus().is2xxSuccessful()) {
            chain.doFilter(req, res);
        } else {
            setGatewayHelperFailureResponse(responseContext, res);
        }
    }

    private void setGatewayHelperFailureResponse(ResponseContext responseContext,
                                                 HttpServletResponse res) throws IOException {
        int statusCode = responseContext.getHttpStatus().value();
        res.setCharacterEncoding("utf-8");
        res.setContentType("application/xhtml+xml");
        String requestStatus = responseContext.getRequestStatus();
        String requestCode = responseContext.getRequestCode();
        String requestMessage = responseContext.getRequestMessage();


        try (PrintWriter out = res.getWriter()) {
            if (statusCode == HttpStatus.FORBIDDEN.value()) {
                res.setStatus(GATEWAY_HELPER_NOT_PASS);
                String message = "<oauth><status>" + requestStatus
                        + "</status><code>" + requestCode + "</code><message>"
                        + requestMessage + "</message></oauth>";
                out.println(message);
            } else {
                String message = "<error><status>" + requestStatus
                        + "</status><code>" + requestCode + "</code><message>"
                        + requestMessage + "</message></error>";
                res.setStatus(HttpStatus.INTERNAL_SERVER_ERROR.value());
                out.println(message);
            }
            out.flush();
        }
    }

    @Override
    public void destroy() {
        //此过滤取退出不需要关闭额外资源
    }
}
