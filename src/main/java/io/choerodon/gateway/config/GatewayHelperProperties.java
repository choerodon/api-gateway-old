package io.choerodon.gateway.config;

import java.util.Arrays;

import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 加载GatewayHelper配置信息的配置类
 * @author zhipeng.zuo
 * @date 17-12-27
 */
@ConfigurationProperties(prefix = "choerodon.gateway.helper")
public class GatewayHelperProperties {

    private boolean enabled = true;

    private String serviceId = "gateway-helper";

    private boolean retryable = false;

    private String[] helperSkipPaths = new String[]{"/**/skip/**", "/oauth/**", "/**/swagger-ui.html"};

    public GatewayHelperProperties() {
        //保留一个空构造器
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getServiceId() {
        return serviceId;
    }

    public void setServiceId(String serviceId) {
        this.serviceId = serviceId;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public String[] getHelperSkipPaths() {
        return helperSkipPaths;
    }

    public void setHelperSkipPaths(String[] helperSkipPaths) {
        this.helperSkipPaths = helperSkipPaths;
    }

    @Override
    public String toString() {
        return "GatewayHelperProperties{"
                + "enabled=" + enabled
                + ", serviceId='" + serviceId + '\''
                + ", retryable=" + retryable
                + ", helperSkipPaths=" + Arrays.toString(helperSkipPaths)
                + '}';
    }
}
