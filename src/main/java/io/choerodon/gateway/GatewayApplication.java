package io.choerodon.gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.SecurityProperties;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.cloud.netflix.zuul.EnableZuulProxy;
import org.springframework.core.annotation.Order;
import org.springframework.retry.annotation.EnableRetry;

/**
 * 运行主类
 * @author zhipeng.zuo
 */
@EnableRetry
@EnableZuulProxy
@EnableEurekaClient
@SpringBootApplication
@Order(SecurityProperties.ACCESS_OVERRIDE_ORDER)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
    }

}
