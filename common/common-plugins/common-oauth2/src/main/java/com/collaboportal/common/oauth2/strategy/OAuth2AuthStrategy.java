package com.collaboportal.common.oauth2.strategy;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.JwtMaintenanceUtil;
import com.collaboportal.common.oauth2.chain.JwtValidationChain;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.exception.OAuth2ConfigurationException;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.oauth2.registry.JwtTokenStrategyRegistry;
import com.collaboportal.common.oauth2.utils.JwtValidationUtils;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.lang.Nullable;

import java.util.List;
import java.util.UUID;

@Component("oauth2AuthStrategy")
public class OAuth2AuthStrategy implements AuthorizationStrategy {

    private static final Logger logger = LoggerFactory.getLogger(OAuth2AuthStrategy.class);

    private final JwtService jwtService;
    private final OAuth2ClientRegistrationFactory clientRegistrationFactory;
    private final JwtTokenStrategyRegistry jwtTokenStrategyRegistry;

    private final String localAuthPage = "/testEnv";
    private static final int STATE_COOKIE_TTL_SECONDS = 300;

    /**
     * コンストラクタ。依存性注入により必要なすべてのコンポーネントを受け取ります。
     */
    public OAuth2AuthStrategy(JwtService jwtService,
            OAuth2ClientRegistrationFactory clientRegistrationFactory,
            @Nullable JwtTokenStrategyRegistry jwtTokenStrategyRegistry) {
        this.jwtService = jwtService;
        this.clientRegistrationFactory = clientRegistrationFactory;
        this.jwtTokenStrategyRegistry = (jwtTokenStrategyRegistry != null)
                ? jwtTokenStrategyRegistry
                : new JwtTokenStrategyRegistry();
        registerDefaultStrategies();
        logger.debug("OAuth2AuthStrategy の初期化が完了しました。");
    }

    /**
     * 認証戦略のコア実装。
     * 複雑なOAuth2認証とリダイレクトロジックを処理するために、検証チェーンを構築し実行します。
     */
    @Override
    public void authenticate(BaseRequest request, BaseResponse response) throws AuthenticationException {
        logger.debug("header:{}", request.getHeader("Authorization-Type"));
        logger.debug("OAuth2認証戦略の実行を開始します...");
        OAuth2ProviderContext context = OAuth2ProviderContext.builder().request(request).response(response).build();
        // 2. 責任チェーンを構築し実行します
        JwtValidationChain chain = buildValidationChain();
        boolean success = chain.execute(context);

        // 3. チェーンの実行結果に基づいて処理を行います
        if (success) {
            // チェーンが正常に実行された場合（トークンが有効または更新されたことを意味する）、認証は成功です
            logger.info("OAuth2認証が成功し、トークンは有効です。");
        } else {
            // チェーンの実行が中断された場合（リダイレクトが必要であることを意味する）、RedirectExceptionをスローします
            String redirectUrl = context.getAuthProviderUrl();
            if (redirectUrl == null || redirectUrl.isBlank()) {
                logger.error("OAuth2認証は失敗しましたが、リダイレクトURLが提供されていません。");
                throw new OAuth2ConfigurationException("OAuth2認証は失敗し、リダイレクトアドレスを特定できません。");
            }
            logger.info("OAuth2認証にはリダイレクトが必要です。ターゲットアドレス: {}", redirectUrl);
            response.redirect(redirectUrl);
            response.flush();
        }
    }

    private void registerDefaultStrategies() {
        if (!jwtTokenStrategyRegistry.contains("header")) {
            jwtTokenStrategyRegistry.register("header", JwtValidationUtils::extractTokenFromHeader);
        }
        if (!jwtTokenStrategyRegistry.contains("cookie")) {
            jwtTokenStrategyRegistry.register("cookie", JwtValidationUtils::extractTokenFromCookie);
        }
        logger.debug("デフォルトのトークン抽出戦略が登録されました: header, cookie");
    }

    private JwtValidationChain buildValidationChain() {
        JwtValidationChain chain = new JwtValidationChain();
        chain.addHandler(this::providerIdHandler);
        chain.addHandler(this::oauthContextHandler);
        chain.addHandler(this::cookieCheckHandler);
        chain.addHandler(this::tokenValidationHandler);
        return chain;
    }

    private boolean providerIdHandler(OAuth2ProviderContext context) {
        String providerId = resolveProviderId(context.getRequest());
        if (providerId == null || providerId.isBlank()) {
            providerId = clientRegistrationFactory.getProviderId();
        }
        if (providerId != null && !providerId.isEmpty()) {
            context.setSelectedProviderId(providerId);
            return true;
        }
        logger.warn("OAuth2 プロバイダーIDを特定できませんでした。");
        return false;
    }

