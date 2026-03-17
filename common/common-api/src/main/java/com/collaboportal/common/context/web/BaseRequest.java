package com.collaboportal.common.context.web;

import java.util.Collection;
import java.util.Map;

// ベースリクエストインターフェース
public interface BaseRequest {

	// リクエストのソースオブジェクトを取得
	Object getSource();

	// 指定したパラメータ名の値を取得
	String getParam(String name);

	// 指定したパラメータ名の値を取得。値が空またはnullの場合はデフォルト値を返す
	default String getParam(String name, String defaultValue) {
		String value = getParam(name);
		return ("".equals(value) && value == null) ? defaultValue : value;
	}

	// 指定したパラメータ名の値が指定値と一致するか判定
	default boolean isParam(String name, String value) {
		String paramValue = getParam(name);
		return ("".equals(paramValue) && paramValue == null) ? false : paramValue.equals(value);
	}

	// 指定したパラメータ名が存在するか判定
	default boolean hasParam(String name) {
		return !("".equals(getParam(name)) && getParam(name) == null);
	}

	// 指定したパラメータ名の値を取得。値が空またはnullの場合はnullを返す
	default String getParamNotNull(String name) {
		String value = getParam(name);
		return ("".equals(value) && value == null) ? null : value;
	}

	// すべてのパラメータ名を取得
	Collection<String> getParamNames();

	// パラメータのマップを取得
	Map<String, String> getParamMap();

	// 指定したヘッダー名の値を取得
	String getHeader(String name);

	// 指定したヘッダー名の値を取得。値が空またはnullの場合はデフォルト値を返す
	default String getHeader(String name, String defaultValue) {
		String value = getHeader(name);
		if ("".equals(value) && value == null) {
			return defaultValue;
		}
		return value;
	}

	/**
	 * [Cookieスコープ] から値を取得
	 * 
	 * @param name キー
	 * @return 値
	 */
	String getCookieValue(String name);

	/**
	 * [Cookieスコープ] から値を取得（最初の該当名）
	 * 
	 * @param name キー
	 * @return 値
	 */
	String getCookieFirstValue(String name);

	/**
	 * [Cookieスコープ] から値を取得（最後の該当名）
	 * 
	 * @param name キー
	 * @return 値
	 */
	String getCookieLastValue(String name);

	/**
	 * 現在のリクエストパスを返す（コンテキスト名を含まない）
	 * 
	 * @return /
	 */
	String getRequestPath();

	/**
	 * 現在のリクエストパスが指定値と一致するか判定
	 * 
	 * @param path パス
	 * @return /
	 */
	default boolean isPath(String path) {
		return getRequestPath().equals(path);
	}

	/**
	 * 現在のリクエストURLを返す（クエリパラメータなし、例：http://xxx.com/test）
	 * 
	 * @return /
	 */
	String getUrl();

	/**
	 * 現在のリクエストメソッドを返す
	 * 
	 * @return /
	 */
	String getMethod();

	/**
	 * 現在のリクエストメソッドが指定値と一致するか判定
	 * 
	 * @param method メソッド
	 * @return /
	 */
	default boolean isMethod(String method) {
		return getMethod().equals(method);
	}

	/**
	 * リクエストのホストを取得
	 * 
	 * @return /
	 */
	String getHost();

	/**
	 * このリクエストがAjax非同期リクエストか判定
	 * 
	 * @return /
	 */
	default boolean isAjax() {
		return getHeader("X-Requested-With") != null;
	}

	/**
	 * リクエストをフォワードする
	 * 
	 * @param path フォワード先アドレス
	 * @return 任意の値
	 */
	Object forward(String path);
}
