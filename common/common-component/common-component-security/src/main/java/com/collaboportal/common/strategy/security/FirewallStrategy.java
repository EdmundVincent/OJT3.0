package com.collaboportal.common.strategy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import com.collaboportal.common.ConfigManager;

public class FirewallStrategy implements SecurityConfigStrategy {
    Logger logger = LoggerFactory.getLogger(FirewallStrategy.class);
    private String noAuthMode = ConfigManager.getConfig().getNoAuthorization();
    private String envFlag = ConfigManager.getConfig().getEnvFlag();
    public FirewallStrategy(String noAuthMode, String envFlag, String[] noAuthUrls) {
        this.noAuthMode = noAuthMode;
        this.envFlag = envFlag;
        logger.debug("AuthorizationStrategyが初期化されました");
    }
    @Override
    public void configure(HttpSecurity http) throws Exception {
        logger.debug("認証設定を開始します。認証不要モード: {}, 環境フラグ: {}", noAuthMode, envFlag);
        http.authorizeHttpRequests(auth -> {
            if ("0".equals(noAuthMode)) {
                logger.debug("認証不要モードが有効です");
                if ("1".equals(envFlag)) {
                    logger.debug("テスト環境が検出されました。/testEnvへのアクセスを拒否します");
                    auth.requestMatchers("/testEnv").denyAll().anyRequest().permitAll();
                } else {
                    logger.debug("全リクエストを許可します");
                    auth.anyRequest().permitAll();
                }
            } else {
                logger.debug("認証が必要なモードが有効です");
                auth.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                    .requestMatchers("/assets/**").permitAll()
                    .anyRequest().authenticated();
                logger.debug("静的リソースと/assets配下のリソースへのアクセスを許可し、その他のリクエストには認証を要求します");
            }
        });
        logger.debug("認証設定が完了しました");
    }
}
