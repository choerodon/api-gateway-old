package io.choerodon.gateway.config;

import io.choerodon.gateway.filter.GateWayHelperFilter;
import io.choerodon.gateway.filter.HeaderWrapperFilter;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.filter.ShallowEtagHeaderFilter;

import javax.servlet.Filter;
import java.util.Collections;
import java.util.List;

/**
 * 自定义configuration配置类
 *
 * @author zhipeng.zuo
 * @date 18-1-4
 */
@Configuration
@EnableConfigurationProperties(GatewayHelperProperties.class)
public class CustomZuulConfig {

    @Autowired
    private ZuulProperties zuulProperties;

    @Autowired
    private ServerProperties server;

    @Autowired
    private GatewayHelperProperties gatewayHelperProperties;

    @SuppressWarnings("rawtypes")
    @Autowired(required = false)
    private List<RibbonRequestCustomizer> requestCustomizers = Collections.emptyList();

    @Bean
    public RouteLocator memoryRouterOperator() {
        return new MemoryRouteLocator(this.server.getServletPrefix(), this.zuulProperties);
    }

    @Bean(name = "hand-routerOperator")
    public RouterOperator routerOperator(ApplicationEventPublisher publisher,
                                         RouteLocator routeLocator) {
        return new RouterOperator(publisher, routeLocator);
    }

    /**
     * 声明GateWayHelperFilter
     *
     * @param ribbonCommandFactory       ribbon创建工厂，GateWayHelperFilter使用ribbon转发请求
     * @return 配置的GateWayHelperFilter
     */
    @Bean
    public GateWayHelperFilter gateWayHelperFilter(RibbonCommandFactory<?> ribbonCommandFactory) {
        return new GateWayHelperFilter(gatewayHelperProperties,
                requestCustomizers,
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
        config.addAllowedOrigin("*");
        config.addAllowedHeader("*");
        config.setMaxAge(18000L);
        config.addAllowedMethod("*");
        source.registerCorsConfiguration("/**", config);

        FilterRegistrationBean bean = new FilterRegistrationBean(new CorsFilter(source));
        bean.setOrder(0);
        return bean;
    }

    @Bean
    public Filter shallowEtagHeaderFilter() {
        return new ShallowEtagHeaderFilter();
    }

    @Bean
    public HeaderWrapperFilter headerWrapperFilter() {
        return new HeaderWrapperFilter();
    }
}
