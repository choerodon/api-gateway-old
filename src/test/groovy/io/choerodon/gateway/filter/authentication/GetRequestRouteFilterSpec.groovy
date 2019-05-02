package io.choerodon.gateway.filter.authentication

import io.choerodon.gateway.domain.CheckRequest
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.CheckState
import io.choerodon.gateway.domain.RequestContext
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.cloud.config.helper.HelperZuulRoutesProperties
import spock.lang.Specification

class GetRequestRouteFilterSpec extends Specification {

    def "test filter Order"() {
        when:
        int result = new GetRequestRouteFilter(null).filterOrder()

        then:
        result == 10
    }

    def "test should Filter"() {
        when:
        boolean result = new GetRequestRouteFilter(null).shouldFilter(null)

        then:
        result
    }

    def "test run"() {
        given: ''
        def properties = new HelperZuulRoutesProperties()
        properties.setRoutes(new HashMap<String, ZuulRoute>(1))
        def getRequestRouteFilter = new GetRequestRouteFilter(properties)
        def context = new RequestContext(new CheckRequest(null, "/zuul/iam/test", "method"),
                new CheckResponse(message: "message", status: CheckState.SUCCESS_PASS_SITE))

        when: '当没有路由匹配时'
        boolean result = getRequestRouteFilter.run(context)
        then:
        context.response.status == CheckState.PERMISSION_SERVICE_ROUTE
        !result

        when: '当有路由匹配时'
        def iamRoute = new ZuulRoute()
        iamRoute.setId('iam')
        iamRoute.setPath('/iam/**')
        properties.getRoutes().put('iam', iamRoute)
        boolean result1 = getRequestRouteFilter.run(context)
        then:
        result1

    }
}