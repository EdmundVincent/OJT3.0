package com.collaboportal.common.exception;


/**
 * 認証例外クラス
 * 認証プロセス中（例：無効なトークン、ユーザーが存在しない場合など）にスローされます。
 */
public class AuthenticationException extends RuntimeException {
    public AuthenticationException(String message) {
        super(message);
    }

    public AuthenticationException(String message, Throwable cause) {
        super(message, cause);
    }
}
