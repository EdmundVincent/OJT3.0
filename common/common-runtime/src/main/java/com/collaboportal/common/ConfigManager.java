package com.collaboportal.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.collaboportal.common.config.BaseConfig;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.config.CommonConfigFactory;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.config.PositionCodeConfig;
import com.collaboportal.common.context.CommonContext;
import com.collaboportal.common.exception.PageNotFoundException;

/**
 * 設定管理クラス
 * アプリケーション全体の設定とフィルタを管理する
 */
public class ConfigManager {

    // 設定クラスとそのインスタンスを保持するマップ
    private static final Map<Class<? extends BaseConfig>, BaseConfig> configMap = new ConcurrentHashMap<>();

    /**
     * 設定を登録する
     *
     * @param config 登録する設定オブジェクト
     */
    public static void setConfig(BaseConfig config) {
        if (config != null) {
            configMap.put(config.getClass(), config);
        }
    }

    /**
     * 指定された設定クラスのインスタンスを取得する
     *
     * @param <T>         設定クラスの型
     * @param configClass 取得する設定クラス
     * @return 設定オブジェクト
     */
    public static <T extends BaseConfig> T getConfig(Class<T> configClass) {
        T config = (T) configMap.get(configClass);
        if (config == null) {
            synchronized (ConfigManager.class) {
                config = (T) configMap.get(configClass);
                if (config == null) {
                    config = CommonConfigFactory.createConfig(configClass);
                    configMap.put(configClass, config);
                }
            }
        }
        return config;
    }

    /**
     * CommonConfig のインスタンスを取得する便利メソッド
     *
     * @return CommonConfig オブジェクト
     */
    public static CommonConfig getConfig() {
        return getConfig(CommonConfig.class);
    }

    /**
     * LogMaskConfig のインスタンスを取得する便利メソッド
     *
     * @return LogMaskConfig オブジェクト
     */
    public static LogMaskConfig getLogMaskConfig() {
        return getConfig(LogMaskConfig.class);
    }

    public static PositionCodeConfig getPositionCodeConfig() {
        return getConfig(PositionCodeConfig.class);
    }

    private static final ThreadLocal<CommonContext> CTX_LOCAL = new ThreadLocal<>();

    /** フィルタ入口で書き込む */
    public static void setCommonContext(CommonContext ctx) {
        CTX_LOCAL.set(ctx);
    }

    /** finally でクリアする */
    public static void clearContext() {
        CTX_LOCAL.remove();
    }

    /** 業務コードで利用する */
    public static CommonContext getCommonContext() {
        CommonContext ctx = CTX_LOCAL.get();
        if (ctx == null || !ctx.isValid()) {
            throw new PageNotFoundException("ページが見つかりませんまたはCommonContextが存在しないか無効です。");
        }
        return ctx;
    }

    private ConfigManager() {
    } // インスタンス化禁止

}
