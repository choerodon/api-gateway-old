package io.choerodon.gateway.service;


import io.choerodon.gateway.domain.CustomUserDetailsWithResult;

public interface GetUserDetailsService {

    CustomUserDetailsWithResult getUserDetails(String accessToken);

}
