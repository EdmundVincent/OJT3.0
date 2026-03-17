package com.collaboportal.common.security.core.spi;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;

@FunctionalInterface
public interface CredentialExtractor {
    AuthRequest extract(AuthContext context);
}
