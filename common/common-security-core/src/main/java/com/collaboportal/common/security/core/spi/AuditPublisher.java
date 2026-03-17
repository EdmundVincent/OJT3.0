package com.collaboportal.common.security.core.spi;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthResult;

@FunctionalInterface
public interface AuditPublisher {
    void publish(AuthContext context, AuthResult result);
}
