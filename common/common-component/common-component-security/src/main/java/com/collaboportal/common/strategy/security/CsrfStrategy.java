package com.collaboportal.common.strategy.security;

import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class CsrfStrategy implements SecurityConfigStrategy {
    @Override
    public void configure(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable());
    }
}
