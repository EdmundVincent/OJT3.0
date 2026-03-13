
package com.collaboportal.common.application;

import io.micrometer.common.util.StringUtils;

public class ApplicationInfo {

    /**
     * アプリケーションのプレフィックス
     */
    public static String routePrefix;

    /**
     * 指定されたパスからroutePrefixプレフィックスを切り取る
     * @param path 指定されたパス
     * @return /
     */
    public static String cutPathPrefix(String path) {
        if(! StringUtils.isEmpty(routePrefix) && ! routePrefix.equals("/") && path.startsWith(routePrefix)){
            path = path.substring(routePrefix.length());
        }
        return path;
    }

}