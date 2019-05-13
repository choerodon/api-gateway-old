package io.choerodon.gateway.config;

import org.springframework.boot.context.properties.ConfigurationProperties;

import java.util.Arrays;
import java.util.List;

/**
 * 加载GatewayHelper配置信息的配置类
 *
 * @author flyleft
 */
@ConfigurationProperties(prefix = "choerodon.gateway")
public class GatewayProperties {

    private Permission permission = new Permission();

    private boolean enabled = true;

    private boolean retryable = false;

    private boolean enabledJwtLog = false;

    private String jwtKey = "choerodon";

    private String oauthInfoUri = "http://oauth-server/oauth/api/user";

    public GatewayProperties() {
        //保留一个空构造器
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRetryable() {
        return retryable;
    }

    public void setRetryable(boolean retryable) {
        this.retryable = retryable;
    }

    public boolean isEnabledJwtLog() {
        return enabledJwtLog;
    }

    public void setEnabledJwtLog(boolean enabledJwtLog) {
        this.enabledJwtLog = enabledJwtLog;
    }

    public Permission getPermission() {
        return permission;
    }

    public void setPermission(Permission permission) {
        this.permission = permission;
    }

    public String getJwtKey() {
        return jwtKey;
    }

    public void setJwtKey(String jwtKey) {
        this.jwtKey = jwtKey;
    }

    public String getOauthInfoUri() {
        return oauthInfoUri;
    }

    public void setOauthInfoUri(String oauthInfoUri) {
        this.oauthInfoUri = oauthInfoUri;
    }

    @Override
    public String toString() {
        return "GatewayProperties{" +
                "permission=" + permission +
                ", enabled=" + enabled +
                ", retryable=" + retryable +
                ", enabledJwtLog=" + enabledJwtLog +
                ", jwtKey='" + jwtKey + '\'' +
                ", oauthInfoUri='" + oauthInfoUri + '\'' +
                '}';
    }

    public static class Permission {
        private Boolean enabled = true;

        private List<String> skipPaths = Arrays.asList("/**/skip/**", "/oauth/**");

        private Long cacheSeconds = 600L;

        private Long cacheSize = 5000L;

        public Boolean getEnabled() {
            return enabled;
        }

        public void setEnabled(Boolean enabled) {
            this.enabled = enabled;
        }

        public List<String> getSkipPaths() {
            return skipPaths;
        }

        public void setSkipPaths(List<String> skipPaths) {
            this.skipPaths = skipPaths;
        }

        public Long getCacheSeconds() {
            return cacheSeconds;
        }

        public void setCacheSeconds(Long cacheSeconds) {
            this.cacheSeconds = cacheSeconds;
        }

        public Long getCacheSize() {
            return cacheSize;
        }

        public void setCacheSize(Long cacheSize) {
            this.cacheSize = cacheSize;
        }

    }
}
