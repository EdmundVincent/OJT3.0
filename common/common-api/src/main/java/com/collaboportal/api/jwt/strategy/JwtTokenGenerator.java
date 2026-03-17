package com.collaboportal.api.jwt.strategy;

@FunctionalInterface
public interface JwtTokenGenerator<T> {
    String generate(T source);
}
