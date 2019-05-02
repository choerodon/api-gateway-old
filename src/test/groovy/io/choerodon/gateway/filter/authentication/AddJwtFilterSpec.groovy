package io.choerodon.gateway.filter.authentication

import io.choerodon.core.oauth.CustomUserDetails
import io.choerodon.gateway.IntegrationTestConfiguration
import io.choerodon.gateway.domain.CheckRequest
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.RequestContext
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.security.jwt.crypto.sign.Signer
import spock.lang.Specification

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
class AddJwtFilterSpec extends Specification {

    @Autowired
    Signer jwtSigner

    def "test filter Order"() {
        when:
        int result = new AddJwtFilter(null).filterOrder()

        then:
        result == 50
    }

    def "test should Filter"() {
        when:
        boolean result = new AddJwtFilter(null).shouldFilter(new RequestContext(
                new CheckRequest("accessToken", "uri", "method"),
                new CheckResponse()))

        then:
        result
    }

    def "test run"() {
        given:
        def context = new RequestContext(
                new CheckRequest("accessToken", "uri", "method"),
                new CheckResponse(jwt: "jwt", message: "message", status: CheckState.SUCCESS_PASS_SITE))
        context.setCustomUserDetails(new CustomUserDetails('user', 'pass', Collections.emptyList()))
        def addJwtFilter = new AddJwtFilter(jwtSigner)
        when:
        boolean result = addJwtFilter.run(context)

        then:
        result
        context.response.jwt != null
    }
}