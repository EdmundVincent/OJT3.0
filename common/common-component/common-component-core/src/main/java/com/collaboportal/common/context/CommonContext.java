package com.collaboportal.common.context;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

public interface CommonContext {
    BaseRequest getRequest();
    BaseResponse getResponse();
    boolean matchPath(String pattern, String path);
    default boolean isValid() { return false; }
}
