package com.collaboportal.common.jwt.provider;

import java.util.List;

import org.springframework.stereotype.Component;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;
import com.collaboportal.common.security.core.spi.AuthProvider;
import com.collaboportal.common.security.core.spi.AuthProviderDescriptor;

@Component
public class JwtAuthProvider implements AuthProvider {

    @Override
    public AuthProviderDescriptor descriptor() {
        return new AuthProviderDescriptor("jwt", List.of(), 10);
    }

    @Override
    public AuthResult authenticate(AuthRequest request, AuthContext context) {
        return AuthResult.skipped();
    }
}
