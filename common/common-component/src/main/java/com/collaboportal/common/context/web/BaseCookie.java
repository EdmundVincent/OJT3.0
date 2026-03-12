
package com.collaboportal.common.context.web;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.util.StringUtils;

/**
 * クッキーモデル。Cookieが持つべきすべてのパラメータを表す
 */
public class BaseCookie {
	/**
	 * レスポンスヘッダーに書き込む際に使用するキー
	 */
	public static final String HEADER_NAME = "Set-Cookie";

	/**
	 * 名前
	 */
	private String name;

	/**
	 * 値
	 */
	private String value;

	/**
	 * 有効期間（単位：秒）、-1は一時Cookieを表し、ブラウザを閉じると自動的に削除される
	 */
	private int maxAge = -1;

	/**
	 * ドメイン
	 */
	private String domain;

	/**
	 * パス
	 */
	private String path;

	/**
	 * HTTPSプロトコル下のみ有効かどうか
	 */
	private Boolean secure = false;

	/**
	 * JSによるCookie操作を禁止するかどうか
	 */
	private Boolean httpOnly = false;

	/**
	 * サードパーティ制限レベル（Strict=完全禁止、Lax=一部許可、None=制限なし）
	 */
	private String sameSite;

	/**
	 * 追加プロパティ
	 */
	private Map<String, String> extraAttrs = new LinkedHashMap<>();

	/**
	 * コンストラクタ
	 */
	public BaseCookie() {
	}

	/**
	 * コンストラクタ
	 * 
	 * @param name  名前
	 * @param value 値
	 */
	public BaseCookie(String name, String value) {
		this.name = name;
		this.value = value;
	}

	/**
	 * 名前を取得
	 * 
	 * @return 名前
	 */
	public String getName() {
		return name;
	}

	/**
	 * 名前を設定
	 * 
	 * @param name 名前
	 * @return 自身
	 */
	public BaseCookie setName(String name) {
		this.name = name;
		return this;
	}

	/**
	 * 値を取得
	 * 
	 * @return 値
	 */
	public String getValue() {
		return value;
	}

	/**
	 * 値を設定
	 * 
	 * @param value 値
	 * @return 自身
	 */
	public BaseCookie setValue(String value) {
		this.value = value;
		return this;
	}

	/**
	 * 有効期間（秒）を取得
	 * 
	 * @return 有効期間（秒）
	 */
	public int getMaxAge() {
		return maxAge;
	}

	/**
	 * 有効期間（秒）を設定
	 * 
	 * @param maxAge 有効期間（秒）
	 * @return 自身
	 */
	public BaseCookie setMaxAge(int maxAge) {
		this.maxAge = maxAge;
		return this;
	}

	/**
	 * ドメインを取得
	 * 
	 * @return ドメイン
	 */
	public String getDomain() {
		return domain;
	}

	/**
	 * ドメインを設定
	 * 
	 * @param domain ドメイン
	 * @return 自身
	 */
	public BaseCookie setDomain(String domain) {
		this.domain = domain;
		return this;
	}

	/**
	 * パスを取得
	 * 
	 * @return パス
	 */
	public String getPath() {
		return path;
	}

	/**
	 * パスを設定
	 * 
	 * @param path パス
	 * @return 自身
	 */
	public BaseCookie setPath(String path) {
		this.path = path;
		return this;
	}

	/**
	 * HTTPSプロトコル下のみ有効かどうかを取得
	 * 
	 * @return HTTPSのみ有効
	 */
	public Boolean getSecure() {
		return secure;
	}

	/**
	 * HTTPSプロトコル下のみ有効かどうかを設定
	 * 
	 * @param secure HTTPSのみ有効
	 * @return 自身
	 */
	public BaseCookie setSecure(Boolean secure) {
		this.secure = secure;
		return this;
	}

	/**
	 * JSによるCookie操作を禁止するかどうかを取得
	 * 
	 * @return HttpOnly
	 */
	public Boolean getHttpOnly() {
		return httpOnly;
	}

