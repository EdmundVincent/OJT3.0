package com.collaboportal.common.model;

import com.collaboportal.common.utils.Message;

import java.io.Serializable;

/**
 * レスポンスボディの基底クラス
 * エラー情報を保持するための共通クラス
 * シリアライズ可能なクラスとして実装
 */
public class BaseResponseBody implements Serializable {

	// 内部エラーコード（デフォルトはHTTPステータス200）
	private String nb_err_cod = "200";
	// エラーメッセージ（デフォルトはnull）
	private String err_msg = null;
	// エラーレベル（デフォルトは成功を示すレベル）
	private String err_level = Message.ERROR_LEVEL_SUCCESS;

	public BaseResponseBody() {
	}
	
	/**
	 * コンストラクタ
	 * @param nb_err_cod 内部エラーコード
	 * @param err_msg エラーメッセージ
	 * @param err_level エラーレベル
	 */
	public BaseResponseBody(String nb_err_cod, String err_msg, String err_level){
		this.nb_err_cod = nb_err_cod;
		this.err_msg = err_msg;
		this.err_level = err_level;
	}

	public String getNb_err_cod() {
		return nb_err_cod;
	}

	public String getErr_msg() {
		return err_msg;
	}

	public String getErr_level() {
		return err_level;
	}
}
