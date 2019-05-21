package io.choerodon.gateway.filter.authentication

import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.RequestContext
import spock.lang.Specification

class AdminUserPermissionFilterSpec extends Specification {

    def adminUserPermissionFilter = new AdminUserPermissionFilter()

    def "test filter Order"() {
        when:
        int result = adminUserPermissionFilter.filterOrder()

        then:
        result == 70
    }

    def "test should Filter"() {
        given:
        def context = new RequestContext(null, new CheckResponse())
        def userDetails = new CustomUserDetails('user','pass',Collections.emptyList())
        userDetails.setAdmin(true)
        context.setCustomUserDetails(userDetails)

        when:
        boolean result = adminUserPermissionFilter.shouldFilter(context)

        then:
        result
    }

    def "test run"() {
        given:
        def context = new RequestContext(null, new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))
        def userDetails = new CustomUserDetails('user','pass',Collections.emptyList())
        context.setCustomUserDetails(userDetails)

        when:
        boolean result = adminUserPermissionFilter.run(context)

        then:
        !result
        context.response.status == CheckState.SUCCESS_ADMIN
    }
}