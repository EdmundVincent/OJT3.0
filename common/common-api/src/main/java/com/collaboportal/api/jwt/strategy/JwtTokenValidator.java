package com.collaboportal.api.jwt.strategy;

import io.jsonwebtoken.Claims;

@FunctionalInterface
public interface JwtTokenValidator {
    boolean validate(String token, Claims claims);
}
