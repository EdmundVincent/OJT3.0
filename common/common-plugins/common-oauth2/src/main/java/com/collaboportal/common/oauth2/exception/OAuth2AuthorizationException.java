package com.collaboportal.common.oauth2.exception;

/**
 * OAuth2 認可例外
 * 
 * OAuth2 認可プロセス中にエラーが発生した場合にスローされる（認可コード交換失敗など）
 */
public class OAuth2AuthorizationException extends OAuth2Exception {

    /**
     * コンストラクタ
     * 
     * @param message エラーメッセージ
     */
    public OAuth2AuthorizationException(String message) {
        super("OAUTH2_AUTHORIZATION_ERROR", message);
    }

    /**
     * コンストラクタ（詳細情報付き）
     * 
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     */
    public OAuth2AuthorizationException(String message, Object errorDetails) {
        super("OAUTH2_AUTHORIZATION_ERROR", message, errorDetails);
    }

    /**
     * コンストラクタ（例外チェーン付き）
     * 
     * @param message エラーメッセージ
     * @param cause 原因例外
     */
    public OAuth2AuthorizationException(String message, Throwable cause) {
        super("OAUTH2_AUTHORIZATION_ERROR", message, cause);
    }

    /**
     * コンストラクタ（完全版）
     * 
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     * @param cause 原因例外
     */
    public OAuth2AuthorizationException(String message, Object errorDetails, Throwable cause) {
        super("OAUTH2_AUTHORIZATION_ERROR", message, errorDetails, cause);
    }
} 