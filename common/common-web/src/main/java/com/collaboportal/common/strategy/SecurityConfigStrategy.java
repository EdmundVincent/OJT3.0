package com.collaboportal.common.strategy.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

/**
 * セキュリティ設定を行うためのストラテジーインターフェース
 * 関数型インターフェースとして定義されており、セキュリティ設定を実装する
 */
@FunctionalInterface
public interface SecurityConfigStrategy {

    /**
     * HTTPセキュリティ設定を構成する
     * @param http HttpSecurityオブジェクト
     * @throws Exception 設定中に発生する可能性のある例外
     */
    void configure(HttpSecurity http) throws Exception;

}
