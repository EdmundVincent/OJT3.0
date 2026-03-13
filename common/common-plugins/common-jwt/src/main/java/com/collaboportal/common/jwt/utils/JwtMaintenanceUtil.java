package com.collaboportal.common.jwt.utils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.model.MaintenanceBody;

/**
 * JWTメンテナンス関連のユーティリティクラス
 */
public class JwtMaintenanceUtil {

    /**
     * コールバックURLを解決する
     * メンテナンスモードの場合はポート番号を442に設定
     * 
     * @return 解決されたコールバックURL
     */
    public static String resolveCallbackUrl() {
        String portSuffix = isMaintenanceMode() ? ":442" : "";
        return ConfigManager.getConfig().getCollaboportalBaseurl() + portSuffix + "/auth/callback";
    }

    /**
     * メンテナンスモードかどうかを判定する
     * 
     * @return メンテナンスモードの場合はtrue、それ以外はfalse
     */
    public static boolean isMaintenanceMode() {
        return "1".equals(MaintenanceBody.getMentFlg());
    }

    public static String resolveLogoutUrl() {
        String portSuffix = isMaintenanceMode() ? ":442" : "";
        return ConfigManager.getConfig().getCollaboportalBaseurl() + portSuffix;
    }

}
