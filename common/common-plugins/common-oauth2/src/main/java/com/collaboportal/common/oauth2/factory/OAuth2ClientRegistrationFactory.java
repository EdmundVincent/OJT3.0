package com.collaboportal.common.oauth2.factory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * ファクトリーパターン - OAuth2クライアント登録ファクトリー
 * 責務：複数のOAuth2ClientRegistrationインスタンスの作成と管理
 * 利点：クライアント設定の集中管理、新しいプロバイダーの拡張が容易
 */
@Component
public class OAuth2ClientRegistrationFactory {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ClientRegistrationFactory.class);

    // 全プロバイダー設定を格納
    private Map<String, Map<String, Object>> providers = new ConcurrentHashMap<>();

    // 作成済みクライアント登録をキャッシュ
    private final Map<String, OAuth2ClientRegistration> clientRegistrations = new ConcurrentHashMap<>();

    @PostConstruct
    public void init() {
        logger.info("OAuth2クライアント登録ファクトリー初期化開始、プロバイダー数: {}", providers.size());

        if (providers.isEmpty()) {
            logger.warn("明示的な OAuth2 プロバイダー定義が見つかりません。CommonConfig の値をもとにデフォルト登録を生成します。");
            registerDefaultFromCommonConfig();
        } else {
            for (Map.Entry<String, Map<String, Object>> entry : providers.entrySet()) {
                String providerId = entry.getKey();
                Map<String, Object> config = entry.getValue();

                try {
                    OAuth2ClientRegistration registration = createClientRegistration(providerId, config);
                    clientRegistrations.put(providerId, registration);

                    logger.info("OAuth2クライアント登録の作成に成功: {} - {}", providerId, registration.getDisplayName());
                } catch (Exception e) {
                    logger.error("OAuth2クライアント登録の作成に失敗: {}", providerId, e);
                }
            }
        }

        logger.info("OAuth2クライアント登録ファクトリー初期化完了、登録成功プロバイダー: {}", clientRegistrations.keySet());
    }

    /**
     * OAuth2クライアント登録を作成
     */
    private OAuth2ClientRegistration createClientRegistration(String providerId, Map<String, Object> config) {
        CommonConfig commonConfig = ConfigManager.getConfig();

        String clientId = asString(config.get("client-id"));
        if (clientId == null || clientId.isBlank()) {
            clientId = commonConfig.getCollaboportalClientIdWeb();
        }

        String clientSecret = asString(config.get("client-secret"));
        if (clientSecret == null || clientSecret.isBlank()) {
            clientSecret = commonConfig.getCollaboportalClientSecretWeb();
        }

        String issuer = asString(config.get("issuer"));
        if (issuer == null || issuer.isBlank()) {
            issuer = commonConfig.getCollaboportalIssuer();
        }

        String audience = asString(config.get("audience"));
        if (audience == null || audience.isBlank()) {
            audience = commonConfig.getCollaboportalAudience();
        }

        String redirectUri = asString(config.get("redirect-uri"));
        if (redirectUri == null || redirectUri.isBlank()) {
            redirectUri = commonConfig.getCallback() != null ? commonConfig.getCallback() : "{baseUrl}/auth/callback";
        }

        return OAuth2ClientRegistration.builder()
                .providerId(providerId)
                .clientId(clientId)
                .clientSecret(clientSecret)
                .issuer(issuer)
                .audience(audience)
                .scope(toList(config.get("scope"), "scope")) // 複数フォーマット対応
                .grantType((String) config.getOrDefault("grant-type", "authorization_code"))
                .redirectUri(redirectUri)
                .userNameAttribute((String) config.getOrDefault("user-name-attribute", "email"))
                .displayName((String) config.getOrDefault("display-name", providerId))
                .build();

    }

    public String getProviderId() {
        return clientRegistrations.keySet().stream().findFirst().orElse("");
    }

    /**
     * プロバイダーIDに基づいてクライアント登録を取得
     */
    public OAuth2ClientRegistration getClientRegistration(String providerId) {
        return clientRegistrations.get(providerId);
    }

    /**
     * 全クライアント登録を取得
     */
    public Map<String, OAuth2ClientRegistration> getAllClientRegistrations() {
        return Collections.unmodifiableMap(clientRegistrations);
    }

    // Spring Boot設定プロパティ注入
    public void setProviders(Map<String, Map<String, Object>> providers) {
        this.providers = providers;
    }

    public Map<String, Map<String, Object>> getProviders() {
        return providers;
    }

    /** String / List / Map → List<String> への対応 */
    private List<String> toList(Object raw, String field) {
        if (raw == null) {
            return List.of();
        }
        if (raw instanceof String str) { // "a b c" | "a,b,c"
            return Arrays.asList(str.split("[,\\s]+"));
        }
        if (raw instanceof List<?> list) { // YAML リスト
            return list.stream().map(Object::toString).toList();
        }
        if (raw instanceof Map<?, ?> map) { // YAML マップ → 値を取得
            return map.values().stream().map(Object::toString).toList();
        }
        throw new IllegalArgumentException(
                "サポートされていないフィールドタイプ '" + field + "': " + raw.getClass());
    }

    private String asString(Object value) {
        return value != null ? value.toString() : null;
    }

    private void registerDefaultFromCommonConfig() {
        CommonConfig commonConfig = ConfigManager.getConfig();
        String providerId = "default";
        OAuth2ClientRegistration registration = OAuth2ClientRegistration.builder()
                .providerId(providerId)
                .clientId(commonConfig.getCollaboportalClientIdWeb())
                .clientSecret(commonConfig.getCollaboportalClientSecretWeb())
                .issuer(commonConfig.getCollaboportalIssuer())
                .audience(commonConfig.getCollaboportalAudience())
                .redirectUri(commonConfig.getCallback() != null ? commonConfig.getCallback() : "{baseUrl}/auth/callback")
                .scope(List.of("openid", "profile", "email"))
                .grantType("authorization_code")
                .userNameAttribute("email")
                .displayName(providerId)
                .build();
        clientRegistrations.put(providerId, registration);
    }

}
