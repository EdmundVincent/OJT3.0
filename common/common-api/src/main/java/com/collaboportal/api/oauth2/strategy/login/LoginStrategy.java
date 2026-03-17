package com.collaboportal.api.oauth2.strategy.login;

@FunctionalInterface
public interface LoginStrategy {
    void run(Object obj);
}
