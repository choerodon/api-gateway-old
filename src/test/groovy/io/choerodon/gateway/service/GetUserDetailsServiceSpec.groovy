package io.choerodon.gateway.service


import io.choerodon.gateway.config.GatewayProperties
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.service.impl.GetUserDetailsServiceImpl
import org.springframework.http.HttpEntity
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.client.RestClientException
import org.springframework.web.client.RestTemplate
import spock.lang.Specification

class GetUserDetailsServiceSpec extends Specification {

    def '测试getUserDetails方法'() {
        given: 'mock RestTemplate'

        def getUserDetailsService = new GetUserDetailsServiceImpl(new MockOkRestTemplate(), new GatewayProperties())

        when: '当oauth返回2XX时'
        def result = getUserDetailsService.getUserDetails('')

        then: '验证结果'
        result.customUserDetails != null
        result.customUserDetails.admin
        result.customUserDetails.userId == 23


        when: '当oauth返回token不合法时'
        getUserDetailsService = new GetUserDetailsServiceImpl(new MockInValidTokenRestTemplate(), new GatewayProperties())
        def result1 = getUserDetailsService.getUserDetails('')

        then: '验证结果'
        result1.customUserDetails == null
        result1.state == CheckState.PERMISSION_GET_USE_DETAIL_FAILED


        when: '当oauth返回token过期时'
        getUserDetailsService = new GetUserDetailsServiceImpl(new MockExpiredTokenRestTemplate(), new GatewayProperties())
        def result2 = getUserDetailsService.getUserDetails('')

        then: '验证结果'
        result2.customUserDetails == null
        result2.state == CheckState.PERMISSION_GET_USE_DETAIL_FAILED
    }

    class MockOkRestTemplate extends RestTemplate {

        @Override
        def ResponseEntity<String> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<String> responseType, Object... uriVariables) throws RestClientException {
            return new ResponseEntity<String>('{"oauth2Request": {"grantType": "client_credentials"},\t"principal": {"username": "admin","userId": 23,"language": "ZH","admin": true,"timeZone": "timeZone","organizationId": 23,"email": "email","clientId": 1,"clientName": "client","additionInfo": {}}}', HttpStatus.OK)
        }

    }

    class MockInValidTokenRestTemplate extends RestTemplate {
        @Override
        def ResponseEntity<String> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<String> responseType, Object... uriVariables) throws RestClientException {
            return new ResponseEntity<String>('{"error":"invalid_token","error_description":"Invalid access token Bear 123"}', HttpStatus.FORBIDDEN)
        }
    }

    class MockExpiredTokenRestTemplate extends RestTemplate {
        @Override
        def ResponseEntity<String> exchange(String url, HttpMethod method, HttpEntity<?> requestEntity, Class<String> responseType, Object... uriVariables) throws RestClientException {
            return new ResponseEntity<String>('{"error":"invalid_token","error_description":"Access token expired Bear 123"}', HttpStatus.FORBIDDEN)
        }
    }


}