package io.choerodon.gateway

import io.choerodon.gateway.mapper.PermissionMapper
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Primary
import org.springframework.data.redis.connection.RedisConnectionFactory
import spock.mock.DetachedMockFactory

import javax.annotation.PostConstruct

/**
 * @author superlee
 */

@TestConfiguration
class IntegrationTestConfiguration {

    private final detachedMockFactory = new DetachedMockFactory()

    @PostConstruct
    void init() {
    }

    @Bean
    PermissionMapper permissionMapper() {
        detachedMockFactory.Mock(PermissionMapper)
    }

    @Bean
    @Primary
    RedisConnectionFactory redisConnectionFactory() {
        detachedMockFactory.Mock(RedisConnectionFactory)
    }

}