package com.collaboportal.common.oauth2.strategy.auth;

import com.collaboportal.common.context.web.BaseRequest;

@FunctionalInterface
public interface JwtAuthStrategy {
    String execute(BaseRequest request);
}
