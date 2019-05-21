package io.choerodon.gateway.filter.authentication

import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.domain.RequestContext
import io.choerodon.gateway.service.PermissionService
import spock.lang.Specification

class GetPermissionFilterSpec extends Specification {

    def "test filter Order"() {
        when:
        int result = new GetPermissionFilter(null).filterOrder()

        then:
        result == 20
    }

    def "test should Filter"() {
        when:
        boolean result = new GetPermissionFilter(null).shouldFilter(null)

        then:
        result
    }

    def "test run"() {
        given: 'Mock PermissionService'
        def permission = new PermissionDTO()
        permission.setWithin(false)
        def noPermissionService = Mock(PermissionService)
        def hasPermissionService = Mock(PermissionService) {
            selectPermissionByRequest(_) >> permission
        }
        def getPermissionFilter = new GetPermissionFilter(noPermissionService)
        def context = new RequestContext(null, new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))
        context.setRequestKey("aa")

        when: '无权限匹配'
        boolean result = getPermissionFilter.run(context)

        then:
        context.response.status == CheckState.PERMISSION_MISMATCH
        !result

        when: '权限匹配'
        getPermissionFilter = new GetPermissionFilter(hasPermissionService)
        boolean result1 = getPermissionFilter.run(context)

        then:
        result1

        when: '匹配内部接口'
        permission.setWithin(true)
        boolean result2 = getPermissionFilter.run(context)

        then:
        !result2
        context.response.status == CheckState.PERMISSION_WITH_IN
    }
}