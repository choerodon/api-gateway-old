package io.choerodon.gateway.filter.route

import io.choerodon.gateway.IntegrationTestConfiguration
import io.choerodon.gateway.config.GatewayProperties
import io.choerodon.gateway.domain.ResponseContext
import io.choerodon.gateway.helper.AuthenticationHelper
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer
import org.springframework.context.annotation.Import
import org.springframework.http.HttpStatus
import spock.lang.Specification
import spock.lang.Stepwise

import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class GateWayHelperFilterSpec extends Specification {

    @Autowired
    private GatewayProperties gatewayHelperProperties

    private List<RibbonRequestCustomizer> requestCustomizers = Mock(List)


    @Autowired
    private TestRestTemplate testRestTemplate

    def "Init"() {
    }

    def "DoFilter"() {
        given:
        AuthenticationHelper gatewayHelper = Mock(AuthenticationHelper)
        GateWayHelperFilter gateWayHelperFilter = new GateWayHelperFilter(gatewayHelper)

        and:
        HttpServletRequest request = Mock(HttpServletRequest)
        HttpServletResponse response = Mock(HttpServletResponse)
        FilterChain chain = Mock(FilterChain)
        ResponseContext responseContext = Mock(ResponseContext)

        when:
        gateWayHelperFilter.doFilter(request, response, chain)

        then:
        1 * gatewayHelper.authentication(_) >> responseContext
        2 * responseContext.getHttpStatus() >> HttpStatus.INTERNAL_SERVER_ERROR
        1 * responseContext.getRequestStatus() >> ""
        1 * responseContext.getRequestCode() >> ""
        1 * responseContext.getRequestMessage() >> ""
        1 * response.getWriter() >> Mock(PrintWriter)
    }

    def "Destroy"() {
    }
}
