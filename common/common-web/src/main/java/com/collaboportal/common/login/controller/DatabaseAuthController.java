package com.collaboportal.common.login.controller;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResponseBody;
import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.dispatcher.AuthDispatcher;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 認証コントローラー
 * /auth/login はデータベース認証のみを処理する。
 */
@RestController
@RequestMapping("/auth")
public class DatabaseAuthController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthController.class);

    private final AuthDispatcher dispatcher;

    public DatabaseAuthController(AuthDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(@RequestBody LoginRequest loginRequest) {

        AuthContext context = new AuthContext(CommonHolder.getRequest(), CommonHolder.getResponse());
        AuthRequest request = AuthRequest.builder()
                .type("database")
                .action("login")
                .request(CommonHolder.getRequest())
                .response(CommonHolder.getResponse())
                .attribute("loginRequest", loginRequest)
                .build();

        AuthResult result = dispatcher.dispatch(request, context);
        if (result != null && result.getStatus() == AuthResult.Status.FAILURE && result.getError() != null) {
            logger.error("Database login failed", result.getError());
            return ResponseEntity.status(500).body(LoginResponseBody.fail("500", "内部サーバーエラー"));
        }
        Object payload = result == null ? null : result.getPayload();
        if (payload instanceof LoginResponseBody body) {
            if (body.isSuccess()) {
                return ResponseEntity.ok().header("Content-Type", "application/json").body(body);
            }
            return ResponseEntity.status(401).header("Content-Type", "application/json").body(body);
        }
        return ResponseEntity.status(500).body(LoginResponseBody.fail("500", "内部サーバーエラー"));
    }
}
