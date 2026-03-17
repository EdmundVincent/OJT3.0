package com.collaboportal.api.jwt.strategy;

import io.jsonwebtoken.Claims;

@FunctionalInterface
public interface JwtClaimResolver<T> {
    T resolve(Claims claims);
}
