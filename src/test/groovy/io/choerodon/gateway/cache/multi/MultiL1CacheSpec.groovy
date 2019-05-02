package io.choerodon.gateway.cache.multi

import io.choerodon.gateway.cache.l1.caffeine.CaffeineL1Cache
import org.springframework.cache.Cache
import org.springframework.cache.support.SimpleValueWrapper
import spock.lang.Specification

import java.util.concurrent.Callable

class MultiL1CacheSpec extends Specification {

    def l1Cache = Mock(Cache) {
        get(_) >> new SimpleValueWrapper('')
        get(_, _) >> ''
    }

    def multiL1Cache = new MultiL1Cache('', new CaffeineL1Cache(l1Cache))

    def "test get"() {
        when:
        Cache.ValueWrapper result = multiL1Cache.get("key")

        then:
        result != null
    }

    def "test get 2"() {
        when:
        String result = multiL1Cache.get("key", String)

        then:
        result != null
    }

    def "test get 3"() {
        when:
        Object result = multiL1Cache.get("key", Mock(Callable))

        then:
        result != null
    }

    def "test put"() {
        when:
        multiL1Cache.put("key", "value")

        then:
        1 * l1Cache.put(_, _)
    }

    def "test put If Absent"() {
        when:
        multiL1Cache.putIfAbsent("key", "value")

        then:
        1 * l1Cache.putIfAbsent(_, _)
    }

    def "test evict"() {
        when:
        multiL1Cache.evict("key")

        then:
        1 * l1Cache.evict(_)
    }

    def "test clear"() {
        when:
        multiL1Cache.clear()

        then:
        1 * l1Cache.clear()
    }

    def "test get Native Cache"() {
        when:
        Object result = multiL1Cache.getNativeCache()

        then:
        result == multiL1Cache
    }
}