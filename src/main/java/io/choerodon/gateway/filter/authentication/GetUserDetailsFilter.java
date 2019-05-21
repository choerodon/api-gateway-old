package io.choerodon.gateway.filter.authentication;

import io.choerodon.gateway.domain.CheckState;
import io.choerodon.gateway.domain.CustomUserDetailsWithResult;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.service.GetUserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

/**
 * 根据access_token获取对应的userDetails
 */
@Component
public class GetUserDetailsFilter implements HelperFilter {

    private GetUserDetailsService getUserDetailsService;

    public GetUserDetailsFilter(GetUserDetailsService getUserDetailsService) {
        this.getUserDetailsService = getUserDetailsService;
    }

    @Override
    public int filterOrder() {
        return 40;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    @SuppressWarnings("unchecked")
    public boolean run(RequestContext context) {
        String accessToken = context.request.accessToken;
        if (StringUtils.isEmpty(accessToken)) {
            context.response.setStatus(CheckState.PERMISSION_ACCESS_TOKEN_NULL);
            context.response.setMessage("Access_token is empty, Please login and set access_token by HTTP header 'Authorization'");
            return false;
        }
        CustomUserDetailsWithResult result = getUserDetailsService.getUserDetails(accessToken);
        if (result.getCustomUserDetails() == null) {
            context.response.setStatus(result.getState());
            context.response.setMessage(result.getMessage());
            return false;
        }
        context.setCustomUserDetails(result.getCustomUserDetails());
        return true;
    }

}
