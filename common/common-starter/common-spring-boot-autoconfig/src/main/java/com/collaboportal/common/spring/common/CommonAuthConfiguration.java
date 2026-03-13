// ファイルパス: com/collaboportal/common/spring/common/CommonAuthConfiguration.java
package com.collaboportal.common.spring.common;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.filter.AuthorizationServletFilter;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.model.VO.MoveUrl;
import com.collaboportal.common.registry.AuthorizationStrategyRegistry;
import com.collaboportal.common.strategy.CorsHandleFunction;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.utils.Message;

import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CommonAuthConfiguration implements WebMvcConfigurer {

    Logger logger = LoggerFactory.getLogger(CommonAuthConfiguration.class);
    private static final Map<String, String> AUTH_ALIAS = Map.of(
            "database-bypass", "database",
            "oauth2-bypass", "oauth2");

    @Bean
    public AuthorizationServletFilter getAuthorizationServletFilter(
            AuthorizationStrategyRegistry strategyRegistry,
            CorsHandleFunction corsHandleFunction) {

        return new AuthorizationServletFilter()
                .addInclude("/**")
                .setExcludeList(ConfigManager.getConfig().getAuthExcludedList())
                .setBeforeAuth((req, resp) -> {
                    corsHandleFunction.execute(req, resp);
                    if ("OPTIONS".equalsIgnoreCase(req.getMethod())) {
                        return;
                    }
                    logger.info("認証前処理");
                    String moveUrl = CommonHolder.getRequest().getRequestPath();
                    logger.info("リクエストパス: {}", moveUrl);
                    MoveUrl url = MoveUrl.fromValue(moveUrl);
                    if (url != null) {
                        switch (url) {
                            case ORDERLIST:
                                CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.MOVE_URL, moveUrl);
                                break;
                            case ORDERINPUT:
                                CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.MOVE_URL, moveUrl);
                                break;
                            case SITUATIONLIST:
                                CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.MOVE_URL, moveUrl);
                                break;
                            case PATIENTINFO:
                                CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.MOVE_URL, moveUrl);
                                break;
                            default:
                                break;
                        }
                    } else if ("/error".equals(moveUrl)) {
                        CookieUtil.setNoneSameSiteCookie(resp, Message.Cookie.MOVE_URL, "/#/error");
                    }
                    String r = req.getParam("r");
                    if (r != null && !r.isEmpty()) {
                        CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.PARAMETER_NAME_R, r);
                    }

                    String s = req.getParam("s");
                    if (s != null && !s.isEmpty()) {
                        CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.PARAMETER_NAME_S, s);
                    }
                    String h = req.getParam("h");
                    if (h != null && !h.isEmpty()) {
                        CookieUtil.setNoneSameSiteCookieWithLongTemp(resp, Message.Cookie.PARAMETER_NAME_H, h);
                    }
                })
                .setAuth((res, resp) -> {
                    String authorizationType = normalizeAuthorizationType(
                            CommonHolder.getRequest().getHeader("Authorization-Type"));

                    AuthorizationStrategy strategy = strategyRegistry.getStrategy(authorizationType);
                    if (strategy == null) {
                        throw new UnsupportedOperationException("サポートされていない認証タイプ: " + authorizationType);
                    }
                    strategy.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
                });

    }

    @Bean
    public FilterRegistrationBean<AuthorizationServletFilter> authorizationFilterRegistration(
            AuthorizationServletFilter authorizationServletFilter) {
        FilterRegistrationBean<AuthorizationServletFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(authorizationServletFilter);

        // インターセプトパスを設定
        registrationBean.addUrlPatterns("/*");

        // 優先度を設定（値が小さいほど前に実行される）
        registrationBean.setOrder(-101);

        // 有効/無効を設定（グレーリリースなどに使用可能）
        registrationBean.setEnabled(true);

        return registrationBean;

    }

    private String normalizeAuthorizationType(String original) {
        if (original == null) {
            return null;
        }
        String normalized = original.trim().toLowerCase(Locale.ROOT);
        return AUTH_ALIAS.getOrDefault(normalized, normalized);
    }
}
