package io.choerodon.gateway.filter

import com.netflix.hystrix.exception.HystrixRuntimeException
import io.choerodon.gateway.IntegrationTestConfiguration
import io.choerodon.gateway.config.GatewayHelperProperties
import io.choerodon.gateway.my.MyClientHttpResponse
import io.choerodon.gateway.my.MyRibbonCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommand
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory
import org.springframework.context.annotation.Import
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
    private GatewayHelperProperties gatewayHelperProperties

    @Autowired
    private List<RibbonRequestCustomizer> requestCustomizers


    @Autowired
    private TestRestTemplate testRestTemplate

    def "Init"() {
    }

    def "DoFilter"() {

        given: "测试200情况"
        def myClientHttpResponse = new MyClientHttpResponse(200)
        def myRibbonCommand = new MyRibbonCommand(myClientHttpResponse)
        def ribbonCommandFactory = Mock(RibbonCommandFactory)
        def command = Mock(RibbonCommand)
        def exception = Mock(HystrixRuntimeException)
        command.execute() >> { throw exception}

        and: "构造request.getHeaderNames()返回对象"
        Set<String> set = new HashSet<>()
        set << "Accept"
        set << "Jwt_Token"
        set << "Host"
        def headerNames = Collections.enumeration(set)

        and: "构造GateWayHelperFilter"
        def gateWayHelperFilter = new GateWayHelperFilter(gatewayHelperProperties, requestCustomizers, ribbonCommandFactory)

        and: "构造doFilter参数"
        def request = Mock(HttpServletRequest)
        def response = Mock(HttpServletResponse)
        def chain = Mock(FilterChain)


        when: "调用doFilter"
        gateWayHelperFilter.doFilter(request, response, chain)

        then: "判断成功"
        _ * request.getRequestURI() >> "/manager/v1/swaggers/resources"
        _ * request.getMethod() >> "GET"
        _ * request.getHeaderNames() >> headerNames
        _ * request.getHeaders(_) >> headerNames
        _ * ribbonCommandFactory.create(_) >> myRibbonCommand
        1 * chain.doFilter(request, response)

        when: "401情况"
        myClientHttpResponse.setStatusCode(401)
        gateWayHelperFilter.doFilter(request, response, chain)

        then: "执行"
        _ * request.getRequestURI() >> "/manager/v1/swaggers/resources"
        _ * request.getMethod() >> "GET"
        _ * ribbonCommandFactory.create(_) >> myRibbonCommand
        _ * response.getWriter() >> Mock(PrintWriter)
        0 * chain.doFilter(request, response)

        when: "直接跳过的服务"
        myClientHttpResponse.setStatusCode(200)
        gateWayHelperFilter.doFilter(request, response, chain)

        then: "执行"
        _ * request.getRequestURI() >> "/manager/swagger-ui.html"
        _ * request.getMethod() >> "GET"
        1 * chain.doFilter(request, response)


        when: "抛异常情况"
        gateWayHelperFilter.doFilter(request, response, chain)

        then: "执行"
        1 * ribbonCommandFactory.create(_) >> command
        _ * request.getRequestURI() >> "/manager/v1/swaggers/resources"
        _ * request.getMethod() >> "GET"
        1 * response.getWriter() >> Mock(PrintWriter)


    }

    def "Destroy"() {
    }
}
