package io.choerodon.gateway.filter.authentication

import io.choerodon.gateway.domain.CheckRequest
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.domain.RequestContext
import spock.lang.Specification

class PublicRequestFilterSpec extends Specification {

    def "test filter Order"() {
        when: ''
        def result = new PublicRequestFilter().filterOrder()
        then: ''
        result == 30
    }

    def "test should Filter"() {
        when: ''
        def context = new RequestContext(null, null)
        def permission = new PermissionDTO()
        permission.setPublicAccess(true)
        context.setPermission(permission)
        def result = new PublicRequestFilter().shouldFilter(context)
        then: ''
        result
    }

    def "test run"() {
        when: ''
        def context = new RequestContext(new CheckRequest(null, "/zuul/iam/test", "method"),
                new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))
        def result = new PublicRequestFilter().run(context)

        then: ''
        !result
        context.response.status == CheckState.SUCCESS_PUBLIC_ACCESS


    }
}
