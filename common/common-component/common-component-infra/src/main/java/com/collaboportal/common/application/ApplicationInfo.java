package com.collaboportal.common.application;

import io.micrometer.common.util.StringUtils;

public class ApplicationInfo {
    public static String routePrefix;
    public static String cutPathPrefix(String path) {
        if (!StringUtils.isEmpty(routePrefix) && !routePrefix.equals("/") && path.startsWith(routePrefix)) {
            path = path.substring(routePrefix.length());
        }
        return path;
    }
}
