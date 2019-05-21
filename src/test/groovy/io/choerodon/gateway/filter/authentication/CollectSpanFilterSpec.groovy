package io.choerodon.gateway.filter.authentication

import io.choerodon.gateway.domain.CheckRequest
import io.choerodon.gateway.domain.CheckResponse
import io.choerodon.gateway.domain.PermissionDTO
import io.choerodon.gateway.domain.RequestContext
import org.springframework.cloud.config.client.ZuulRoute
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.core.ValueOperations
import spock.lang.Specification

class CollectSpanFilterSpec extends Specification {

    StringRedisTemplate stringRedisTemplate = Mock(StringRedisTemplate)

    CollectSpanFilter collectSpanFilter = new CollectSpanFilter(stringRedisTemplate)

    def "FilterOrder"() {
        when:
        def result = collectSpanFilter.filterOrder()
        then:
        result == 25
    }

    def "ShouldFilter"() {
        when:
        def result = collectSpanFilter.shouldFilter()
        then:
        result == true
    }

    def "Run"() {
        given:
        CheckRequest request = new CheckRequest(null, "url", "get")
        RequestContext requestContext = new RequestContext(request, Mock(CheckResponse))
        ZuulRoute route = Mock(ZuulRoute)
        requestContext.setRoute(route)
        PermissionDTO permissionDTO = Mock(PermissionDTO)
        requestContext.setPermission(permissionDTO)
        permissionDTO.getPath() >> "/v1/users/self"
        route.getServiceId() >> "iam-service"

        stringRedisTemplate.hasKey(_) >> false
        stringRedisTemplate.opsForValue() >> Mock(ValueOperations)

        when:
        def result = collectSpanFilter.run(requestContext)

        then:
        result == true

    }
}
