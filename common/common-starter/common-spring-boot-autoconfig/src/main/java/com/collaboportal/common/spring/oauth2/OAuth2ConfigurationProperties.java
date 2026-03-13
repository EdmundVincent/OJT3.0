package com.collaboportal.common.spring.oauth2;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * OAuth2 設定プロパティクラス
 * application-oauth2.properties の OAuth2 関連設定をバインドする
 */
@Component
@ConfigurationProperties(prefix = "oauth2")
public class OAuth2ConfigurationProperties {

    /**
     * OAuth2 プロバイダー設定
     * oauth2.providers.{providerId}.{property}
     */
    private Map<String, OAuth2ProviderProperties> providers = new HashMap<>();

    /**
     * OAuth2 セキュリティ設定
     */
    private SecurityProperties security = new SecurityProperties();

    /**
     * OAuth2 フィルター設定
     */
    private FilterProperties filter = new FilterProperties();

    /**
     * OAuth2 コールバック設定
     */
    private CallbackProperties callback = new CallbackProperties();

    /**
     * OAuth2 状態管理設定
     */
    private StateProperties state = new StateProperties();

    // Getters and Setters
    public Map<String, OAuth2ProviderProperties> getProviders() {
        return providers;
    }

    public void setProviders(Map<String, OAuth2ProviderProperties> providers) {
        this.providers = providers;
    }

    public SecurityProperties getSecurity() {
        return security;
    }

    public void setSecurity(SecurityProperties security) {
        this.security = security;
    }

    public FilterProperties getFilter() {
        return filter;
    }

    public void setFilter(FilterProperties filter) {
        this.filter = filter;
    }

    public CallbackProperties getCallback() {
        return callback;
    }

    public void setCallback(CallbackProperties callback) {
        this.callback = callback;
    }

    public StateProperties getState() {
        return state;
    }

    public void setState(StateProperties state) {
        this.state = state;
    }

    /**
     * OAuth2 プロバイダープロパティ
     */
    public static class OAuth2ProviderProperties {
        private String clientId;
        private String clientSecret;
        private String issuer;
        private String redirectUri;
        private List<String> scope;
        private String audience;
        private String grantType;
        private String userNameAttribute;
        private String displayName;
        private String[] pathPatterns;

        // Getters and Setters
        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getClientSecret() {
            return clientSecret;
        }

        public void setClientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
        }

        public String getIssuer() {
            return issuer;
        }

        public void setIssuer(String issuer) {
            this.issuer = issuer;
        }

        public String getRedirectUri() {
            return redirectUri;
        }

        public void setRedirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
        }

        public List<String> getScope() {
            return scope;
        }

        public void setScope(List<String> scope) {
            this.scope = scope;
        }

        public String getAudience() {
            return audience;
        }

        public void setAudience(String audience) {
            this.audience = audience;
        }

        public String getGrantType() {
            return grantType;
        }

        public void setGrantType(String grantType) {
            this.grantType = grantType;
        }

        public String getUserNameAttribute() {
            return userNameAttribute;
        }

        public void setUserNameAttribute(String userNameAttribute) {
            this.userNameAttribute = userNameAttribute;
        }

        public String getDisplayName() {
            return displayName;
        }

        public void setDisplayName(String displayName) {
            this.displayName = displayName;
        }

        public String[] getPathPatterns() {
            return pathPatterns;
        }

        public void setPathPatterns(String[] pathPatterns) {
            this.pathPatterns = pathPatterns;
        }
    }

    /**
     * セキュリティプロパティ
     */
    public static class SecurityProperties {
        private boolean enabled = true;
        private boolean allowCredentials = true;
        private int maxAge = 3600;

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public boolean isAllowCredentials() {
            return allowCredentials;
        }

        public void setAllowCredentials(boolean allowCredentials) {
            this.allowCredentials = allowCredentials;
        }

        public int getMaxAge() {
            return maxAge;
        }

        public void setMaxAge(int maxAge) {
            this.maxAge = maxAge;
        }
    }

    /**
     * フィルタープロパティ
     */
    public static class FilterProperties {
        private boolean enabled = true;
        private int order = 100;
        private List<String> includePatterns = new ArrayList<>(Arrays.asList("/", "/index.html"));

        // Getters and Setters
        public boolean isEnabled() {
            return enabled;
        }

        public void setEnabled(boolean enabled) {
            this.enabled = enabled;
        }

        public int getOrder() {
            return order;
        }

        public void setOrder(int order) {
            this.order = order;
        }

        public List<String> getIncludePatterns() {
            return new ArrayList<>(includePatterns);
        }

        public void setIncludePatterns(List<String> includePatterns) {
            this.includePatterns = new ArrayList<>(includePatterns);
        }
    }

    /**
     * コールバックプロパティ
     */
    public static class CallbackProperties {
        private String successRedirect = "/";
        private String errorRedirect = "/#/error";
        private int timeout = 30000;

        // Getters and Setters
        public String getSuccessRedirect() {
            return successRedirect;
        }

        public void setSuccessRedirect(String successRedirect) {
            this.successRedirect = successRedirect;
        }

        public String getErrorRedirect() {
            return errorRedirect;
        }

        public void setErrorRedirect(String errorRedirect) {
            this.errorRedirect = errorRedirect;
        }

        public int getTimeout() {
            return timeout;
        }

        public void setTimeout(int timeout) {
            this.timeout = timeout;
        }
    }

    /**
     * 状態プロパティ
     */
    public static class StateProperties {
        private String storeType = "cookie";
        private int expiration = 300;
        private String prefix = "oauth2_state_";

        // Getters and Setters
        public String getStoreType() {
            return storeType;
        }

        public void setStoreType(String storeType) {
            this.storeType = storeType;
        }

        public int getExpiration() {
            return expiration;
        }

        public void setExpiration(int expiration) {
            this.expiration = expiration;
        }

        public String getPrefix() {
            return prefix;
        }

        public void setPrefix(String prefix) {
            this.prefix = prefix;
        }
    }
}