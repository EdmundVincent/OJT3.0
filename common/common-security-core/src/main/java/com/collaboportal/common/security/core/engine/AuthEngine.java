package com.collaboportal.common.security.core.engine;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;

public interface AuthEngine {
    AuthResult authenticate(AuthRequest request, AuthContext context);
}
