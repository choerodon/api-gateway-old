package io.choerodon.gateway.service

import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.mapper.PermissionMapper
import io.choerodon.gateway.service.impl.PermissionServiceImpl
import spock.lang.Specification

class PermissionServiceSpec extends Specification {

    def ''() {
        given: '创建HelperProperties和PermissionMapper'
        PermissionDTO permission1 = new PermissionDTO()
        permission1.setPath("/v1/projects/{id}")
        PermissionDTO permission2 = new PermissionDTO()
        permission2.setPath("/v1/projects/name")
        def permissionMapper = Mock(PermissionMapper) {
            selectPermissionByMethodAndService(_, _) >> Arrays.asList(permission1, permission2)
        }
        def permissionService = new PermissionServiceImpl(permissionMapper)

        when: '权限匹配，不开启multiplyMatch校验'
        def permission3 = permissionService.selectPermissionByRequest("/v1/projects/name:::get:::iam-service")

        then: '验证permission不为空'
        permission3 != null
    }
}