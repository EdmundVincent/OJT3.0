package com.collaboportal.common.funcs.hooks;

/**
 * 認証フィルターエラー処理戦略関数インターフェース
 * 認証フィルターで発生したエラーを処理するための関数型インターフェース
 */
@FunctionalInterface
public interface AuthFilterErrorStrategyFunction {
    
    /**
     * エラーを処理するメソッド
     * @param e 発生した例外オブジェクト
     */
    void handle(Throwable e);

}
