package io.choerodon.gateway.filter.authentication;

import io.choerodon.gateway.domain.PermissionDTO;
import io.choerodon.gateway.domain.RequestContext;
import io.choerodon.gateway.domain.TranceSpan;
import org.springframework.cloud.config.client.ZuulRoute;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;
import rx.Observable;
import rx.schedulers.Schedulers;

import java.time.LocalDate;
import java.util.concurrent.TimeUnit;


/**
 * @author superlee
 */
@Component
public class CollectSpanFilter implements HelperFilter {

    private StringRedisTemplate stringRedisTemplate;

    public CollectSpanFilter(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public int filterOrder() {
        return 25;
    }

    @Override
    public boolean shouldFilter(RequestContext context) {
        return true;
    }

    @Override
    public boolean run(RequestContext context) {
        ZuulRoute zuulRoute = context.getRoute();
        PermissionDTO permission = context.getPermission();
        String serviceId = zuulRoute.getServiceId();
        String method = context.request.method;
        TranceSpan tranceSpan = new TranceSpan(permission.getPath(), serviceId, method, LocalDate.now());
        Observable
                .just(tranceSpan)
                .subscribeOn(Schedulers.io())
                .subscribe(this::tranceSpanSubscriber);
        return true;
    }

    private void tranceSpanSubscriber(final TranceSpan tranceSpan) {
        String service = tranceSpan.getService();
        StringBuilder builder =
                new StringBuilder(tranceSpan.getToday().toString())
                        .append(":").append("zSet");
        String serviceInvokeKey = builder.toString();
        staticInvokeCount(serviceInvokeKey, service);

        builder.append(":").append(service);
        String apiInvokeKey = builder.toString();
        StringBuilder api = new StringBuilder();
        api.append(tranceSpan.getUrl()).append(":").append(tranceSpan.getMethod());
        staticInvokeCount(apiInvokeKey, api.toString());
    }

    private void staticInvokeCount(String key, String value) {
        if (stringRedisTemplate.hasKey(key)) {
            stringRedisTemplate.opsForZSet().incrementScore(key, value, 1);
        } else {
            stringRedisTemplate.opsForZSet().add(key, value, 1);
            stringRedisTemplate.expire(key, 31, TimeUnit.DAYS);
        }
    }
}
