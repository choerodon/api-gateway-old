package io.choerodon.gateway.filter.route

import com.netflix.zuul.context.RequestContext
import io.choerodon.gateway.config.GatewayProperties
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito

import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik

import spock.lang.Specification

import javax.servlet.http.HttpServletRequest

/**
 * Created by superlee on 2018/9/19.
 */
@PrepareForTest(RequestContext.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class HeaderWrapperFilterSpec extends Specification {

    def gatewayHelperProperties = Mock(GatewayProperties) {
        isEnabledJwtLog() >> true
    }
    def headerWrapperFilter = new HeaderWrapperFilter(gatewayHelperProperties)

    def "FilterType"() {
        when: '调用filterType'
        def type = headerWrapperFilter.filterType()
        then: '验证'
        type == 'pre'
    }

    def "FilterOrder"() {
        when: '调用filterOrder'
        def order = headerWrapperFilter.filterOrder()
        then: '验证'
        order == -1
    }

    def "ShouldFilter"() {
        when: '调用shouldFilter'
        def shouldFilter = headerWrapperFilter.shouldFilter()
        then: '验证'
        shouldFilter
    }

    def "Run"() {
        given: ""
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext)
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)
        def request = Mock(HttpServletRequest)

        when: ""
        def value = headerWrapperFilter.run()

        then: ""
        1 * ctx.getRequest() >> request
        1 * request.getAttribute(_) >> "token"
        value == null
    }
}