    private boolean oauthContextHandler(OAuth2ProviderContext context) {
        OAuth2ClientRegistration clientRegistration = getClientRegistration(context);
        if (clientRegistration == null) {
            throw new OAuth2ConfigurationException("OAuth2クライアント設定が見つかりません: " + context.getSelectedProviderId(),
                    context.getSelectedProviderId());
        }
        context.setIssuer(clientRegistration.getIssuer());
        context.setClientId(clientRegistration.getClientId());
        context.setClientSecret(clientRegistration.getClientSecret());
        context.setAudience(clientRegistration.getAudience());
        List<String> scopes = clientRegistration.getScope();
        if (scopes != null && !scopes.isEmpty()) {
            context.setScope(String.join(" ", scopes));
        }
        context.setRedirectUri(JwtMaintenanceUtil.resolveCallbackUrl());
        return true;
    }

    private boolean cookieCheckHandler(OAuth2ProviderContext context) {
        logger.debug(context.getRequest().getCookieValue(Message.Cookie.AUTH));
        logger.debug(context.getRequest().toString());
        if (context.getRequest().getCookieValue(Message.Cookie.AUTH) == null
                || context.getRequest().getCookieValue(Message.Cookie.AUTH).isEmpty()) {
            logger.debug("認証Cookieが検出されませんでした。");
            if (!JwtValidationUtils.isUseCookieAuthorization(context.getRequest())) {
                logger.warn("現在のパスではCookieモードの使用が許可されていません。認証は拒否されました。");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }
        logger.debug("認証Cookieが検出されました。");
        return true;
    }

    private boolean tokenValidationHandler(OAuth2ProviderContext context) {
        String strategyKey = JwtValidationUtils.decideStrategyByPath(context.getRequest());
        String token = jwtTokenStrategyRegistry.resolveToken(context.getRequest(), strategyKey);

        if (token == null || token.isBlank()) {
            logger.debug("トークンが存在しないため、認証に失敗しました。");
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }

        context.setToken(token); // 見つかったトークンをコンテキストに保存します

        try {
            if (!jwtService.validateToken(token, JwtConstants.VALIDATE_TYPE_EXPIRED)) {
                logger.debug("トークンは期限切れです。");
                context.setAuthProviderUrl(getRedirectUrlByEnv(context));
                return false;
            }
            String updatedToken = jwtService.generateToken(token, JwtConstants.GENERATE_REFRESH_FROM_OLD);
            logger.debug("トークンの検証に成功し、更新されました。");
            JwtValidationUtils.setCookie(context.getResponse(), Message.Cookie.AUTH, updatedToken);
            return true;
        } catch (Exception e) {
            logger.warn("トークン検証エラー: {}", e.getMessage(), e);
            context.setAuthProviderUrl(getRedirectUrlByEnv(context));
            return false;
        }
    }

    private OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    private String getRedirectUrlByEnv(OAuth2ProviderContext context) {
        return "0".equals(ConfigManager.getConfig().getEnvFlag())
                ? localAuthPage
                : prepareAuthorizationRedirect(context);
    }

    private String resolveProviderId(BaseRequest request) {
        if (request == null) {
            return null;
        }
        String headerProvider = request.getHeader("Authorization-Provider");
        if (headerProvider != null && !headerProvider.isBlank()) {
            String candidate = headerProvider.trim();
            if (clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        String headerAlt = request.getHeader("X-OAuth2-Provider");
        if (headerAlt != null && !headerAlt.isBlank()) {
            String candidate = headerAlt.trim();
            if (clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        String paramProvider = request.getParam("providerId");
        if (paramProvider != null && !paramProvider.isBlank()) {
            String candidate = paramProvider.trim();
            if (clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        String paramAlt = request.getParam("authorization_provider");
        if (paramAlt != null && !paramAlt.isBlank()) {
            String candidate = paramAlt.trim();
            if (clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        String stateValue = request.getParam("state");
        if (stateValue != null && stateValue.contains("|")) {
            // state に providerId|nonce の形式で含まれている場合を想定
            String candidate = stateValue.substring(0, stateValue.indexOf('|')).trim();
            if (!candidate.isEmpty() && clientRegistrationFactory.getClientRegistration(candidate) != null) {
                return candidate;
            }
        }
        return null;
    }

    private String prepareAuthorizationRedirect(OAuth2ProviderContext context) {
        if (context.getResponse() != null) {
            if (context.getState() == null || context.getState().isBlank()) {
                String providerId = context.getSelectedProviderId();
                String nonce = UUID.randomUUID().toString().replace("-", "");
                String stateValue = (providerId != null && !providerId.isBlank())
                        ? providerId + "|" + nonce
                        : nonce;
                context.setState(stateValue);
                BaseCookie stateCookie = new BaseCookie(Message.Cookie.AUTH_STATE, stateValue)
                        .setPath("/")
                        .setMaxAge(STATE_COOKIE_TTL_SECONDS)
                        .setSameSite("Strict")
                        .setSecure(ConfigManager.getConfig().isCookieSecure())
                        .setHttpOnly(true);
                context.getResponse().addCookie(stateCookie);
            }
        }
        return JwtValidationUtils.buildAuthRedirectUrl(context);
    }
}
