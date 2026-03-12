package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.processor.impl.AuthProcessorImpl;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.service.Oauth2UserMasterService;
import com.collaboportal.common.oauth2.template.ext.CallbackLoginTemplate;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

/**
 * OAuth2 コア自動設定クラス
 * OAuth2 関連のコアコンポーネントの登録を担当する
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.oauth2.factory",
        "com.collaboportal.common.oauth2.processor",
        "com.collaboportal.common.oauth2.utils"
})
public class OAuth2CoreAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2CoreAutoConfiguration.class);
    private JwtTokenUtil jwtTokenUtil;

    public OAuth2CoreAutoConfiguration() {
        logger.debug("OAuth2 コア自動設定の初期化が完了しました");
    }

    /**
     * OAuth2 クライアント登録ファクトリ Bean
     * OAuth2 プロバイダーの設定情報を管理する
     * 
     * @return OAuth2ClientRegistrationFactory インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ClientRegistrationFactory.class)
    public OAuth2ClientRegistrationFactory oAuth2ClientRegistrationFactory() {
        logger.debug("OAuth2ClientRegistrationFactory Bean を登録します");
        OAuth2ClientRegistrationFactory bean = new OAuth2ClientRegistrationFactory();
        return bean;
    }

    /**
     * 認証プロセッサ Bean
     * OAuth2 認証フローを処理する
     * 
     * @return AuthProcessor インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(AuthProcessor.class)
    public AuthProcessor authProcessor(@Autowired JwtService jwtService) {
        logger.debug("AuthProcessor Bean を登録します");
        return new AuthProcessorImpl(jwtTokenUtil);
    }

    /**
     * OAuth2 設定プロパティ Bean
     * 外部設定ファイルの OAuth2 関連プロパティをバインドする
     * 
     * @return OAuth2ConfigurationProperties インスタンス
     */
    @Bean
    @ConditionalOnMissingBean(OAuth2ConfigurationProperties.class)
    public OAuth2ConfigurationProperties oAuth2ConfigurationProperties() {
        logger.debug("OAuth2ConfigurationProperties Bean を登録します");
        return new OAuth2ConfigurationProperties();
    }

    @Bean
    @ConditionalOnMissingBean(CallbackLoginTemplate.class)
    public CallbackLoginTemplate callbackLoginTemplate(AuthProcessor authProcessor,
            Oauth2UserMasterService oauth2UserMasterService,
            LoginStrategyRegistry strategyRegistry,
            JwtService jwtService) {
        logger.debug("CallbackLoginTemplate登录");
        return new CallbackLoginTemplate(authProcessor, oauth2UserMasterService, strategyRegistry, jwtService);
    }
}