	/**
	 * JSによるCookie操作を禁止するかどうかを設定
	 * 
	 * @param httpOnly HttpOnly
	 * @return 自身
	 */
	public BaseCookie setHttpOnly(Boolean httpOnly) {
		this.httpOnly = httpOnly;
		return this;
	}

	/**
	 * サードパーティ制限レベルを取得
	 * 
	 * @return SameSite
	 */
	public String getSameSite() {
		return sameSite;
	}

	/**
	 * サードパーティ制限レベルを設定
	 * 
	 * @param sameSite SameSite
	 * @return 自身
	 */
	public BaseCookie setSameSite(String sameSite) {
		this.sameSite = sameSite;
		return this;
	}

	/**
	 * 追加プロパティを取得
	 * 
	 * @return 追加プロパティ
	 */
	public Map<String, String> getExtraAttrs() {
		return extraAttrs;
	}

	/**
	 * 追加プロパティを設定
	 * 
	 * @param extraAttrs 追加プロパティ
	 * @return 自身
	 */
	public BaseCookie setExtraAttrs(Map<String, String> extraAttrs) {
		this.extraAttrs = extraAttrs;
		return this;
	}

	/**
	 * 追加プロパティを追加
	 * 
	 * @param name  プロパティ名
	 * @param value プロパティ値
	 * @return 自身
	 */
	public BaseCookie addExtraAttr(String name, String value) {
		if (extraAttrs == null) {
			extraAttrs = new LinkedHashMap<>();
		}
		this.extraAttrs.put(name, value);
		return this;
	}

	/**
	 * 追加プロパティを追加（値なし）
	 * 
	 * @param name プロパティ名
	 * @return 自身
	 */
	public BaseCookie addExtraAttr(String name) {
		return this.addExtraAttr(name, null);
	}

	/**
	 * 指定した追加プロパティを削除
	 * 
	 * @param name プロパティ名
	 * @return 自身
	 */
	public BaseCookie removeExtraAttr(String name) {
		if (extraAttrs != null) {
			this.extraAttrs.remove(name);
		}
		return this;
	}

	// toString
	@Override
	public String toString() {
		return "CommonCookie [" +
				"name=" + name +
				", value=" + value +
				", maxAge=" + maxAge +
				", domain=" + domain +
				", path=" + path
				+ ", secure=" + secure +
				", httpOnly=" + httpOnly +
				", sameSite=" + sameSite +
				", extraAttrs=" + extraAttrs +
				"]";
	}

	/**
	 * パスがnullの場合、デフォルト値を設定
	 */
	public void builder() {
		if (path == null) {
			path = "/";
		}
	}

	/**
	 * Set-Cookieヘッダー用の値に変換
	 * 
	 * @return ヘッダー値
	 */
	public String toHeaderValue() {
		this.builder();

		if ("".equals(name) || name == null) {
			throw new RuntimeException("nameは空にできません");
		}
		if (value != null && value.contains(";")) {
			throw new RuntimeException("無効なValue：" + value);
		}

		StringBuilder sb = new StringBuilder();
		sb.append(name).append("=").append(value);

		if (maxAge >= 0) {
			sb.append("; Max-Age=").append(maxAge);
			String expires;
			if (maxAge == 0) {
				expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
			} else {
				expires = OffsetDateTime.now().plusSeconds(maxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
			}
			sb.append("; Expires=").append(expires);
		}
		if (!StringUtils.isEmpty(domain)) {
			sb.append("; Domain=").append(domain);
		}
		if (!StringUtils.isEmpty(path)) {
			sb.append("; Path=").append(path);
		}
		if (secure) {
			sb.append("; Secure");
		}
		if (httpOnly) {
			sb.append("; HttpOnly");
		}
		if (!StringUtils.isEmpty(sameSite)) {
			sb.append("; SameSite=").append(sameSite);
		}

		if (extraAttrs != null) {
			extraAttrs.forEach((k, v) -> {
				if (StringUtils.isEmpty(v)) {
					sb.append("; ").append(k);
				} else {
					sb.append("; ").append(k).append("=").append(v);
				}
			});
		}

		return sb.toString();
	}

}
