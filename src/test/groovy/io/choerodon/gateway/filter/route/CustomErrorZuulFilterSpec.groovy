package io.choerodon.gateway.filter.route

import com.netflix.zuul.context.RequestContext
import com.netflix.zuul.exception.ZuulException
import com.netflix.zuul.monitoring.CounterFactory
import io.choerodon.gateway.filter.route.CustomErrorZuulFilter
import org.junit.runner.RunWith
import org.powermock.api.mockito.PowerMockito
import org.powermock.core.classloader.annotations.PrepareForTest
import org.powermock.modules.junit4.PowerMockRunner
import org.powermock.modules.junit4.PowerMockRunnerDelegate
import org.spockframework.runtime.Sputnik
import org.springframework.cloud.netflix.zuul.metrics.EmptyCounterFactory
import spock.lang.Specification

import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

import static org.springframework.cloud.netflix.zuul.filters.support.FilterConstants.ERROR_TYPE

@PrepareForTest(RequestContext.class)
@RunWith(PowerMockRunner.class)
@PowerMockRunnerDelegate(Sputnik.class)
class CustomErrorZuulFilterSpec extends Specification {

    def zuulFilter = new CustomErrorZuulFilter()

    def 'filterType'() {
        when: '调用filterType'
        String result = zuulFilter.filterType()

        then: '验证type'
        result == ERROR_TYPE
    }

    def 'filterOrder'() {
        when: '调用filterOrder'
        int order = zuulFilter.filterOrder()

        then: '验证order'
        order == -10
    }

    def 'shouldFilter'() {
        given: 'mock'
        def request = Mock(HttpServletRequest)
        CounterFactory.initialize(new EmptyCounterFactory())
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext) {
            getThrowable() >> new ZuulException(new RuntimeException(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "message")
            getBoolean("sendErrorFilter.ran", false) >> false
            getRequest() >> request
        }
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)

        when: '调用shouldFilter'
        boolean shouldFilter = zuulFilter.shouldFilter()

        then: '验证结果'
        shouldFilter
    }

    def 'run'() {
        given: 'mock'
        def request = Mock(HttpServletRequest)
        CounterFactory.initialize(new EmptyCounterFactory())
        PowerMockito.mockStatic(RequestContext.class)
        def ctx = Mock(RequestContext) {
            getThrowable() >> new ZuulException(new RuntimeException(),
                    HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "message")
            getBoolean("sendErrorFilter.ran", false) >> false
            getRequest() >> request
        }
        PowerMockito.when(RequestContext.getCurrentContext()).thenReturn(ctx)

        when: '调用run方法'
        zuulFilter.run()

        then: '验证method调用'
        3 * request.setAttribute(_, _)
    }

}
