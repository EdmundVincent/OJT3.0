package com.collaboportal.common.strategy.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

@FunctionalInterface
public interface SecurityConfigStrategy {
    void configure(HttpSecurity http) throws Exception;
}
