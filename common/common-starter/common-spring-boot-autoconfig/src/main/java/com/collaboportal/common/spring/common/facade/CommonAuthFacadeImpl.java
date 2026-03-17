package com.collaboportal.common.spring.common.facade;

import com.collaboportal.api.facade.CommonAuthFacade;
import com.collaboportal.api.login.LoginRequest;
import com.collaboportal.api.login.LoginResult;
import com.collaboportal.api.login.LoginService;
import com.collaboportal.common.jwt.service.JwtService;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class CommonAuthFacadeImpl implements CommonAuthFacade {

    private final ObjectProvider<LoginService> loginServiceProvider;
    private final ObjectProvider<JwtService> jwtServiceProvider;

    @Autowired
    public CommonAuthFacadeImpl(ObjectProvider<LoginService> loginServiceProvider,
                                ObjectProvider<JwtService> jwtServiceProvider) {
        this.loginServiceProvider = loginServiceProvider;
        this.jwtServiceProvider = jwtServiceProvider;
    }

    @Override
    public LoginResult login(LoginRequest request) {
        LoginService loginService = loginServiceProvider.getIfAvailable();
        if (loginService == null) {
            return LoginResult.fail("501", "LoginService not available");
        }
        return loginService.login(request);
    }

    @Override
    public <T> String generateToken(T source, String generatorKey) {
        JwtService jwtService = jwtServiceProvider.getIfAvailable();
        if (jwtService == null) {
            throw new IllegalStateException("JwtService not available");
        }
        return jwtService.generateToken(source, generatorKey);
    }

    @Override
    public boolean validateToken(String token, String validatorKey) {
        JwtService jwtService = jwtServiceProvider.getIfAvailable();
        if (jwtService == null) {
            return false;
        }
        return jwtService.validateToken(token, validatorKey);
    }

    @Override
    public <T> T extractClaim(String token, String resolverKey) {
        JwtService jwtService = jwtServiceProvider.getIfAvailable();
        if (jwtService == null) {
            return null;
        }
        return jwtService.extractClaim(token, resolverKey);
    }
}
