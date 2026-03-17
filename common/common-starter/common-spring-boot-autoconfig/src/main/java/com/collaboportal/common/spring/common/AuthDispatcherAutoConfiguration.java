package com.collaboportal.common.spring.common;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.ServiceLoader;
import java.util.Set;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.collaboportal.common.security.core.dispatcher.AuthDispatcher;
import com.collaboportal.common.security.core.dispatcher.DefaultAuthDispatcher;
import com.collaboportal.common.security.core.spi.AuthProvider;

@Configuration
public class AuthDispatcherAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean(AuthDispatcher.class)
    public AuthDispatcher authDispatcher(ObjectProvider<List<AuthProvider>> providerListProvider) {
        List<AuthProvider> providers = new ArrayList<>();
        List<AuthProvider> springProviders = providerListProvider.getIfAvailable();
        if (springProviders != null) {
            providers.addAll(springProviders);
        }
        if (providers.isEmpty()) {
            Set<String> seen = new HashSet<>();
            for (AuthProvider provider : ServiceLoader.load(AuthProvider.class)) {
                if (provider == null) {
                    continue;
                }
                if (seen.add(provider.getClass().getName())) {
                    providers.add(provider);
                }
            }
        }
        return new DefaultAuthDispatcher(providers);
    }
}
