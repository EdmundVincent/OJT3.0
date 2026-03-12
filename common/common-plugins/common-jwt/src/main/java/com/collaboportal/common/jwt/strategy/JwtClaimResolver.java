package com.collaboportal.common.jwt.strategy;

import io.jsonwebtoken.Claims;

@FunctionalInterface
public interface JwtClaimResolver<T> {
    T resolve(Claims claims);
}
