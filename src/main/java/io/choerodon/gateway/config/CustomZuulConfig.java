package io.choerodon.gateway.config;

import java.util.Arrays;
import java.util.List;

import io.choerodon.gateway.helper.AuthenticationHelper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.web.ServerProperties;
import org.springframework.boot.autoconfigure.web.servlet.DispatcherServletPath;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.cloud.client.loadbalancer.LoadBalanced;
import org.springframework.cloud.config.client.MemoryRouteLocator;
import org.springframework.cloud.config.client.RouterOperator;
import org.springframework.cloud.netflix.zuul.filters.RouteLocator;
import org.springframework.cloud.netflix.zuul.filters.ZuulProperties;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.jwt.crypto.sign.MacSigner;
import org.springframework.security.jwt.crypto.sign.Signer;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

import io.choerodon.gateway.filter.route.GateWayHelperFilter;
import io.choerodon.gateway.filter.route.HeaderWrapperFilter;

/**
 * 自定义configuration配置类
 *
 * @author flyleft
 */
@Configuration
@EnableConfigurationProperties(GatewayProperties.class)
public class CustomZuulConfig {
    @Value("#{T(java.util.Arrays).asList('${choerodon.gateway.allowed.origin:*}')}")
    private List<String> allowedOrigins;

    @Bean
    public RouteLocator memoryRouterOperator(ServerProperties server, ZuulProperties zuulProperties, DispatcherServletPath dispatcherServletPath) {
        return new MemoryRouteLocator(dispatcherServletPath.getPrefix(), zuulProperties);
    }

    @Bean(name = "handRouterOperator")
    public RouterOperator routerOperator(ApplicationEventPublisher publisher,
                                         RouteLocator routeLocator) {
        return new RouterOperator(publisher, routeLocator);
    }

    /**
     * 声明GateWayHelperFilter
     *
     * @return 配置的GateWayHelperFilter
     */
    @Bean
    public GateWayHelperFilter gateWayHelperFilter(AuthenticationHelper gatewayHelper) {
        return new GateWayHelperFilter(gatewayHelper);
    }

    /**
     * 配置GateWayHelperFilter的路径，执行顺序
     *
     * @param filter GateWayHelperFilter
     * @return filter的声明
     */
    @Bean
    public FilterRegistrationBean<GateWayHelperFilter> gatewayHelperFilterRegistrationBean(GateWayHelperFilter filter) {
        FilterRegistrationBean<GateWayHelperFilter> registration = new FilterRegistrationBean<>();
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
        config.setAllowedOrigins(allowedOrigins);
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        config.addAllowedMethod("*");
        //添加response暴露的header
        String[] responseHeader =
                {"date", "content-encoding", "server", "etag", "vary", "Cache-Control", "Last-Modified",
                        "content-type", "transfer-encoding", "connection", "x-application-context"};
        config.setExposedHeaders(Arrays.asList(responseHeader));
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean<>(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public HeaderWrapperFilter headerWrapperFilter(GatewayProperties gatewayHelperProperties) {
        return new HeaderWrapperFilter(gatewayHelperProperties);
    }

    @Bean
    public Signer jwtSigner(GatewayProperties gatewayHelperProperties) {
        return new MacSigner(gatewayHelperProperties.getJwtKey());
    }

    @Bean
    @LoadBalanced
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

}
