package com.collaboportal.common.model;

import org.springframework.http.HttpStatus;

import com.collaboportal.common.utils.Message;

import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.Getter;

/**
 * レスポンスボディの基底クラス
 * エラー情報を保持するための共通クラス
 * シリアライズ可能なクラスとして実装
 */
@NoArgsConstructor
@Getter
public class BaseResponseBody implements Serializable {

	// 内部エラーコード（デフォルトはHTTPステータス200）
	private String nb_err_cod = Integer.toString(HttpStatus.OK.value());
	// エラーメッセージ（デフォルトはnull）
	private String err_msg = null;
	// エラーレベル（デフォルトは成功を示すレベル）
	private String err_level = Message.ERROR_LEVEL_SUCCESS;
	
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
}
