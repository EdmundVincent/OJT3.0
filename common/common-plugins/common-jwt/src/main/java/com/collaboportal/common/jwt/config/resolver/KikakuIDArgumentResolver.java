package com.collaboportal.common.jwt.config.resolver;

import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.utils.JwtClaimUtils;
import com.collaboportal.common.utils.WebContextUtil;
import org.springframework.core.MethodParameter;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

import java.util.Collections;
import java.util.List;

@Component
public class KikakuIDArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_TOKEN_COOKIE_NAME = "AuthToken";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        // @KikakuID または @KikakuIDs アノテーションが付与されている引数をサポートする
        return parameter.hasParameterAnnotation(KikakuID.class) || parameter.hasParameterAnnotation(KikakuIDs.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        boolean isRequired = isAnnotationRequired(parameter);

        try {
            String authToken = WebContextUtil.getCookieValue(AUTH_TOKEN_COOKIE_NAME);
            if (authToken == null || authToken.trim().isEmpty()) {
                throw new AuthenticationException("認証トークンがリクエストに含まれていません。");
            }

            List<String> projectIds = JwtClaimUtils.getProjectIds(authToken);

            // 企画IDリストが取得できなかった場合のチェックを共通化
            if (projectIds == null || projectIds.isEmpty()) {
                throw new AuthenticationException("認証トークンに企画IDが含まれていません。");
            }

            // アノテーションに応じて返す値を決定
            if (parameter.hasParameterAnnotation(KikakuID.class)) {
                return projectIds.get(0);
            } else {
                return projectIds;
            }

        } catch (Exception e) {
            if (isRequired) {
                if (e instanceof AuthenticationException) {
                    throw e;
                }
                throw new AuthenticationException("企画IDの取得に失敗しました。", e);
            }
            
            // 必須でない場合は、型に応じてnullか空のリストを返す
            if (parameter.hasParameterAnnotation(KikakuIDs.class)) {
                return Collections.emptyList();
            }
            return null;
        }
    }

    /**
     * 付与されているアノテーションの required 属性をチェックする
     * @param parameter メソッド引数
     * @return 必須の場合は true
     */
    private boolean isAnnotationRequired(MethodParameter parameter) {
        KikakuID kikakuIDAnnotation = parameter.getParameterAnnotation(KikakuID.class);
        if (kikakuIDAnnotation != null) {
            return kikakuIDAnnotation.required();
        }

        KikakuIDs kikakuIDsAnnotation = parameter.getParameterAnnotation(KikakuIDs.class);
        if (kikakuIDsAnnotation != null) {
            return kikakuIDsAnnotation.required();
        }

        return false;
    }
}