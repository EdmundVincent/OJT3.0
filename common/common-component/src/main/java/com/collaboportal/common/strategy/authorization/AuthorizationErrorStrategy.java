package com.collaboportal.common.strategy.authorization;

@FunctionalInterface
public interface AuthorizationErrorStrategy {
    Object run(Throwable e);
}