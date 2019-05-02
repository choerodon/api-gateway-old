package io.choerodon.gateway.filter.authentication

import io.choerodon.core.iam.ResourceLevel
import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.domain.RequestContext
import io.choerodon.gateway.mapper.PermissionMapper
import spock.lang.Specification

class CommonRequestCheckFilterSpec extends Specification {

    def "test filter Order"() {
        when:
        int result = new CommonRequestCheckFilter(null).filterOrder()

        then:
        result == 80
    }

    def "test should Filter"() {
        when:
        boolean result = new CommonRequestCheckFilter(null).shouldFilter(null)

        then:
        result
    }

    def "test run"() {
        given: 'mock mapper, 设置userDetails和permission'
        def permission = new PermissionDTO()
        permission.setPath("v1/{project_id}")
        permission.setMethod("get")
        permission.setLoginAccess(false)
        permission.setPublicAccess(false)
        permission.setWithin(false)
        permission.setResourceLevel("project")
        permission.setId(0L)
        def emptyPermissionMapper = Mock(PermissionMapper) {
            selectSourceIdsByUserIdAndPermission(_,_, _, _) >> Collections.emptyList()
        }
        def matchPermissionMapper = Mock(PermissionMapper) {
            selectSourceIdsByUserIdAndPermission(_,_, _, _) >> Collections.singletonList(23L)
        }
        def commonRequestCheckFilter = new CommonRequestCheckFilter(emptyPermissionMapper)
        def context = new RequestContext(null, new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))
        context.setPermission(permission)
        def userDetails = new CustomUserDetails('user', 'pass', Collections.emptyList())
        userDetails.setUserId(1L)
        userDetails.setClientId(1L)
        context.setCustomUserDetails(userDetails)

        when: '无权限匹配时调用run'
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.PERMISSION_NOT_PASS

        when: '全局层权限匹配时调用run'

        commonRequestCheckFilter = new CommonRequestCheckFilter(matchPermissionMapper)
        permission.setResourceLevel(ResourceLevel.SITE.value())
        permission.setId(2L)
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.SUCCESS_PASS_SITE

        when: '项目层接口异常'
        permission.setResourceLevel(ResourceLevel.PROJECT.value())
        permission.setId(3L)
        permission.setPath('v1/projects')
        context.setTrueUri('v1/projects')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.API_ERROR_PROJECT_ID

        when: '项目层权限校验通过'
        permission.setResourceLevel(ResourceLevel.PROJECT.value())
        permission.setId(4L)
        permission.setPath('v1/projects/{project_id}')
        context.setTrueUri('v1/projects/23')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.SUCCESS_PASS_PROJECT

        when: '项目层权限校验不通过'
        context.setTrueUri('v1/projects/24')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.PERMISSION_NOT_PASS_PROJECT


        when: '组织层接口异常'
        permission.setResourceLevel(ResourceLevel.ORGANIZATION.value())
        permission.setId(5L)
        permission.setPath('v1/organizations')
        context.setTrueUri('v1/organizations')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.API_ERROR_ORG_ID

        when: '组织层权限校验通过'
        permission.setId(6L)
        permission.setPath('v1/organizations/{organization_id}')
        context.setTrueUri('v1/organizations/23')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.SUCCESS_PASS_ORG

        when: '组织层权限校验不通过'
        context.setTrueUri('v1/organizations/24')
        commonRequestCheckFilter.run(context)
        then: '验证response状态'
        context.response.status == CheckState.PERMISSION_NOT_PASS_ORG
    }
}