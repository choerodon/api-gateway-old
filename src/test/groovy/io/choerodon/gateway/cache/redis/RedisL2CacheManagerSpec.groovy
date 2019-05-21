package io.choerodon.gateway.cache.redis

import io.choerodon.gateway.cache.l2.redis.RedisL2CacheManager
import org.springframework.data.redis.cache.RedisCache
import org.springframework.data.redis.connection.RedisConnectionFactory
import spock.lang.Specification

class RedisL2CacheManagerSpec extends Specification {

    def "test type"() {
        when:
        String result = RedisL2CacheManager.type()

        then:
        result == "redis"
    }

    def "test get L2 Cache"() {
        when:
        def result = new RedisL2CacheManager(Mock(RedisConnectionFactory)).getL2Cache("name", "expiration=1800")

        then:
        result != null
        result.getCache() instanceof RedisCache
    }
}
