package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal"
})
public class OAuth2StrategyAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2StrategyAutoConfiguration.class);

    public OAuth2StrategyAutoConfiguration() {
        logger.debug("OAuth2ストラテジーの自動設定を開始します");
    }

    @Bean
    @ConditionalOnMissingBean(LoginStrategyRegistry.class)
    public LoginStrategyRegistry loginStrategyRegistry() {
        logger.debug("LoginStrategyRegistry Bean");
        return new LoginStrategyRegistry();
    }

    @Bean
    @ConditionalOnMissingBean(JwtTokenStrategyRegistry.class)
    public JwtTokenStrategyRegistry jwtTokenStrategyRegistry() {
        logger.debug("JwtTokenStrategyRegistry Bean");
        return new JwtTokenStrategyRegistry();
    }

}