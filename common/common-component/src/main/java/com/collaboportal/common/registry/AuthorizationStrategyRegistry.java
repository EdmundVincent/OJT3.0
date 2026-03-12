package com.collaboportal.common.registry;

import org.springframework.stereotype.Component;

import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class AuthorizationStrategyRegistry {

    private final Map<String, AuthorizationStrategy> strategies = new ConcurrentHashMap<>();

    public AuthorizationStrategyRegistry(Map<String, AuthorizationStrategy> strategyMap) {
        this.strategies.putAll(strategyMap);
    }

    public AuthorizationStrategy getStrategy(String type) {
        return strategies.get(type + "AuthStrategy");
    }
}
