package com.collaboportal.common.strategy.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;

/**
 * ステートレスセッション設定を行うストラテジークラス
 */
public class StatelessSessionStrategy implements SecurityConfigStrategy{

    /**
     * HTTPセキュリティ設定を構成する
     * @param http HttpSecurityオブジェクト
     * @throws Exception 設定中に発生する可能性のある例外
     */
    @Override
    public void configure(HttpSecurity http) throws Exception {
        // セッション管理をステートレスモードに設定
        http.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS));
    }

}
