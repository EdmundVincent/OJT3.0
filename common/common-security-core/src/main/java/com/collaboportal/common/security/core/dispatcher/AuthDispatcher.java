package com.collaboportal.common.security.core.dispatcher;

import com.collaboportal.common.security.core.context.AuthContext;
import com.collaboportal.common.security.core.model.AuthRequest;
import com.collaboportal.common.security.core.model.AuthResult;

public interface AuthDispatcher {
    AuthResult dispatch(AuthRequest request, AuthContext context);
}
