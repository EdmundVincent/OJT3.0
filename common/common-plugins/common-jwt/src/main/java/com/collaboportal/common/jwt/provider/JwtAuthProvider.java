package com.collaboportal.common.jwt.provider;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
import com.collaboportal.common.security.core.spi.AuthProvider;
import com.collaboportal.common.security.core.spi.AuthProviderDescriptor;
import com.collaboportal.common.jwt.utils.JwtClaimUtils;

@Component
public class JwtAuthProvider implements AuthProvider {

    @Override
    public AuthProviderDescriptor descriptor() {
        return new AuthProviderDescriptor("jwt", List.of(), 10);
    }

    @Override
    public AuthResult authenticate(AuthRequest request, AuthContext context) {
        if (request != null && "claims".equalsIgnoreCase(request.getAction())) {
            Object tokenObj = request.getAttribute("token");
            String token = tokenObj == null ? null : tokenObj.toString();
            if (token == null || token.isBlank()) {
                return AuthResult.failure(new IllegalArgumentException("token is missing"));
            }
            Map<String, Object> payload = new HashMap<>();
            payload.put("userId", JwtClaimUtils.get(token, "userId", String.class).orElse(null));
            payload.put("projectIds", JwtClaimUtils.getProjectIds(token));
            return AuthResult.success(payload);
        }
        return AuthResult.skipped();
    }
}
