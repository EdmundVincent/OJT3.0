package com.collaboportal.common.spring.jwt;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;

import org.springframework.context.annotation.ComponentScan;

/**
 * JWT自動設定クラス
 * JWT関連のコアコンポーネントの登録を担当する
 */
@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.jwt",
        "com.collaboportal.common.login",
        "com.collaboportal.common.oauth2"
})
public class JwtAutoConfiguration {

    private static final Logger logger = LoggerFactory.getLogger(JwtAutoConfiguration.class);

    public JwtAutoConfiguration() {
        logger.debug("JWT自動設定の初期化が完了しました");
    }

}