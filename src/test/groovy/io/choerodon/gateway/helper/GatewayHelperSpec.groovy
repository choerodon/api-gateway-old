package io.choerodon.gateway.helper

import io.choerodon.gateway.domain.CheckRequest
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.RequestContext
import io.choerodon.gateway.domain.ResponseContext
import io.choerodon.gateway.filter.authentication.HelperFilter
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest


@PrepareForTest(AuthenticationHelper.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class GatewayHelperSpec extends Specification {


    def "Authentication"() {
        given:
        HttpServletRequest request = Mock(HttpServletRequest)
        request.getMethod() >> "get"
        CheckRequest checkRequest = Mock(CheckRequest)
        CheckResponse checkResponse = Mock(CheckResponse)
        RequestContext requestContext = new RequestContext(checkRequest, checkResponse)
        PowerMockito.whenNew(RequestContext.class).withAnyArguments().thenReturn(requestContext)

        and:
        Optional<List<HelperFilter>> list = Optional.empty()
        AuthenticationHelper gatewayHelper = new AuthenticationHelper(list)

        ResponseContext responseContext = null

        when:
        responseContext = gatewayHelper.authentication(request)

        then:
        3 * checkResponse.getStatus() >> CheckState.SUCCESS_PASS_SITE
        2 * checkResponse.getJwt() >> "jwtToken"
        responseContext.getRequestCode() == "success.permission.sitePass"

        when:
        responseContext = gatewayHelper.authentication(request)

        then:
        4 * checkResponse.getStatus() >> CheckState.PERMISSION_MISMATCH
        responseContext.getRequestCode() == "error.permission.mismatch"

        when:
        responseContext = gatewayHelper.authentication(request)

        then:
        4 * checkResponse.getStatus() >> CheckState.API_ERROR_MATCH_MULTIPLY
        responseContext.getRequestCode() == "error.api.matchMultiplyPermission"

    }
}
