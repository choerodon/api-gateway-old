package io.choerodon.gateway.filter

import com.netflix.zuul.context.RequestContext
import io.choerodon.gateway.config.GatewayHelperProperties
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

    def "FilterType"() {
    }

    def "FilterOrder"() {
    }

    def "ShouldFilter"() {
    }

    def "Run"() {
        given: ""
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext)
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)
        def request = Mock(HttpServletRequest)
        def gatewayHelperProperties = Mock(GatewayHelperProperties)
        gatewayHelperProperties.setEnabledJwtLog(true)

        when: ""
        def headerWrapperFilter = new HeaderWrapperFilter(gatewayHelperProperties)
        def value = headerWrapperFilter.run()

        then: ""
        1 * ctx.getRequest() >> request
        1 * request.getAttribute(_) >> "token"
        value == null
    }
}
