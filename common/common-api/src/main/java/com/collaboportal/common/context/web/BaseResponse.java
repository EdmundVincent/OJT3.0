package com.collaboportal.common.context.web;

/**
 * レスポンスのベースインターフェース
 */
public interface BaseResponse {

    String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";

    /**
     * レスポンスのソースオブジェクトを取得
     * 
     * @return ソースオブジェクト
     */
    Object Source();

    /**
     * Cookieを削除する（パス・ドメイン指定なし）
     * 
     * @param name クッキー名
     */
    default void deleteCookie(String name) {
        // クッキー削除処理を実行
        addCookie(name, null, null, null, 0);
    }

    /**
     * Cookieを削除する（パス・ドメイン指定あり）
     * 
     * @param name   クッキー名
     * @param path   パス
     * @param domain ドメイン
     */
    default void deleteCookie(String name, String path, String domain) {
        // 指定パス・ドメインでクッキー削除処理を実行
        addCookie(name, null, path, domain, 0);
    }

    /**
     * Cookieを追加する
     * 
     * @param name    クッキー名
     * @param value   値
     * @param path    パス
     * @param domain  ドメイン
     * @param timeout 有効期間（秒）
     */
    default void addCookie(String name, String value, String path, String domain, int timeout) {
        // クッキー追加処理を実行
        this.addCookie(new BaseCookie(name, value).setPath(path).setDomain(domain).setMaxAge(timeout));
    }

    /**
     * Cookieを追加する
     * 
     * @param cookie CommonCookieインスタンス
     */
    default void addCookie(BaseCookie cookie) {
        // CommonCookieインスタンスでクッキー追加
        this.addHeader(BaseCookie.HEADER_NAME, cookie.toHeaderValue());
    }

    /**
     * レスポンスステータスコードを設定
     * 
     * @param sc ステータスコード
     * @return 自身
     */
    BaseResponse setStatus(int sc);

    /**
     * レスポンスヘッダーに値を設定
     * 
     * @param name  ヘッダー名
     * @param value 値
     * @return 自身
     */
    BaseResponse setHeader(String name, String value);

    /**
     * レスポンスヘッダーに値を追加
     * 
     * @param name  ヘッダー名
     * @param value 値
     * @return 自身
     */
    BaseResponse addHeader(String name, String value);

    /**
     * [Server]ヘッダーにサーバー名を書き込む
     * 
     * @param value サーバー名
     * @return 自身
     */
    default BaseResponse setServer(String value) {
        // Serverヘッダーを設定
        return this.setHeader("Server", value);
    }

    /**
     * リダイレクト処理
     * 
     * @param url リダイレクト先URL
     * @return 任意値
     */
    Object redirect(String url);

    /**
     * リダイレクト処理
     * 
     * @param url リダイレクト先URL
     * @return 任意値
     */
    void flush();

}
