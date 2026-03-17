package com.collaboportal.common.jwt.config.resolver;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.dispatcher.AuthDispatcher;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
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
    private static final String CLAIM_USER_ID = "userId";

    private final AuthDispatcher dispatcher;

    public UserIdArgumentResolver(AuthDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

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

            String userId = extractUserId(authToken);

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

    private String extractUserId(String authToken) {
        AuthContext context = new AuthContext(CommonHolder.getRequest(), CommonHolder.getResponse());
        AuthRequest request = AuthRequest.builder()
                .type("jwt")
                .action("claims")
                .request(CommonHolder.getRequest())
                .response(CommonHolder.getResponse())
                .attribute("token", authToken)
                .build();
        AuthResult result = dispatcher.dispatch(request, context);
        if (result == null || !result.isSuccess()) {
            return null;
        }
        Object payload = result.getPayload();
        if (payload instanceof java.util.Map<?, ?> map) {
            Object value = map.get(CLAIM_USER_ID);
            return value == null ? null : String.valueOf(value);
        }
        return null;
    }
}
