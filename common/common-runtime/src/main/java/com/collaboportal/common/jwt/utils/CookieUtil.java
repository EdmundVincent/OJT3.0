package com.collaboportal.common.jwt.utils;

import java.io.ObjectInputFilter.Config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseResponse;

public class CookieUtil {

    private static final Logger logger = LoggerFactory.getLogger(CookieUtil.class);

    /**
     * Cookie設定処理
     *
     * @param response HTTPレスポンス
     * @param name     Cookie名
     * @param value    Cookie値
     */
    public static void setNoneSameSiteCookie(BaseResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        response.addCookie(new BaseCookie(name, value).setPath("/")
                .setMaxAge(ConfigManager.getConfig().getCookieExpiration())
                .setSameSite("None")
                .setSecure(ConfigManager.getConfig().isCookieSecure())
                .setHttpOnly(ConfigManager.getConfig().getIsCookieHttpOnly()));
    }

    public static void setSameSiteCookie(BaseResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        response.addCookie(
                new BaseCookie(name, value).setPath("/")
                        .setMaxAge(ConfigManager.getConfig().getCookieExpiration())
                        .setSameSite("Strict")
                        .setSecure(ConfigManager.getConfig().isCookieSecure()));
    }

    public static void setNoneSameSiteCookieWithLongTemp(BaseResponse response, String name, String value) {
        logger.debug("Cookieを設定します。{}={}", name, value);
        response.addCookie(new BaseCookie(name, value).setPath("/")
                .setMaxAge(ConfigManager.getConfig().getCookieExpirationLongTemp())
                .setSameSite("None")
                .setSecure(ConfigManager.getConfig().isCookieSecure())
                .setHttpOnly(ConfigManager.getConfig().getIsCookieHttpOnly()));
    }

}