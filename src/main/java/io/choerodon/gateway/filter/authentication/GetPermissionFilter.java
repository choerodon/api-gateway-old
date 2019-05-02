package io.choerodon.gateway.filter.authentication;

import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.domain.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.service.PermissionService;
import org.springframework.stereotype.Component;

/**
 * 根据接口uri，method和service获取匹配到的权限
 * 匹配不到或者接口类型为内部接口，返回失败，不再向下执行
 */
@Component
public class GetPermissionFilter implements HelperFilter {

    private PermissionService permissionService;

    public GetPermissionFilter(PermissionService permissionService) {
        this.permissionService = permissionService;
    }

    @Override
    public int filterOrder() {
        return 20;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        String key = context.getRequestKey();
        PermissionDTO permission = permissionService.selectPermissionByRequest(key);
        if (permission == null) {
            context.response.setStatus(CheckState.PERMISSION_MISMATCH);
            context.response.setMessage("This request mismatch any permission");
            return false;
        } else if (permission.getWithin()) {
            context.response.setStatus(CheckState.PERMISSION_WITH_IN);
            context.response.setMessage("No access to within interface");
            return false;
        } else {
            context.setPermission(permission);
        }
        return true;
    }

}
