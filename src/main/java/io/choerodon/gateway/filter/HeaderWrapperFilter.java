package io.choerodon.gateway.filter;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;

import javax.servlet.http.HttpServletRequest;

import static io.choerodon.core.variable.RequestVariableHolder.HEADER_JWT;
import static io.choerodon.core.variable.RequestVariableHolder.HEADER_LABEL;
import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.PRE_TYPE;

/**
 * 添加token和label到请求header
 *
 * @author zhipeng.zuo
 */
public class HeaderWrapperFilter extends ZuulFilter {

    private static final int HEADER_WRAPPER_FILTER = -1;

    @Override
    public String filterType() {
        return PRE_TYPE;
    }

    @Override
    public int filterOrder() {
        return HEADER_WRAPPER_FILTER;
    }

    @Override
    public boolean shouldFilter() {
        return true;
    }

    @Override
    public Object run() {
        RequestContext ctx = RequestContext.getCurrentContext();
        HttpServletRequest request = ctx.getRequest();
        String token = (String) request.getAttribute(HEADER_JWT);
        String label = (String) request.getAttribute(HEADER_LABEL);
        if (token != null) {
            ctx.addZuulRequestHeader(HEADER_JWT, token);
        }
        if (label != null) {
            ctx.addZuulRequestHeader(HEADER_LABEL, label);
        }
        return null;
    }
}
