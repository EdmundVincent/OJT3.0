package com.collaboportal.common.oauth2.utils;


import java.util.ArrayList;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import org.springframework.web.util.UriComponentsBuilder;

/**
 * JWT検証ユーティリティクラス
 */
public class JwtValidationUtils {

    static Logger logger = LoggerFactory.getLogger(JwtValidationUtils.class);

    private static final List<String> COOKIE_AUTH_PATHS = new ArrayList<>(List.of("/", "/index.html"));

    public static void addCookieAuthPath(String path) {
        COOKIE_AUTH_PATHS.add(path);
    }

    public static boolean isUseCookieAuthorization(BaseRequest request) {

        String path = request.getRequestPath();
        boolean result = COOKIE_AUTH_PATHS.contains(path);
        if (result) {
            logger.debug("パス [{}] はCookie認証を使用可能です", path);
        } else {
            logger.debug("パス [{}] はCookie認証を使用できません", path);
        }
        return result;
    }

    /**
     * Cookieを設定する
     * 
     * @param response HTTPレスポンス
     * @param name     Cookie名
     * @param value    Cookie値
     */
    public static void setCookie(BaseResponse response, String name, String value) {
        response.addCookie(
                new BaseCookie(name, value).setPath("/").setMaxAge(ConfigManager.getConfig().getCookieExpiration()));
    }

    /**
     * ヘッダーからトークンを抽出する
     * 
     * @param request HTTPリクエスト
     * @return トークン
     */
    public static String extractTokenFromHeader(BaseRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }

    /**
     * Cookieからトークンを抽出する
     * 
     * @param request HTTPリクエスト
     * @return トークン
     */
    public static String extractTokenFromCookie(BaseRequest request) {
        String cookie = request.getCookieValue("AuthToken");
        return cookie;
    }

    public static String buildAuthRedirectUrl(OAuth2ProviderContext context) {
        String issuer = context.getIssuer();
        if (issuer == null || issuer.isBlank()) {
            throw new IllegalStateException("OAuth2 issuer is not configured.");
        }

        String authorizationEndpoint = issuer;
        if (!authorizationEndpoint.startsWith("http://") && !authorizationEndpoint.startsWith("https://")) {
            authorizationEndpoint = "https://" + authorizationEndpoint;
        }

        UriComponentsBuilder builder = UriComponentsBuilder.fromHttpUrl(authorizationEndpoint)
                .path("/authorize")
                .queryParam("client_id", context.getClientId())
                .queryParam("redirect_uri", context.getRedirectUri())
                .queryParam("response_type", "code")
                .queryParam("response_mode", "query");

        String scope = context.getScope();
        if (scope == null || scope.isBlank()) {
            scope = "openid profile email offline_access";
        }
        builder.queryParam("scope", scope);

        if (context.getAudience() != null && !context.getAudience().isBlank()) {
            builder.queryParam("audience", context.getAudience());
        }
        if (context.getState() != null && !context.getState().isBlank()) {
            builder.queryParam("state", context.getState());
        }
        return builder.build().toUriString();
    }

    /**
     * パスに基づいて認証戦略を決定する
     * 
     * @param request HTTPリクエスト
     * @return 認証戦略 ("cookie" または "header")
     */
    public static String decideStrategyByPath(BaseRequest request) {
        String path = request.getRequestPath();
        if ("/mr".equals(path) || "/".equals(path) || "/index.html".equals(path)) {
            return "cookie";
        } else {
            return "header";
        }
    }
}
