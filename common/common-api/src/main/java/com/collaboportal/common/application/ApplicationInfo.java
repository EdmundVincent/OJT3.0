
package com.collaboportal.common.application;

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
        if (routePrefix != null && !routePrefix.isBlank() && !routePrefix.equals("/")
                && path != null && path.startsWith(routePrefix)) {
            path = path.substring(routePrefix.length());
        }
        return path;
    }

}
