package com.collaboportal.common.strategy.authorization;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;

@FunctionalInterface
public interface AuthorizationStrategy {
    void authenticate(BaseRequest req, BaseResponse resp) throws AuthenticationException;
}
