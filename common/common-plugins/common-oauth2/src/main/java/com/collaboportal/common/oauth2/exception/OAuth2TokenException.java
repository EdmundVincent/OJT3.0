package com.collaboportal.common.oauth2.exception;

/**
 * OAuth2 トークン例外
 * 
 * JWT トークンの検証、解析、または処理プロセス中にエラーが発生した場合にスローされる
 */
public class OAuth2TokenException extends OAuth2Exception {

    /**
     * コンストラクタ
     * 
     * @param message エラーメッセージ
     */
    public OAuth2TokenException(String message) {
        super("OAUTH2_TOKEN_ERROR", message);
    }

    /**
     * コンストラクタ（詳細情報付き）
     * 
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     */
    public OAuth2TokenException(String message, Object errorDetails) {
        super("OAUTH2_TOKEN_ERROR", message, errorDetails);
    }

    /**
     * コンストラクタ（例外チェーン付き）
     * 
     * @param message エラーメッセージ
     * @param cause 原因例外
     */
    public OAuth2TokenException(String message, Throwable cause) {
        super("OAUTH2_TOKEN_ERROR", message, cause);
    }

    /**
     * コンストラクタ（完全版）
     * 
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     * @param cause 原因例外
     */
    public OAuth2TokenException(String message, Object errorDetails, Throwable cause) {
        super("OAUTH2_TOKEN_ERROR", message, errorDetails, cause);
    }
} 