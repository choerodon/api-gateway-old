package io.choerodon.gateway.filter

import io.choerodon.core.convertor.ApplicationContextHelper
import io.choerodon.gateway.IntegrationTestConfiguration
import io.choerodon.gateway.my.MyClientHttpResponse
import io.choerodon.gateway.my.MyRibbonCommand
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.web.client.TestRestTemplate
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory
import org.springframework.context.annotation.Import
import spock.lang.Specification
import spock.lang.Stepwise


import static org.springframework.boot.test.context.SpringBootTest.WebEnvironment.RANDOM_PORT

/**
 * Created by superlee on 2018/9/18.
 */
@SpringBootTest(webEnvironment = RANDOM_PORT)
@Import(IntegrationTestConfiguration)
@Stepwise
class GateWayHelperFilterSpec extends Specification {

    @Autowired
    private TestRestTemplate testRestTemplate

    def "Init"() {
    }

    def "DoFilter"() {

        given: "测试200情况"
        def myClientHttpResponse = new MyClientHttpResponse(200)
        def myRibbonCommand = new MyRibbonCommand(myClientHttpResponse)
        def ribbonCommandFactory = Mock(RibbonCommandFactory)
        def gateWayHelperFilter = ApplicationContextHelper.getSpringFactory().getBean(GateWayHelperFilter.class)
        gateWayHelperFilter.setRibbonCommandFactory(ribbonCommandFactory)

        when: "调用doFilter"
        def entity = testRestTemplate.getForEntity("/manager/v1/swaggers/resources", String)

        then: "判断成功"
        entity.statusCode.is2xxSuccessful()
        2 * ribbonCommandFactory.create(_) >> myRibbonCommand


//        when: "测试403情况"
//        myClientHttpResponse.setStatusCode(403)
//        def entity1 = testRestTemplate.getForEntity("/manager/v1/swaggers/resources", String)
//
//        then: "判断成功"
//        1 * ribbonCommandFactory.create(_) >> myRibbonCommand
//        entity1.statusCode.is4xxClientError()


    }

    def "Destroy"() {
    }
}
