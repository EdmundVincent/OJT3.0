package com.collaboportal.common.context;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

public class CommonHolder {
    public static CommonContext getContext() { return ConfigManager.getCommonContext(); }
    public static BaseRequest getRequest() { return ConfigManager.getCommonContext().getRequest(); }
    public static BaseResponse getResponse() { return ConfigManager.getCommonContext().getResponse(); }
}
