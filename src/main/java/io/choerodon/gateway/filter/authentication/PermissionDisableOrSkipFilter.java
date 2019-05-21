package io.choerodon.gateway.filter.authentication;

import io.choerodon.gateway.config.GatewayProperties;
import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.domain.RequestContext;
import org.springframework.stereotype.Component;
import org.springframework.util.AntPathMatcher;

/**
 * 是否启用权限校验和跳过权限校验路径的过滤器
 */
@Component
public class PermissionDisableOrSkipFilter implements HelperFilter {

    private final AntPathMatcher matcher = new AntPathMatcher();

    private GatewayProperties gatewayHelperProperties;

    public PermissionDisableOrSkipFilter(GatewayProperties gatewayHelperProperties) {
        this.gatewayHelperProperties = gatewayHelperProperties;
    }

    @Override
    public int filterOrder() {
        return 0;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        if (!gatewayHelperProperties.getPermission().getEnabled()) {
            context.response.setStatus(CheckState.SUCCESS_PERMISSION_DISABLED);
            context.response.setMessage("Permission check disabled");
            return false;
        }
        if (gatewayHelperProperties.getPermission().getSkipPaths().stream()
                .anyMatch(t -> matcher.match(t, context.request.uri))) {
            context.response.setStatus(CheckState.SUCCESS_SKIP_PATH);
            context.response.setMessage("This request match skipPath, skipPaths: " + gatewayHelperProperties.getPermission().getSkipPaths());
            return false;
        }
        return true;
    }
}
