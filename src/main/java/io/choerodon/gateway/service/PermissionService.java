package io.choerodon.gateway.service;


import io.choerodon.gateway.dto.PermissionDTO;

public interface PermissionService {

    PermissionDTO selectPermissionByRequest(String requestKey);

}
