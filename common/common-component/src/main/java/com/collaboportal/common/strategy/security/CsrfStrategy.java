package com.collaboportal.common.strategy.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * CSRF保護設定を行うストラテジークラス
 */
public class CsrfStrategy implements SecurityConfigStrategy{

    /**
     * HTTPセキュリティ設定を構成する
     * @param http HttpSecurityオブジェクト
     * @throws Exception 設定中に発生する可能性のある例外
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // CSRF保護を無効化する
       http.csrf(csrf -> csrf.disable());
    }

}
