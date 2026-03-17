package com.collaboportal.common.strategy;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

@FunctionalInterface
public interface CorsHandleFunction {
    void execute(BaseRequest request, BaseResponse response);
}
