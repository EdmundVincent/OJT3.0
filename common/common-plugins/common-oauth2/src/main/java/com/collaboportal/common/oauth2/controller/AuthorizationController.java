package com.collaboportal.common.oauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.oauth2.CallbackContext;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtMaintenanceUtil;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.utils.MaintenanceModeUtil;
import com.collaboportal.common.utils.Message;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private final LoginStrategyRegistry loginStrategyRegistry;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    public AuthorizationController(LoginStrategyRegistry loginStrategyRegistry,
            OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.loginStrategyRegistry = loginStrategyRegistry;
        this.clientRegistrationFactory = clientRegistrationFactory;
        logger.debug("AuthorizationControllerの初期化が完了し、リクエストの監視を開始しました");
    }

    @GetMapping("/callback")
    public void login(
            @RequestParam(value = "email", required = false) String emailFromForm,
            @RequestParam String code,
            @RequestParam String state,
            @CookieValue(name = "MoveUrl", required = false) String moveUrl) {

        logger.info("[認証コールバック] リクエストの処理を開始します。環境フラグ: {}", ConfigManager.getConfig().getEnvFlag());
        logger.debug("[認証コールバック] 受信パラメータ - メール: {}, 認証コード: {}, 状態: {}, リダイレクトURL: {}",
                emailFromForm, code, state, moveUrl);

        BaseRequest request = CommonHolder.getRequest();
        BaseResponse response = CommonHolder.getResponse();

        CallbackContext context = CallbackContext.builder()
                .emailFromForm(emailFromForm)
                .code(code)
                .state(state)
                .moveUrl(moveUrl)
                .request(request)
                .response(response)
                .build();
        context.setAuthStateToken(request.getCookieValue(Message.Cookie.AUTH_STATE));

        String providerId = resolveProviderId(state, request);
        if (providerId == null || providerId.isBlank()) {
            providerId = clientRegistrationFactory.getProviderId();
        }
        OAuth2ClientRegistration registration = clientRegistrationFactory.getClientRegistration(providerId);
        if (registration == null) {
            throw new IllegalStateException("OAuth2クライアント設定が見つかりません: " + providerId);
        }

        context.setSelectedProviderId(providerId)
                .setIssuer(registration.getIssuer())
                .setClientId(registration.getClientId())
                .setClientSecret(registration.getClientSecret())
                .setAudience(registration.getAudience())
                .setRedirectUri(JwtMaintenanceUtil.resolveCallbackUrl());

        String strategyKey = ("0".equals(ConfigManager.getConfig().getEnvFlag())) ? "test" : "prod";
        context.setStrategyKey(strategyKey);
        logger.debug("[認証コールバック] 使用するステージング: {}", strategyKey);

        try {
            logger.info("[認証コールバック] ログイン戦略の実行を開始します");
            var strategy = loginStrategyRegistry.getStrategy(strategyKey);
            if (strategy == null) {
                throw new OAuth2ConfigurationException("ログインストラテジーが未登録です。キー: " + strategyKey,
                        strategyKey);
            }
            strategy.run(context);
            logger.info("[認証コールバック] ログイン戦略の実行が完了しました");
        } catch (Exception e) {
            logger.error("[認証コールバック] ログイン戦略の実行中に例外が発生しました: {}", e.getMessage(), e);
            throw e;
        }
    }

    @GetMapping("/logout")
    public void getLogoutMethod() {
        BaseRequest baseRequest = CommonHolder.getRequest();
        BaseResponse baseResponse = CommonHolder.getResponse();

        String type = baseRequest.getCookieValue("type");

        invalidateSession(baseRequest);
        clearAuthCookies(baseResponse);

        if ("0".equals(type)) {
            String indexPage = ConfigManager.getConfig().getIndexPage();
            String resolvedBase = MaintenanceModeUtil.resolveIndexPage(indexPage, "database");
            if (resolvedBase != null && resolvedBase.endsWith("/")) {
                baseResponse.redirect(resolvedBase + "login.html");
            } else if (resolvedBase != null) {
                baseResponse.redirect(resolvedBase + "/login.html");
            } else {
                baseResponse.redirect("/login.html");
            }
        } else {
            baseResponse.redirect(MaintenanceModeUtil.resolveIndexPage());
        }
    }

    private void invalidateSession(BaseRequest baseRequest) {
        if (baseRequest == null) {
            return;
        }
        Object source = baseRequest.getSource();
        if (source instanceof HttpServletRequest httpServletRequest) {
            HttpSession session = httpServletRequest.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }

    private void clearAuthCookies(BaseResponse baseResponse) {
        if (baseResponse == null) {
            return;
        }
        baseResponse.deleteCookie("AuthToken");
        baseResponse.deleteCookie("XSRF-TOKEN");
        baseResponse.deleteCookie("type");
    }

    private String resolveProviderId(String state, BaseRequest request) {
        if (state != null && state.contains("|")) {
            String candidate = state.substring(0, state.indexOf('|')).trim();
            if (!candidate.isEmpty() && clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        if (request != null) {
            String headerProvider = request.getHeader("Authorization-Provider");
            if (headerProvider != null && !headerProvider.isBlank()
                    && clientRegistrationFactory.getClientRegistration(headerProvider.trim()) != null) {
                return headerProvider.trim();
            }
            String headerAlt = request.getHeader("X-OAuth2-Provider");
            if (headerAlt != null && !headerAlt.isBlank()
                    && clientRegistrationFactory.getClientRegistration(headerAlt.trim()) != null) {
                return headerAlt.trim();
            }
            String paramProvider = request.getParam("providerId");
            if (paramProvider != null && !paramProvider.isBlank()
                    && clientRegistrationFactory.getClientRegistration(paramProvider.trim()) != null) {
                return paramProvider.trim();
            }
        }
        return null;
    }

}
