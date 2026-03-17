package com.collaboportal.common.strategy.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.security.servlet.PathRequest;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

import com.collaboportal.common.ConfigManager;

/**
 * 認証・認可設定を行うストラテジークラス
 */
public class FirewallStrategy implements SecurityConfigStrategy {

    // ロガー
    Logger logger = LoggerFactory.getLogger(FirewallStrategy.class);

    // 認証不要モードフラグ
    private String noAuthMode = ConfigManager.getConfig().getNoAuthorization();

    // 環境フラグ
    private String envFlag = ConfigManager.getConfig().getEnvFlag();

    /**
     * コンストラクタ
     * 
     * @param noAuthMode 認証不要モードフラグ
     * @param envFlag    環境フラグ
     * @param noAuthUrls 認証不要URLリスト
     */
    public FirewallStrategy(String noAuthMode, String envFlag, String[] noAuthUrls) {
        this.noAuthMode = noAuthMode;
        this.envFlag = envFlag;
        logger.debug("AuthorizationStrategyが初期化されました");
    }

    /**
     * HTTPセキュリティ設定を構成する
     * 
     * @param http HttpSecurityオブジェクト
     * @throws Exception 設定中に発生する可能性のある例外
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        logger.debug("認証設定を開始します。認証不要モード: {}, 環境フラグ: {}", noAuthMode, envFlag);

        http.authorizeHttpRequests(auth -> {
            // 認証不要モードの場合
            if ("0".equals(noAuthMode)) {
                logger.debug("認証不要モードが有効です");
                // テスト環境の場合、/testEnvへのアクセスを拒否
                if ("1".equals(envFlag)) {
                    logger.debug("テスト環境が検出されました。/testEnvへのアクセスを拒否します");
                    auth.requestMatchers("/testEnv").denyAll().anyRequest().permitAll();
                } else {
                    logger.debug("全リクエストを許可します");
                    // それ以外は全リクエストを許可
                    auth.anyRequest().permitAll();
                }
            } else {
                logger.debug("認証が必要なモードが有効です");
                // 認証が必要なモードの場合
                auth
                        // 静的リソースへのアクセスを許可
                        .requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        // /assets配下のリソースへのアクセスを許可
                        .requestMatchers("/assets/**").permitAll()
                        // その他のリクエストは認証が必要
                        .anyRequest().authenticated();
                logger.debug("静的リソースと/assets配下のリソースへのアクセスを許可し、その他のリクエストには認証を要求します");
            }
        });

        logger.debug("認証設定が完了しました");
    }

}
