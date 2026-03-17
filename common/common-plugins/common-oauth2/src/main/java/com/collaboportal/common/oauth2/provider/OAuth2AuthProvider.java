package com.collaboportal.common.oauth2.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.oauth2.CallbackContext;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.JwtMaintenanceUtil;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.strategy.OAuth2AuthStrategy;
import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
import com.collaboportal.common.security.core.spi.AuthProvider;
import com.collaboportal.common.security.core.spi.AuthProviderDescriptor;
import com.collaboportal.common.utils.MaintenanceModeUtil;
import com.collaboportal.common.utils.Message;


@Component
public class OAuth2AuthProvider implements AuthProvider {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthProvider.class);

    private final OAuth2AuthStrategy authStrategy;
    private final LoginStrategyRegistry loginStrategyRegistry;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public OAuth2AuthProvider(OAuth2AuthStrategy authStrategy,
            LoginStrategyRegistry loginStrategyRegistry,
            OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.authStrategy = authStrategy;
        this.loginStrategyRegistry = loginStrategyRegistry;
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    @Override
    public AuthProviderDescriptor descriptor() {
        return new AuthProviderDescriptor("oauth2", List.of("oauth2-bypass"), 90);
    }

    @Override
    public AuthResult authenticate(AuthRequest request, AuthContext context) {
        String action = request.getAction();
        if ("callback".equalsIgnoreCase(action)) {
            return handleCallback(request);
        }
        if ("logout".equalsIgnoreCase(action)) {
            return handleLogout(request);
        }
        try {
            authStrategy.authenticate(request.getRequest(), request.getResponse());
            return AuthResult.success();
        } catch (Exception e) {
            return AuthResult.failure(e);
        }
    }

    private AuthResult handleCallback(AuthRequest request) {
        BaseRequest baseRequest = request.getRequest();
        BaseResponse baseResponse = request.getResponse();

        String emailFromForm = (String) request.getAttribute("email");
        String code = (String) request.getAttribute("code");
        String state = (String) request.getAttribute("state");
        String moveUrl = (String) request.getAttribute("moveUrl");

        if (code == null && baseRequest != null) {
            code = baseRequest.getParam("code");
        }
        if (state == null && baseRequest != null) {
            state = baseRequest.getParam("state");
        }
        if (emailFromForm == null && baseRequest != null) {
            emailFromForm = baseRequest.getParam("email");
        }
        if (moveUrl == null && baseRequest != null) {
            moveUrl = baseRequest.getCookieValue("MoveUrl");
        }

        logger.info("[認証コールバック] リクエストの処理を開始します。環境フラグ: {}", ConfigManager.getConfig().getEnvFlag());

        CallbackContext ctx = CallbackContext.builder()
                .emailFromForm(emailFromForm)
                .code(code)
                .state(state)
                .moveUrl(moveUrl)
                .request(baseRequest)
                .response(baseResponse)
                .build();
        if (baseRequest != null) {
            ctx.setAuthStateToken(baseRequest.getCookieValue(Message.Cookie.AUTH_STATE));
        }

        String providerId = resolveProviderId(state, baseRequest);
        if (providerId == null || providerId.isBlank()) {
            providerId = clientRegistrationFactory.getProviderId();
        }
        OAuth2ClientRegistration registration = clientRegistrationFactory.getClientRegistration(providerId);
        if (registration == null) {
            return AuthResult.failure(new IllegalStateException("OAuth2クライアント設定が見つかりません: " + providerId));
        }

        ctx.setSelectedProviderId(providerId)
                .setIssuer(registration.getIssuer())
                .setClientId(registration.getClientId())
                .setClientSecret(registration.getClientSecret())
                .setAudience(registration.getAudience())
                .setRedirectUri(JwtMaintenanceUtil.resolveCallbackUrl());

        String strategyKey = ("0".equals(ConfigManager.getConfig().getEnvFlag())) ? "test" : "prod";
        ctx.setStrategyKey(strategyKey);

        try {
            var strategy = loginStrategyRegistry.getStrategy(strategyKey);
            if (strategy == null) {
                throw new OAuth2ConfigurationException("ログインストラテジーが未登録です。キー: " + strategyKey,
                        strategyKey);
            }
            strategy.run(ctx);
            return AuthResult.success();
        } catch (Exception e) {
            return AuthResult.failure(e);
        }
    }

    private AuthResult handleLogout(AuthRequest request) {
        BaseRequest baseRequest = request.getRequest();
        BaseResponse baseResponse = request.getResponse();
        String type = baseRequest == null ? null : baseRequest.getCookieValue("type");

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

        return AuthResult.success();
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
