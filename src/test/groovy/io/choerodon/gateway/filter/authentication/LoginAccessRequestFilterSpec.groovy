package io.choerodon.gateway.filter.authentication

import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.domain.RequestContext
import spock.lang.Specification

class LoginAccessRequestFilterSpec extends Specification {

    def "test filter Order"() {
        when: ''
        def result = new LoginAccessRequestFilter().filterOrder()
        then: ''
        result == 60
    }

    def "test should Filter"() {
        given: ''
        def context = new RequestContext(null, new CheckResponse())
        def userDetails = new CustomUserDetails('user', 'pass', Collections.emptyList())
        context.setCustomUserDetails(userDetails)
        def permission = new PermissionDTO()
        permission.setLoginAccess(true)
        context.setPermission(permission)

        when: ''
        def result = new LoginAccessRequestFilter().shouldFilter(context)
        then: ''
        result
    }

    def "test run"() {
        given: ''
        def context = new RequestContext(null, new CheckResponse())
        when: ''
        def result = new LoginAccessRequestFilter().run(context)
        then: ''
        !result
        context.response.status == CheckState.SUCCESS_LOGIN_ACCESS
    }
}
