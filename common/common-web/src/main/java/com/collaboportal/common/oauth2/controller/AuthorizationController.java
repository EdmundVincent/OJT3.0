package com.collaboportal.common.oauth2.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.dispatcher.AuthDispatcher;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;

@RestController
@RequestMapping("/auth")
public class AuthorizationController {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationController.class);

    private final AuthDispatcher dispatcher;

    public AuthorizationController(AuthDispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    @GetMapping("/callback")
    public void login(
            @RequestParam(value = "email", required = false) String emailFromForm,
            @RequestParam String code,
            @RequestParam String state,
            @CookieValue(name = "MoveUrl", required = false) String moveUrl) {

        AuthContext context = new AuthContext(CommonHolder.getRequest(), CommonHolder.getResponse());
        AuthRequest request = AuthRequest.builder()
                .type("oauth2")
                .action("callback")
                .request(CommonHolder.getRequest())
                .response(CommonHolder.getResponse())
                .attribute("email", emailFromForm)
                .attribute("code", code)
                .attribute("state", state)
                .attribute("moveUrl", moveUrl)
                .build();

        AuthResult result = dispatcher.dispatch(request, context);
        if (result != null && result.getStatus() == AuthResult.Status.FAILURE && result.getError() != null) {
            logger.error("OAuth2 callback failed", result.getError());
            throw new RuntimeException(result.getError());
        }
    }

    @GetMapping("/logout")
    public void getLogoutMethod() {
        AuthContext context = new AuthContext(CommonHolder.getRequest(), CommonHolder.getResponse());
        AuthRequest request = AuthRequest.builder()
                .type("oauth2")
                .action("logout")
                .request(CommonHolder.getRequest())
                .response(CommonHolder.getResponse())
                .build();

        AuthResult result = dispatcher.dispatch(request, context);
        if (result != null && result.getStatus() == AuthResult.Status.FAILURE && result.getError() != null) {
            logger.error("OAuth2 logout failed", result.getError());
            throw new RuntimeException(result.getError());
        }
    }
}
