package com.collaboportal.common.oauth2.exception;

import lombok.Getter;

/**
 * OAuth2 基底例外クラス
 * 
 * すべての OAuth2 関連例外の基底クラスとして、統一された例外処理構造を提供する
 */
@Getter
public class OAuth2Exception extends RuntimeException {

    private final String errorCode;
    private final String errorDescription;
    private final Object errorDetails;

    /**
     * コンストラクタ
     * 
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     */
    public OAuth2Exception(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
        this.errorDescription = message;
        this.errorDetails = null;
    }

    /**
     * コンストラクタ（詳細情報付き）
     * 
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     */
    public OAuth2Exception(String errorCode, String message, Object errorDetails) {
        super(message);
        this.errorCode = errorCode;
        this.errorDescription = message;
        this.errorDetails = errorDetails;
    }

    /**
     * コンストラクタ（例外チェーン付き）
     * 
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     * @param cause 原因例外
     */
    public OAuth2Exception(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDescription = message;
        this.errorDetails = null;
    }

    /**
     * コンストラクタ（完全版）
     * 
     * @param errorCode エラーコード
     * @param message エラーメッセージ
     * @param errorDetails エラー詳細情報
     * @param cause 原因例外
     */
    public OAuth2Exception(String errorCode, String message, Object errorDetails, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
        this.errorDescription = message;
        this.errorDetails = errorDetails;
    }
} 