package com.collaboportal.common.security.core.spi;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;

public interface AuthProvider {

    AuthProviderDescriptor descriptor();

    AuthResult authenticate(AuthRequest request, AuthContext context);

    default boolean supports(AuthRequest request) {
        AuthProviderDescriptor descriptor = descriptor();
        return descriptor != null && descriptor.matches(request.getType());
    }
}
