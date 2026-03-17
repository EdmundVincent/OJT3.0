package com.collaboportal.common.context.web;

import java.util.Collection;
import java.util.Map;

public interface BaseRequest {
    Object getSource();
    String getParam(String name);
    default String getParam(String name, String defaultValue) {
        String value = getParam(name);
        return ("".equals(value) && value == null) ? defaultValue : value;
    }
    default boolean isParam(String name, String value) {
        String paramValue = getParam(name);
        return ("".equals(paramValue) && paramValue == null) ? false : paramValue.equals(value);
    }
    default boolean hasParam(String name) {
        return !("".equals(getParam(name)) && getParam(name) == null);
    }
    default String getParamNotNull(String name) {
        String value = getParam(name);
        return ("".equals(value) && value == null) ? null : value;
    }
    Collection<String> getParamNames();
    Map<String, String> getParamMap();
    String getHeader(String name);
    default String getHeader(String name, String defaultValue) {
        String value = getHeader(name);
        if ("".equals(value) && value == null) {
            return defaultValue;
        }
        return value;
    }
    String getCookieValue(String name);
    String getCookieFirstValue(String name);
    String getCookieLastValue(String name);
    String getRequestPath();
    default boolean isPath(String path) {
        return getRequestPath().equals(path);
    }
    String getUrl();
    String getMethod();
    default boolean isMethod(String method) {
        return getMethod().equals(method);
    }
    String getHost();
    default boolean isAjax() {
        return getHeader("X-Requested-With") != null;
    }
    Object forward(String path);
}
