package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.oauth2.controller.AuthorizationController;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 コントローラー自動設定クラス
 * OAuth2 関連のコントローラーコンポーネントの登録を担当する
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.controller"
})
public class OAuth2ControllerAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2ControllerAutoConfiguration.class);

    public OAuth2ControllerAutoConfiguration() {
        logger.debug("OAuth2 コントローラー自動設定の初期化が完了しました");
        logger.debug("AuthorizationServletFilterの設定を開始します,除外パス: {}",
                ConfigManager.getConfig().getAuthExcludedList());
    }

    /**
     * 認可コントローラー Bean
     * OAuth2 認可リクエストとコールバックを処理する
     * 
     * @param loginStrategyRegistry ログイン戦略レジストリ
     * @return AuthorizationController インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(AuthorizationController.class)
    @ConditionalOnBean(LoginStrategyRegistry.class)
    public AuthorizationController authorizationController(
            @Autowired LoginStrategyRegistry loginStrategyRegistry,
            @Autowired OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        logger.debug("AuthorizationController Bean を登録します");
        return new AuthorizationController(loginStrategyRegistry, clientRegistrationFactory);
    }
}