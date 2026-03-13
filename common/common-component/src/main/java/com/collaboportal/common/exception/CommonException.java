package com.collaboportal.common.exception;

import com.collaboportal.common.error.InternalErrorCode;

/**
 * 共通例外クラス
 * アプリケーション全体で使用されるカスタム例外
 * 内部エラーコードに加えてレスポンス用の値を保持する
 */
public class CommonException extends RuntimeException {

    // 内部エラーコード
    private final InternalErrorCode internalErrorCode;
    // レスポンス用のエラーコード
    private final String nbErrCod;
    // レスポンス用のエラーメッセージ
    private final String errMsg;
    // レスポンス用のエラーレベル
    private final String errLevel;

    /**
     * コンストラクタ
     *
     * @param internalErrorCode 内部エラーコード
     */
    public CommonException(InternalErrorCode internalErrorCode) {
        this(internalErrorCode, null, null);
    }

    /**
     * メッセージ上書き付きコンストラクタ
     *
     * @param internalErrorCode 内部エラーコード
     * @param errMsg エラーメッセージ
     */
    public CommonException(InternalErrorCode internalErrorCode, String errMsg) {
        this(internalErrorCode, errMsg, null);
    }

    /**
     * メッセージ・レベル上書き付きコンストラクタ
     *
     * @param internalErrorCode 内部エラーコード
     * @param errMsg エラーメッセージ
     * @param errLevel エラーレベル
     */
    public CommonException(InternalErrorCode internalErrorCode, String errMsg, String errLevel) {
        super(errMsg != null ? errMsg : resolveMessage(internalErrorCode));
        this.internalErrorCode = internalErrorCode;
        this.nbErrCod = resolveCode(internalErrorCode);
        this.errMsg = errMsg != null ? errMsg : resolveMessage(internalErrorCode);
        this.errLevel = errLevel;
    }

    /**
     * 直接値指定コンストラクタ
     *
     * @param nbErrCod エラーコード
     * @param errMsg エラーメッセージ
     */
    public CommonException(String nbErrCod, String errMsg) {
        this(nbErrCod, errMsg, null);
    }

    /**
     * 直接値指定コンストラクタ（エラーレベル付き）
     *
     * @param nbErrCod エラーコード
     * @param errMsg エラーメッセージ
     * @param errLevel エラーレベル
     */
    public CommonException(String nbErrCod, String errMsg, String errLevel) {
        super(errMsg);
        this.internalErrorCode = null;
        this.nbErrCod = nbErrCod;
        this.errMsg = errMsg;
        this.errLevel = errLevel;
    }

    /**
     * 内部エラーコードを取得する
     *
     * @return 内部エラーコード
     */
    public InternalErrorCode getInternalErrorCode() {
        return internalErrorCode;
    }

    /**
     * レスポンス用エラーコードを取得する
     *
     * @return レスポンス用エラーコード
     */
    public String getNbErrCod() {
        return nbErrCod;
    }

    /**
     * レスポンス用エラーメッセージを取得する
     *
     * @return レスポンス用エラーメッセージ
     */
    public String getErrMsg() {
        return errMsg;
    }

    /**
     * レスポンス用エラーレベルを取得する
     *
     * @return レスポンス用エラーレベル
     */
    public String getErrLevel() {
        return errLevel;
    }

    private static String resolveMessage(InternalErrorCode errorCode) {
        return errorCode != null ? errorCode.getErrorMessage() : null;
    }

    private static String resolveCode(InternalErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        Integer code = errorCode.getInternalErrorCode();
        return code != null ? code.toString() : null;
    }
}
