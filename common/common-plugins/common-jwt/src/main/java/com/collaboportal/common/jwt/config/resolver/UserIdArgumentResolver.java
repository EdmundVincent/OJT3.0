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

@Component
public class UserIdArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_TOKEN_COOKIE_NAME = "AuthToken";

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.hasParameterAnnotation(UserId.class);
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer,
                                  NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {

        UserId userIdAnnotation = parameter.getParameterAnnotation(UserId.class);
        boolean isRequired = userIdAnnotation != null && userIdAnnotation.required();

        try {
            String authToken = WebContextUtil.getCookieValue(AUTH_TOKEN_COOKIE_NAME);
            if (authToken == null || authToken.trim().isEmpty()) {
                throw new AuthenticationException("認証トークンがリクエストに含まれていません。");
            }

            String userId = JwtClaimUtils.get(authToken, "userId", String.class).orElse(null);

            if (userId == null && isRequired) {
                throw new AuthenticationException("認証トークンにユーザーIDが含まれていません。");
            }

            return userId;

        } catch (Exception e) {
            if (isRequired) {
                if (e instanceof AuthenticationException) {
                    throw e;
                }
                throw new AuthenticationException("ユーザーIDの取得に失敗しました。", e);
            }
            return null;
        }
    }
}
