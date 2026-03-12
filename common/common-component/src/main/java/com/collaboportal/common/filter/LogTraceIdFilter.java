package com.collaboportal.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.MDC;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.UUID;

import jakarta.servlet.http.Cookie;

/**
 * ログにtraceIDとIPアドレスとトラッキングクッキーの値を設定し、トラッキングクッキーを設定するフィルター
 * traceIDはリクエストごとに生成する値であり、ログに記録する。
 * IPアドレスはリクエストのIPアドレスである。
 * トラッキングクッキーはクライアントの追跡を補助するためのクッキーである。IPアドレスだけでは追跡できない場合があるため追加。
 * traceIDとトラッキングクッキーは認証に使用せず、流出しても問題が無い範囲で利用すること
 */
public class LogTraceIdFilter implements Filter {

    public LogTraceIdFilter(){
        logger.debug("LogTraceIdFilterのロードが完了しました");
    }

    // ログに使用するTrace IDのヘッダー名
    private static final String TRACE_ID = "X-Track";
    // ログに使用するIPアドレスのキー
    private static final String IP_ADDRESS = "ipAddress";
    // トラッキングクッキーのキー
    private static final String TRACKING_COOKIE_VALUE = "trackingCookieValue";
    // ログ出力のためのクラス
    Logger logger = LoggerFactory.getLogger(LogTraceIdFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
            throws IOException, ServletException {

        // HttpServletRequestかどうかのチェックを行うメソッドを呼び出す
        if (!isHttpServlet(request, response)) {
            logger.debug("HttpServletRequestまたはHttpServletResponseではないリクエストを検出しました。MDC処理をスキップします。");
            filterchain.doFilter(request, response); // 次のフィルターに進む
            return; // 早期リターン
        }
        // requestをHttpServletRequestにキャスト
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        // リクエストからtraceIDとIPアドレスとトラッキングクッキーを決定
        String traceId = determineTraceId(httpRequest);
        String ipAddress = determineIpAddress(httpRequest);
        String trackingCookie = getTrackingCookieValue(httpRequest, httpResponse);// トラッキングクッキーを生成してresponseに追加
        // MDC（Mapped Diagnostic Context）にtraceID、IPアドレス、トラッキングクッキーの値をセット
        MDC.put(TRACE_ID, traceId);
        MDC.put(IP_ADDRESS, ipAddress);
        MDC.put(TRACKING_COOKIE_VALUE, trackingCookie);
        try {
            // フィルタチェーンを続行
            filterchain.doFilter(request, response);
        } finally {
            // リクエスト終了後に、MDCをクリア
            MDC.clear();
        }
    }

    /**
     * HttpServletRequestかつHttpServletResponseかどうかを判定するメソッド
     * 
     * @param request  リクエスト
     * @param response レスポンス
     * @return HttpServletRequestかつHttpServletResponseでない場合はtrueを返す
     */
    private boolean isHttpServlet(ServletRequest request, ServletResponse response) {
        if (!((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse))) {
            return false; // HttpServletRequestでない場合はfalseを返す
        }
        return true; // HttpServletRequestかつHttpServletResponseの場合はtrueを返す
    }

    /**
     * traceIDを決定
     * 
     * @param request リクエスト
     * @return traceID
     */
    private String determineTraceId(HttpServletRequest request) {
        // ランダムなUUIDを生成
        String traceId = UUID.randomUUID().toString();
        // リクエストヘッダーからtraceIDを取得
        String headerTraceId = request.getHeader(TRACE_ID);
        // リクエストヘッダーにtraceIDが設定されている場合、その値を使用
        if (headerTraceId != null && !headerTraceId.isEmpty()) {
            traceId = headerTraceId;
        }
        return traceId;
    }

    /**
     * リクエストからIPアドレスを取得
     * 
     * @param request リクエスト
     * @return IPアドレス
     */
    private String determineIpAddress(HttpServletRequest request) {
        // リクエストのIPアドレスを取得
        String ipAddress = request.getRemoteAddr();
        // X-Forwarded-ForヘッダーからIPアドレスを取得
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        // X-Forwarded-Forヘッダーが設定されている場合、その値を使用
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            ipAddress = xForwardedFor.split(",")[0];
        }

        return ipAddress;
    }

    /**
     * トラッキングクッキーの値を取得
     * 
     * @param request  リクエスト
     * @param response レスポンス
     * @return トラッキングクッキーの値
     */
    private String getTrackingCookieValue(HttpServletRequest request, HttpServletResponse response) {
        // トラッキングクッキーが存在する場合、有効なクッキーを探す
        // 存在しない場合、新しく生成してレスポンスに追加
        Cookie[] cookies = request.getCookies();
        Cookie retcookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TRACKING_COOKIE_VALUE)
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty()) {
                    retcookie = cookie;
                }
            }
        }
        if (retcookie == null) {
            retcookie = new Cookie(TRACKING_COOKIE_VALUE, UUID.randomUUID().toString());
        }
        retcookie.setHttpOnly(true); // JavaScriptからのアクセス防止
        retcookie.setMaxAge(60 * 60 * 24 * 30); // 30日間有効
        retcookie.setPath("/"); // サイト全体で有効
        retcookie.setAttribute("SameSite", "Strict");
        // クッキーをレスポンスに追加
        response.addCookie(retcookie);
        return retcookie.getValue(); // 有効なクッキーがあればその値を返す
    }


}
