
package com.collaboportal.common.context;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
public class CommonHolder {

	/**
	 * 現在のリクエストのCommonContext（共通コンテキスト）オブジェクトを取得します。
	 * 
	 * @see CommonContext
	 * 
	 * @return CommonContextインスタンス
	 */
	public static CommonContext getContext() {
		return ConfigManager.getCommonContext();
	}

	/**
	 * 現在のリクエストのRequestラッパーオブジェクトを取得します。
	 * 
	 * @see BaseRequest
	 * 
	 * @return BaseRequestインスタンス
	 */
	public static BaseRequest getRequest() {
		return ConfigManager.getCommonContext().getRequest();
	}

	/**
	 * 現在のリクエストのResponseラッパーオブジェクトを取得します。
	 * 
	 * @see BaseResponse
	 * 
	 * @return BaseResponseインスタンス
	 */
	public static BaseResponse getResponse() {
		return ConfigManager.getCommonContext().getResponse();
	}


}
