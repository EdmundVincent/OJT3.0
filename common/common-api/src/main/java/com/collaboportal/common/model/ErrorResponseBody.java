package com.collaboportal.common.model;

/**
 * エラーレスポンス用のボディクラス
 * BaseResponseBodyを継承し、エラー情報を保持する
 */
public class ErrorResponseBody extends BaseResponseBody {
	public ErrorResponseBody() {
		super();
	}
	/**
	 * コンストラクタ
	 * @param nb_err_cod 内部エラーコード
	 * @param err_msg エラーメッセージ
	 * @param err_level エラーレベル
	 */
	public ErrorResponseBody(String nb_err_cod, String err_msg, String err_level) {
		super(nb_err_cod, err_msg, err_level);
	}
}
