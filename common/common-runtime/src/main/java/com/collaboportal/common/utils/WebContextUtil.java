package com.collaboportal.common.utils;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;

public class WebContextUtil {

    public static String getHeader(String name) {
        BaseRequest request = CommonHolder.getRequest();
        return request.getHeader(name);
    }

    public static String getCookieValue(String name) {
        BaseRequest request = CommonHolder.getRequest();
        return request.getCookieValue(name);
    }

    public static String getParameters(String name) {
        BaseRequest request = CommonHolder.getRequest();
        return request.getParam(name);
    }

}
