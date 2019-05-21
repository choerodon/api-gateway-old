package io.choerodon.gateway.cache.multi

import io.choerodon.gateway.cache.MultiCacheProperties
import io.choerodon.gateway.cache.l1.L1CacheManager
import io.choerodon.gateway.cache.l1.caffeine.CaffeineL1Cache
import io.choerodon.gateway.cache.l2.L2CacheManager
import io.choerodon.gateway.cache.l2.redis.RedisL2Cache
import org.springframework.cache.support.NoOpCache
import spock.lang.Specification

class MultiCacheManagerSpec extends Specification {

    def "test get Cache"() {
        given: 'mock l1CacheManager 和 l2CacheManager'
        def l1CacheManager = Mock(L1CacheManager) {
            getL1Cache(_,_) >> new CaffeineL1Cache(null)
        }
        def l2CacheManager = Mock(L2CacheManager) {
            getL2Cache(_,_) >> new RedisL2Cache(null)
        }
        def properties = new MultiCacheProperties()
        properties.setCaches(new HashMap<String, MultiCacheProperties.Cache>())

        when: '都为null'
        def allNullManager = new MultiCacheManager(null, null, properties)
        def allNull = allNullManager.getCache('allNull')
        then:
        allNull instanceof NoOpCache
        allNullManager.getCacheNames().contains('allNull')

        when: 'L1CacheManager 为null'
        def l1Null = new MultiCacheManager(null, l2CacheManager, properties).getCache('l1Null')
        then:
        l1Null instanceof MultiL2Cache

        when: 'L2CacheManager 为null'
        def l2Null = new MultiCacheManager(l1CacheManager, null, properties).getCache('l2Null')
        then:
        l2Null instanceof MultiL1Cache

        when: 'L1CacheManager和L2CacheManager不为null, l1 enabled'
        def l1EnabledConfig = new MultiCacheProperties.Cache()
        l1EnabledConfig.setL2Enabled(false)
        properties.getCaches().put('l1Enabled', l1EnabledConfig)
        def manager = new MultiCacheManager(l1CacheManager, l2CacheManager, properties)
        def l1Enabled = manager.getCache('l1Enabled')
        then:
        l1Enabled instanceof MultiL1Cache

        when: 'L1CacheManager和L2CacheManager不为null, l2 enabled'
        def l2EnabledConfig = new MultiCacheProperties.Cache()
        l2EnabledConfig.setL1Enabled(false)
        properties.getCaches().put('l2Enabled', l2EnabledConfig)
        def l2Enabled = manager.getCache('l2Enabled')
        then:
        l2Enabled instanceof MultiL2Cache

        when: 'L1CacheManager和L2CacheManager不为null, all enabled'
        def allEnabledConfig = new MultiCacheProperties.Cache()
        properties.getCaches().put('allEnabled', allEnabledConfig)
        def allEnabled = manager.getCache('allEnabled')
        then:
        allEnabled instanceof MultiAllCache
    }

    def "test get Cache Names"() {
        when:
        def names = new MultiCacheManager(null, null, null).cacheNames
        then:
        names != null
    }
}
