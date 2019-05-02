package io.choerodon.gateway.cache.multi

import io.choerodon.gateway.cache.l1.caffeine.CaffeineL1Cache
import io.choerodon.gateway.cache.l2.redis.RedisL2Cache
import org.springframework.cache.Cache
import spock.lang.Specification

import java.util.concurrent.Callable

class MultiAllCacheSpec extends Specification {

    def l1Cache = Mock(Cache)

    def l2Cache = Mock(Cache)

    def multiAllCache = new MultiAllCache('', new CaffeineL1Cache(l1Cache), new RedisL2Cache(l2Cache))


    def "test get"() {
        when:
        multiAllCache.get('key')

        then:
        1 * l1Cache.get(_)
        1 * l2Cache.get(_)
    }

    def "test get 2"() {
        when:
        multiAllCache.get("key", String)

        then:
        1 * l1Cache.get(_, String)
        1 * l2Cache.get(_, String)
    }

    def "test get 3"() {
        when:
        def result = multiAllCache.get("key", Mock(Callable))

        then:
        result == null
    }

    def "test put"() {
        when:
        multiAllCache.put("key", "value")

        then:
        1 * l1Cache.put(_, _)
        1 * l2Cache.put(_, _)
    }

    def "test put If Absent"() {
        when:
        multiAllCache.putIfAbsent("key", "value")

        then:
        1 * l1Cache.putIfAbsent(_, _)
        1 * l2Cache.putIfAbsent(_, _)
    }

    def "test evict"() {
        when:
        multiAllCache.evict("key")

        then:
        1 * l1Cache.evict(_)
        1 * l2Cache.evict(_)
    }

    def "test clear"() {
        when:
        multiAllCache.clear()

        then:
        1 * l1Cache.clear()
        1 * l2Cache.clear()
    }

    def "test get Native Cache"() {
        when:
        Object result = multiAllCache.getNativeCache()

        then:
        result == multiAllCache
    }
}