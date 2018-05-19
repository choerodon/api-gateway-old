package io.choerodon.gateway.filter;

import io.choerodon.core.variable.RequestVariableHolder;
import io.choerodon.gateway.config.GatewayHelperProperties;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.Arrays;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_LABEL;

/**
 * @author flyleft
 * @date 2018/3/23
 */
public class TempSkipPermissionGateWayHelperFilter implements Filter {

    private GatewayHelperProperties gatewayHelperProperties;

    private final AntPathMatcher matcher = new AntPathMatcher();

    public TempSkipPermissionGateWayHelperFilter(GatewayHelperProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    private static final String JWT = "Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJ1c2VyX25hbWUiOiJhZG1pbiIsInVzZXJJ"
            + "ZCI6MSwibGFuZ3VhZ2UiOiJ6aF9DTiIsInRpbWVab25lIjoiQ1RUIiwiZW1haWwiOiJqY2FsYXpANjEzLmNvbSIsIm9yZ2FuaXphdGlvbk"
            + "lkIjoxfQ.yMMLwAKWUUXMA8_Wpb0Tby45JyHd0EgC9DNEJAnl2oE";

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        //do nothing but for implement abstract method
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest req = (HttpServletRequest) request;
        boolean shouldSkipHelper = Arrays.stream(gatewayHelperProperties.getHelperSkipPaths())
                .anyMatch(t -> matcher.match(t, req.getRequestURI()));
        if (!shouldSkipHelper) {
            req.setAttribute(RequestVariableHolder.HEADER_JWT, JWT);
            req.setAttribute(HEADER_LABEL, "");
        }
        chain.doFilter(req, response);
    }

    @Override
    public void destroy() {
        //do nothing only for implement abstract method
    }
}
