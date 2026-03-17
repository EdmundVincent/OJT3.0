package com.collaboportal.common.login.provider;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResponseBody;
import com.collaboportal.common.login.model.LoginResult;
import com.collaboportal.common.login.service.LoginService;
import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
import com.collaboportal.common.security.core.spi.AuthProvider;
import com.collaboportal.common.security.core.spi.AuthProviderDescriptor;
import com.collaboportal.common.utils.Message;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.login.strategy.DatabaseAuthStrategy;
import org.springframework.stereotype.Component;

@Component
public class DatabaseAuthProvider implements AuthProvider {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthProvider.class);

    private final DatabaseAuthStrategy authStrategy;
    private final LoginService loginService;

    public DatabaseAuthProvider(DatabaseAuthStrategy authStrategy, LoginService loginService) {
        this.authStrategy = authStrategy;
        this.loginService = loginService;
    }

    @Override
    public AuthProviderDescriptor descriptor() {
        return new AuthProviderDescriptor("database", List.of("database-bypass"), 100);
    }

    @Override
    public AuthResult authenticate(AuthRequest request, AuthContext context) {
        String action = request.getAction();
        if ("login".equalsIgnoreCase(action)) {
            return handleLogin(request);
        }
        if (authStrategy == null) {
            return AuthResult.failure(new IllegalStateException("DatabaseAuthStrategy is null"));
        }
        try {
            authStrategy.authenticate(request.getRequest(), request.getResponse());
            return AuthResult.success();
        } catch (Exception e) {
            return AuthResult.failure(e);
        }
    }

    private AuthResult handleLogin(AuthRequest request) {
        LoginRequest loginRequest = (LoginRequest) request.getAttribute("loginRequest");
        if (loginRequest == null) {
            return AuthResult.failure(LoginResponseBody.fail("400", "リクエストが不正です"),
                    new IllegalArgumentException("loginRequest is null"));
        }
        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            return AuthResult.success(LoginResponseBody.fail("400", "メールアドレスは必須項目です"));
        }
        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            return AuthResult.success(LoginResponseBody.fail("400", "パスワードは必須項目です"));
        }
        try {
            LoginResult result = loginService.login(loginRequest);
            if (result.success()) {
                if (result.token() != null && !result.token().isEmpty()) {
                    CookieUtil.setNoneSameSiteCookie(CommonHolder.getResponse(), Message.Cookie.AUTH, result.token());
                    CookieUtil.setNoneSameSiteCookie(CommonHolder.getResponse(), "role", result.role().toString());
                }
                return AuthResult.success(LoginResponseBody.ok("/index.html"));
            }
            return AuthResult.success(LoginResponseBody.fail("401", "ユーザー名またはパスワードが間違っています"));
        } catch (Exception e) {
            logger.error("login failed", e);
            return AuthResult.failure(LoginResponseBody.fail("500", "内部サーバーエラー"), e);
        }
    }
}
