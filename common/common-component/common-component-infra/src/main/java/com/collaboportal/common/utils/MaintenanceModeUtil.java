package com.collaboportal.common.utils;

import java.util.Locale;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.model.MaintenanceBody;

public final class MaintenanceModeUtil {
    private static final Logger logger = LoggerFactory.getLogger(MaintenanceModeUtil.class);
    private static final Map<String, String> AUTH_ALIAS = Map.of(
            "oauth2-bypass", "oauth2",
            "database-bypass", "database");
    private MaintenanceModeUtil() {}
    public static String resolveIndexPage() {
        CommonConfig config = ConfigManager.getConfig();
        if (config == null) {
            logger.warn("CommonConfigが取得できませんでした。インデックスページの解決をスキップします。");
            return null;
        }
        String indexPage = config.getIndexPage();
        return resolveIndexPage(indexPage, resolveAuthorizationType());
    }
    public static String resolveIndexPage(String indexPage, String authorizationType) {
        if (indexPage == null || indexPage.isBlank()) {
            return indexPage;
        }
        boolean maintenanceMode = "1".equals(MaintenanceBody.getMentFlg());
        String normalizedAuth = normalizeAuthorizationType(authorizationType);
        if (maintenanceMode) {
            if ("oauth2".equals(normalizedAuth)) {
                return indexPage.replaceAll("com$", "com:442");
            }
            if ("database".equals(normalizedAuth)) {
                return indexPage.replaceAll("com$", "com:448");
            }
        }
        return indexPage.replaceAll(":442", "").replaceAll(":448", "");
    }
    private static String resolveAuthorizationType() {
        BaseRequest request = CommonHolder.getRequest();
        if (request == null) {
            return null;
        }
        return request.getHeader("Authorization-Type");
    }
    private static String normalizeAuthorizationType(String original) {
        if (original == null) {
            return null;
        }
        String normalized = original.trim().toLowerCase(Locale.ROOT);
        return AUTH_ALIAS.getOrDefault(normalized, normalized);
    }
}
