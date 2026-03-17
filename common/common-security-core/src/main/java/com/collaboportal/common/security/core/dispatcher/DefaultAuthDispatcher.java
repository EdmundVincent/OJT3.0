package com.collaboportal.common.security.core.dispatcher;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
import com.collaboportal.common.security.core.spi.AuthProvider;
import com.collaboportal.common.security.core.spi.AuthProviderDescriptor;

public class DefaultAuthDispatcher implements AuthDispatcher {

    private static final Logger logger = LoggerFactory.getLogger(DefaultAuthDispatcher.class);

    private final List<AuthProvider> providers;
    private final Map<String, String> aliasMap = new HashMap<>();

    public DefaultAuthDispatcher(List<AuthProvider> providers) {
        this.providers = providers == null ? new ArrayList<>() : new ArrayList<>(providers);
        this.providers.sort(Comparator.comparingInt(this::priorityOf).reversed());
        aliasMap.put("database-bypass", "database");
        aliasMap.put("oauth2-bypass", "oauth2");
    }

    @Override
    public AuthResult dispatch(AuthRequest request, AuthContext context) {
        if (request == null) {
            return AuthResult.failure(new IllegalArgumentException("request is null"));
        }
        String normalized = normalizeType(request.getType());
        AuthProvider matched = null;
        if (normalized != null) {
            for (AuthProvider provider : providers) {
                AuthProviderDescriptor descriptor = provider.descriptor();
                if (descriptor != null && descriptor.matches(normalized)) {
                    matched = provider;
                    break;
                }
            }
        }
        if (matched == null) {
            for (AuthProvider provider : providers) {
                try {
                    if (provider.supports(request)) {
                        matched = provider;
                        break;
                    }
                } catch (Exception e) {
                    logger.debug("provider supports check failed: {}", provider, e);
                }
            }
        }
        if (matched == null) {
            return AuthResult.failure(new IllegalStateException("No AuthProvider for type: " + normalized));
        }
        return matched.authenticate(request, context);
    }

    private int priorityOf(AuthProvider provider) {
        AuthProviderDescriptor descriptor = provider.descriptor();
        return descriptor == null ? 0 : descriptor.getPriority();
    }

    private String normalizeType(String type) {
        if (type == null) {
            return null;
        }
        String normalized = type.trim().toLowerCase(Locale.ROOT);
        return aliasMap.getOrDefault(normalized, normalized);
    }
}
