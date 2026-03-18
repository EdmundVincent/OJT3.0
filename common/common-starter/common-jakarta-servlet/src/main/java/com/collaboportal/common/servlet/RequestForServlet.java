package com.collaboportal.common.servlet;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.application.ApplicationInfo;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * サーブレット用リクエストラッパークラス
 * HttpServletRequestをラップし、BaseRequestインターフェースを実装する
 */
public class RequestForServlet implements BaseRequest {

	// HttpServletRequestのインスタンス
	protected HttpServletRequest request;
	private final ServletZeroCopyReader zeroCopyReader = new ServletZeroCopyReader();
	private byte[] cachedBody;
	private boolean bodyLoaded;

	/**
	 * コンストラクタ
	 * 
	 * @param request HttpServletRequestインスタンス
	 */
	public RequestForServlet(HttpServletRequest request) {
		this.request = request;
	}

	/**
	 * リクエストのソースオブジェクトを取得
	 * 
	 * @return HttpServletRequest
	 */
	@Override
	public Object getSource() {
		return request;
	}

	/**
	 * 指定したパラメータ名の値を取得
	 * 
	 * @param name パラメータ名
	 * @return パラメータ値
	 */
	@Override
	public String getParam(String name) {
		return request.getParameter(name);
	}

	/**
	 * すべてのパラメータ名を取得
	 * 
	 * @return パラメータ名のコレクション
	 */
	@Override
	public Collection<String> getParamNames() {
		return Collections.list(request.getParameterNames());
	}

	/**
	 * すべてのパラメータのマップを取得
	 * 
	 * @return パラメータ名と値のマップ
	 */
	@Override
	public Map<String, String> getParamMap() {
		// すべてのパラメータを取得
		Map<String, String[]> parameterMap = request.getParameterMap();
		Map<String, String> map = new LinkedHashMap<>(parameterMap.size());
		for (String key : parameterMap.keySet()) {
			String[] values = parameterMap.get(key);
			map.put(key, values[0]);
		}
		return map;
	}

	/**
	 * 指定したヘッダー名の値を取得
	 * 
	 * @param name ヘッダー名
	 * @return ヘッダー値
	 */
	@Override
	public String getHeader(String name) {
		byte[] headerBytes = getHeaderBytes(name);
		return headerBytes == null ? null : new String(headerBytes, StandardCharsets.UTF_8);
	}

	@Override
	public byte[] getHeaderBytes(String name) {
		String headerValue = request.getHeader(name);
		if (headerValue == null) {
			return null;
		}
		return zeroCopyReader.read(ByteBuffer.wrap(headerValue.getBytes(StandardCharsets.UTF_8)));
	}

	/**
	 * 指定したCookie名の値を取得（最初に見つかったもの）
	 * 
	 * @param name Cookie名
	 * @return Cookie値
	 */
	@Override
	public String getCookieValue(String name) {
		return request.getCookies() == null ? null
				: Arrays.stream(request.getCookies())
						.filter(cookie -> cookie.getName().equals(name))
						.findFirst()
						.map(Cookie::getValue)
						.orElse(null);
	}

	/**
	 * 指定したCookie名の値を取得（最初の該当名）
	 * 
	 * @param name Cookie名
	 * @return Cookie値
	 */
	@Override
	public String getCookieFirstValue(String name) {
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && name.equals(cookie.getName())) {
					return cookie.getValue();
				}
			}
		}
		return null;
	}

	/**
	 * 指定したCookie名の値を取得（最後の該当名）
	 * 
	 * @param name Cookie名
	 * @return Cookie値
	 */
	@Override
	public String getCookieLastValue(String name) {
		String value = null;
		Cookie[] cookies = request.getCookies();
		if (cookies != null) {
			for (Cookie cookie : cookies) {
				if (cookie != null && name.equals(cookie.getName())) {
					value = cookie.getValue();
				}
			}
		}
		return value;
	}

	/**
	 * 現在のリクエストパスを返す（コンテキスト名を含まない）
	 * 
	 * @return リクエストパス
	 */
	@Override
	public String getRequestPath() {
		return ApplicationInfo.cutPathPrefix(request.getRequestURI());
	}

	/**
	 * 現在のリクエストURLを返す（クエリパラメータなし）
	 * 
	 * @return リクエストURL
	 */
	@Override
	public String getUrl() {
		String currDomain = ConfigManager.getConfig().getCurrDomain();
		if (!StringUtils.isEmpty(currDomain)) {
			return currDomain + this.getRequestPath();
		}
		return request.getRequestURL().toString();
	}

	/**
	 * リクエストのホスト名を取得
	 * 
	 * @return ホスト名
	 */
	@Override
	public String getHost() {
		return request.getServerName();
	}

	/**
	 * リクエストを指定パスにフォワードする
	 * 
	 * @param path フォワード先アドレス
	 * @return null
	 */
	@Override
	public Object forward(String path) {
		try {
			// レスポンスオブジェクトを取得し、フォワード処理を実行
			HttpServletResponse response = (HttpServletResponse) ConfigManager.getCommonContext().getResponse()
					.Source();
			request.getRequestDispatcher(path).forward(request, response);
			return null;
		} catch (ServletException | IOException e) {
			// システムエラー発生時は例外をスロー
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
	}

	/**
	 * 現在のリクエストメソッドを返す
	 * 
	 * @return メソッド名
	 */
	@Override
	public String getMethod() {
		return request.getMethod();
	}

	@Override
	public byte[] getBodyBytes() {
		if (bodyLoaded) {
			return cachedBody == null ? null : cachedBody.clone();
		}
		try {
			byte[] source = request.getInputStream().readAllBytes();
			cachedBody = zeroCopyReader.read(ByteBuffer.wrap(source));
			bodyLoaded = true;
			return cachedBody.clone();
		} catch (IOException e) {
			throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
		}
	}

	@Override
	public String getBody() {
		byte[] body = getBodyBytes();
		return body == null ? null : new String(body, StandardCharsets.UTF_8);
	}
}
