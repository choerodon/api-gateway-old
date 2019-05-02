package io.choerodon.gateway.cache.caffeine

import io.choerodon.gateway.cache.l1.caffeine.CaffeineL1CacheManager
import org.springframework.cache.caffeine.CaffeineCache
import spock.lang.Specification

class CaffeineL1CacheManagerSpec extends Specification {

    def "test get L1 Cache"() {
        when: '调用getL1Cache'
        def result = new CaffeineL1CacheManager().getL1Cache("test", "initialCapacity=50,maximumSize=500,expireAfterWrite=600s")

        then:
        result != null
        result.getCache() instanceof CaffeineCache
        result.getCache().getName() == 'test'
    }
}