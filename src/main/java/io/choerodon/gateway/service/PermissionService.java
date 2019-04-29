package io.choerodon.gateway.service;


import io.choerodon.gateway.domain.PermissionDTO;

public interface PermissionService {

    PermissionDTO selectPermissionByRequest(String requestKey);

}
