package com.collaboportal.common.oauth2.strategy.login;

/**
 * ログイン戦略インターフェース
 * 
 * 様々なログイン方式（テスト環境、本番環境など）を統一的に処理するための
 * 関数型インターフェースです。Strategy パターンを使用して、
 * 認証コンテキストに応じた適切なログイン処理を実行します。
 */
@FunctionalInterface
public interface LoginStrategy {
    
    /**
     * ログイン戦略を実行します
     * 
     * @param obj 認証コンテキストオブジェクト（通常は CallbackContext など）
     */
    void run(Object obj);
}
