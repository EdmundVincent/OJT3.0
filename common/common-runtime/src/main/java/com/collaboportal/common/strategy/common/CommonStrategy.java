package com.collaboportal.common.strategy.common;

@FunctionalInterface
public interface CommonStrategy {
    void run(Object object) throws Throwable;
}