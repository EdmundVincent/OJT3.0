package com.collaboportal.common.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;

import com.collaboportal.common.config.CommonConfig;

/**
 * 共通設定の自動設定クラス
 * Spring Bootの自動設定機能を提供する
 */
@AutoConfiguration
public class CommonConfigAutoConfiguration {

    /**
     * 共通設定Beanを生成する
     * application.propertiesの"common.util"プレフィックスで始まる設定をバインドする
     * @return CommonConfig 共通設定オブジェクト
     */
    @Bean
    @ConfigurationProperties(prefix = "common.util")
    public CommonConfig getCommonConfig(){
        return new CommonConfig();
    }
}
