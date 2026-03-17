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

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@Component
public class KikakuIDArgumentResolver implements HandlerMethodArgumentResolver {

    private static final String AUTH_TOKEN_COOKIE_NAME = "AuthToken";
    private static final String CLAIM_PROJECT_IDS = "projectIds";

    private final AuthDispatcher dispatcher;

    public KikakuIDArgumentResolver(AuthDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

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

            List<String> projectIds = extractProjectIds(authToken);

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

    private List<String> extractProjectIds(String authToken) {
        Map<String, Object> claims = dispatchClaims(authToken);
        if (claims == null) {
            return Collections.emptyList();
        }
        Object value = claims.get(CLAIM_PROJECT_IDS);
        if (value == null) {
            return Collections.emptyList();
        }
        if (value instanceof List<?> list) {
            List<String> result = new ArrayList<>(list.size());
            for (Object item : list) {
                if (item != null) {
                    result.add(String.valueOf(item));
                }
            }
            return result;
        }
        String raw = String.valueOf(value).trim();
        if (raw.isEmpty()) {
            return Collections.emptyList();
        }
        String[] parts = raw.split(",");
        List<String> result = new ArrayList<>(parts.length);
        for (String part : parts) {
            String trimmed = part.trim();
            if (!trimmed.isEmpty()) {
                result.add(trimmed);
            }
        }
        return result;
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> dispatchClaims(String authToken) {
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
        if (payload instanceof Map<?, ?> map) {
            return (Map<String, Object>) map;
        }
        return null;
    }
}
