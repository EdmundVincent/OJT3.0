package com.collaboportal.common.filter;

import java.util.List;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;

@SuppressWarnings("rawtypes")
public interface AuthFilter {
    AuthFilter addInclude(String... patterns);
    AuthFilter addExclude(String... patterns);
    AuthFilter setIncludeList(List<String> pathList);
    AuthFilter setExcludeList(List<String> pathList);
    AuthFilter setAuth(AuthorizationStrategy auth);
    AuthFilter setBeforeAuth(AuthorizationStrategy beforeAuth);
    AuthFilter setError(AuthorizationErrorStrategy error);
}
