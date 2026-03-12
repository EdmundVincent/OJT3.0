package com.collaboportal.common.jwt.strategy;

import io.jsonwebtoken.Claims;

@FunctionalInterface
public interface JwtTokenValidator {
    boolean validate(String token, Claims claims);
}
