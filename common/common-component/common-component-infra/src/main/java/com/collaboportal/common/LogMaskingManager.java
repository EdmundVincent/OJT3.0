package com.collaboportal.common;

import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.utils.SensitiveDataMaskUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LogMaskingManager {

    private static final Logger logger = LoggerFactory.getLogger(LogMaskingManager.class);
    private static volatile LogMaskingManager instance;
    private final Map<String, Object> statisticsCache = new ConcurrentHashMap<>();
    private ScheduledExecutorService scheduler;
    private volatile boolean initialized = false;

    private LogMaskingManager() {
    }

    public static LogMaskingManager getInstance() {
        if (instance == null) {
            synchronized (LogMaskingManager.class) {
                if (instance == null) {
                    instance = new LogMaskingManager();
                }
            }
        }
        return instance;
    }

    public void initialize() {
        if (initialized) {
            return;
        }
        synchronized (this) {
            if (initialized) {
                return;
            }
            LogMaskConfig config = getConfig();
            if (config.isEnableMaskingStats()) {
                startStatisticsCollection();
            }
            registerShutdownHook();
            initialized = true;
        }
    }

    public LogMaskConfig getConfig() {
        try {
            return ConfigManager.getLogMaskConfig();
        } catch (Exception e) {
            return new LogMaskConfig();
        }
    }

    public void updateConfig(LogMaskConfig newConfig) {
        if (newConfig == null) {
            throw new IllegalArgumentException("設定をnullにすることはできません");
        }
        ConfigManager.setConfig(newConfig);
        if (newConfig.isEnableMaskingStats() && (scheduler == null || scheduler.isShutdown())) {
            startStatisticsCollection();
        } else if (!newConfig.isEnableMaskingStats() && scheduler != null && !scheduler.isShutdown()) {
            stopStatisticsCollection();
        }
    }

    private void startStatisticsCollection() {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            try {
                statisticsCache.put("maskOperationsTimestamp", System.currentTimeMillis());
            } catch (Exception ignored) {
            }
        }, 0, 1, TimeUnit.MINUTES);
    }

    private void stopStatisticsCollection() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdownNow();
        }
    }

    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            stopStatisticsCollection();
        }));
    }
}
