package com.collaboportal.common.jwt.strategy;

@FunctionalInterface
public interface JwtTokenGenerator<T> {
    String generate(T source);
}