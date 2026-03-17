package com.collaboportal.common;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.zalando.logbook.Logbook;
import org.zalando.logbook.servlet.LogbookFilter;

import com.collaboportal.common.config.BaseConfig;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.config.CommonConfigFactory;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.config.PositionCodeConfig;
import com.collaboportal.common.context.CommonContext;
import com.collaboportal.common.exception.PageNotFoundException;
import com.collaboportal.common.filter.LogTraceIdFilter;

public class ConfigManager {

    private static final Map<Class<? extends BaseConfig>, BaseConfig> configMap = new ConcurrentHashMap<>();

    public static void setConfig(BaseConfig config) {
        if (config != null) {
            configMap.put(config.getClass(), config);
        }
    }

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

    public static CommonConfig getConfig() {
        return getConfig(CommonConfig.class);
    }

    public static LogMaskConfig getLogMaskConfig() {
        return getConfig(LogMaskConfig.class);
    }

    public static PositionCodeConfig getPositionCodeConfig() {
        return getConfig(PositionCodeConfig.class);
    }

    private volatile static FilterRegistrationBean<LogTraceIdFilter> logTraceIdFilterBean;
    private volatile static FilterRegistrationBean<LogbookFilter> logbookFilterBean;

    public static LogTraceIdFilter getLogTraceIdFilter() {
        if (logTraceIdFilterBean == null) {
            synchronized (ConfigManager.class) {
                if (logTraceIdFilterBean == null) {
                    FilterRegistrationBean<LogTraceIdFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new LogTraceIdFilter());
                    bean.addUrlPatterns("/*");
                    bean.setOrder(1);
                    setLogTraceIdFilter(bean);
                }
            }
        }
        return logTraceIdFilterBean.getFilter();
    }

    public static void setLogTraceIdFilter(FilterRegistrationBean<LogTraceIdFilter> filter) {
        logTraceIdFilterBean = filter;
    }

    public static LogbookFilter getLogbookFilter(Logbook logbook) {
        if (logbookFilterBean == null) {
            synchronized (ConfigManager.class) {
                if (logbookFilterBean == null) {
                    FilterRegistrationBean<LogbookFilter> bean = new FilterRegistrationBean<>();
                    bean.setFilter(new LogbookFilter(logbook));
                    bean.addUrlPatterns("/*");
                    bean.setOrder(1);
                    setLogbookFilter(bean);
                }
            }
        }
        return logbookFilterBean.getFilter();
    }

    public static void setLogbookFilter(FilterRegistrationBean<LogbookFilter> filter) {
        logbookFilterBean = filter;
    }

    private static final ThreadLocal<CommonContext> CTX_LOCAL = new ThreadLocal<>();

    public static void setCommonContext(CommonContext ctx) {
        CTX_LOCAL.set(ctx);
    }

    public static void clearContext() {
        CTX_LOCAL.remove();
    }

    public static CommonContext getCommonContext() {
        CommonContext ctx = CTX_LOCAL.get();
        if (ctx == null || !ctx.isValid()) {
            throw new PageNotFoundException("ページが見つかりませんまたはCommonContextが存在しないか無効です。");
        }
        return ctx;
    }

    private ConfigManager() {
    }
}
