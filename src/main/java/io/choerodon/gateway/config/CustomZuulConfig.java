package io.choerodon.gateway.config;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.config.client.MemoryRouteLocator;
import org.springframework.cloud.config.client.RouterOperator;
import org.springframework.cloud.netflix.ribbon.support.RibbonRequestCustomizer;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.cloud.netflix.zuul.filters.route.RibbonCommandFactory;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import io.choerodon.gateway.filter.GateWayHelperFilter;
import io.choerodon.gateway.filter.HeaderWrapperFilter;

/**
 * 自定义configuration配置类
 *
 * @author flyleft
 */
@Configuration
@EnableConfigurationProperties(GatewayHelperProperties.class)
public class CustomZuulConfig {
    @Value("${choerodon.gateway.allowed.origin:*}")
    private String allowedOrigin;

    @Bean
    public RouteLocator memoryRouterOperator(ServerProperties server, ZuulProperties zuulProperties) {
        return new MemoryRouteLocator(server.getServletPrefix(), zuulProperties);
    }

    @Bean(name = "handRouterOperator")
    public RouterOperator routerOperator(ApplicationEventPublisher publisher,
                                         RouteLocator routeLocator) {
        return new RouterOperator(publisher, routeLocator);
    }

    /**
     * 声明GateWayHelperFilter
     *
     * @param ribbonCommandFactory ribbon创建工厂，GateWayHelperFilter使用ribbon转发请求
     * @return 配置的GateWayHelperFilter
     */
    @Bean
    public GateWayHelperFilter gateWayHelperFilter(GatewayHelperProperties gatewayHelperProperties, RibbonCommandFactory<?> ribbonCommandFactory,
                                                   Optional<List<RibbonRequestCustomizer>> requestCustomizers) {
        return new GateWayHelperFilter(gatewayHelperProperties,
                requestCustomizers.orElseGet(Collections::emptyList),
                ribbonCommandFactory);
    }

    /**
     * 配置GateWayHelperFilter的路径，执行顺序
     *
     * @param filter GateWayHelperFilter
     * @return filter的声明
     */
    @Bean
    public FilterRegistrationBean gatewayHelperFilterRegistrationBean(GateWayHelperFilter filter) {
        FilterRegistrationBean registration = new FilterRegistrationBean();
        registration.setFilter(filter);
        registration.addUrlPatterns("/*");
        registration.setName("gateWayHelperFilter");
        registration.setOrder(1);
        return registration;
    }

    /**
     * 解决跨域问题
     *
     * @return 跨域声明
     */
    @Bean
    public FilterRegistrationBean corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.addAllowedOrigin(allowedOrigin);
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        config.addAllowedMethod("*");
        //添加response暴露的header
        String[] responseHeader =
                {"date", "content-encoding", "server", "etag", "vary", "Cache-Control", "Last-Modified",
                        "content-type", "transfer-encoding", "connection", "x-application-context"};
        config.setExposedHeaders(Arrays.asList(responseHeader));
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public HeaderWrapperFilter headerWrapperFilter(GatewayHelperProperties gatewayHelperProperties) {
        return new HeaderWrapperFilter(gatewayHelperProperties);
    }

}
