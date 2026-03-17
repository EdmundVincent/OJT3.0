package com.collaboportal.common.spring.oauth2;

import com.collaboportal.common.ConfigManager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
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

}
