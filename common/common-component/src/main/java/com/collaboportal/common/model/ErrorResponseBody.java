package com.collaboportal.common.model;

import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * エラーレスポンス用のボディクラス
 * BaseResponseBodyを継承し、エラー情報を保持する
 */
@Getter
@NoArgsConstructor
public class ErrorResponseBody extends BaseResponseBody {
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
