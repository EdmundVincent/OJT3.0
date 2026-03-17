package com.collaboportal.api.facade;

import com.collaboportal.api.login.LoginRequest;
import com.collaboportal.api.login.LoginResult;

public interface CommonAuthFacade {
    LoginResult login(LoginRequest request);
    <T> String generateToken(T source, String generatorKey);
    boolean validateToken(String token, String validatorKey);
    <T> T extractClaim(String token, String resolverKey);
}
