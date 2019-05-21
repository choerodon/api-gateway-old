package io.choerodon.gateway.filter.route;

import com.netflix.zuul.ZuulFilter;
import com.netflix.zuul.context.RequestContext;
import com.netflix.zuul.exception.ZuulException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.cloud.netflix.zuul.util.ZuulRuntimeException;
import org.springframework.stereotype.Component;
import org.springframework.util.ReflectionUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE;

@Component
public class CustomErrorZuulFilter extends ZuulFilter {

    private static final Logger LOGGER = LoggerFactory.getLogger("CustomErrorZuulFilter");

    private static final String SEND_ERROR_FILTER_RAN = "sendErrorFilter.ran";

    @Override
    public String filterType() {
        return ERROR_TYPE;
    }

    @Override
    public int filterOrder() {
        return -10;
    }

    @Override
    public boolean shouldFilter() {
        RequestContext ctx = RequestContext.getCurrentContext();
        return ctx.getThrowable() != null
                && !ctx.getBoolean(SEND_ERROR_FILTER_RAN, false);
    }

    @Override
    public Object run() {
        try {
            RequestContext ctx = RequestContext.getCurrentContext();
            ctx.set(SEND_ERROR_FILTER_RAN);
            ctx.setResponseStatusCode(500);
            ctx.setResponseBody("forward service error");
            ZuulException exception = findZuulException(ctx.getThrowable());
            HttpServletRequest request = ctx.getRequest();
            request.setAttribute("javax.servlet.error.status_code", exception.nStatusCode);
            LOGGER.warn("Error during filtering", exception);
            request.setAttribute("javax.servlet.error.exception", exception);
            if (StringUtils.hasText(exception.errorCause)) {
                request.setAttribute("javax.servlet.error.message", exception.errorCause);
            }
        } catch (Exception e) {
            ReflectionUtils.rethrowRuntimeException(e);
        }
        return null;
    }


    ZuulException findZuulException(Throwable throwable) {
        if (throwable.getCause() instanceof ZuulRuntimeException) {
            return (ZuulException) throwable.getCause().getCause();
        }

        if (throwable.getCause() instanceof ZuulException) {
            return (ZuulException) throwable.getCause();
        }

        if (throwable instanceof ZuulException) {
            return (ZuulException) throwable;
        }
        return new ZuulException(throwable, HttpServletResponse.SC_INTERNAL_SERVER_ERROR, null);
    }

}
