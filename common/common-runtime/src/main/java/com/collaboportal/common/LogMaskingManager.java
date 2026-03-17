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

/**
 * ログマスキング管理器
 * ログマスキング機能、設定および統計情報を統合管理する
 */
public class LogMaskingManager {

    private static final Logger logger = LoggerFactory.getLogger(LogMaskingManager.class);

    // シングルトンインスタンス
    private static volatile LogMaskingManager instance;

    // 統計情報キャッシュ
    private final Map<String, Object> statisticsCache = new ConcurrentHashMap<>();

    // 定時タスク実行器
    private ScheduledExecutorService scheduler;

    // 初期化済みかどうか
    private volatile boolean initialized = false;

    // プライベートコンストラクタ
    private LogMaskingManager() {
        // プライベートコンストラクタで直接インスタンス化を防止
    }

    /**
     * シングルトンインスタンスを取得
     */
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

    /**
     * マスキング管理器を初期化
     */
    public void initialize() {
        if (initialized) {
            logger.debug("ログマスキング管理器は既に初期化されています");
            return;
        }

        synchronized (this) {
            if (initialized) {
                return;
            }

            try {
                logger.info("ログマスキング管理器を初期化中...");

                // 設定を取得
                LogMaskConfig config = getConfig();

                // 統計情報収集を開始
                if (config.isEnableMaskingStats()) {
                    startStatisticsCollection();
                }

                // JVMシャットダウンフックを登録
                registerShutdownHook();

                initialized = true;
                logger.info("ログマスキング管理器の初期化が完了しました - マスキング機能: {}, AOPマスキング: {}",
                        config.isEnableLogMasking(), config.isEnableAopMasking());

            } catch (Exception e) {
                logger.error("ログマスキング管理器の初期化に失敗しました", e);
                throw new RuntimeException("ログマスキング管理器の初期化に失敗しました", e);
            }
        }
    }

    /**
     * マスキング設定を取得
     */
    public LogMaskConfig getConfig() {
        try {
            return ConfigManager.getLogMaskConfig();
        } catch (Exception e) {
            logger.warn("マスキング設定を取得できません、デフォルト設定を使用します: {}", e.getMessage());
            return new LogMaskConfig();
        }
    }

    /**
     * 設定を動的更新
     */
    public void updateConfig(LogMaskConfig newConfig) {
        if (newConfig == null) {
            throw new IllegalArgumentException("設定をnullにすることはできません");
        }

        try {
            ConfigManager.setConfig(newConfig);
            logger.info("マスキング設定が更新されました: {}", newConfig.toString());

            // 統計収集戦略を更新
            if (newConfig.isEnableMaskingStats() && (scheduler == null || scheduler.isShutdown())) {
                startStatisticsCollection();
            } else if (!newConfig.isEnableMaskingStats() && scheduler != null && !scheduler.isShutdown()) {
                stopStatisticsCollection();
            }

        } catch (Exception e) {
            logger.error("マスキング設定の更新に失敗しました", e);
            throw new RuntimeException("マスキング設定の更新に失敗しました", e);
        }
    }

    /**
     * 機密情報検出をテスト
     */
    public boolean testSensitiveDataDetection(String text) {
        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        LogMaskConfig config = getConfig();
        if (!config.isEnableLogMasking()) {
            return false;
        }

        return SensitiveDataMaskUtil.containsSensitiveData(text);
    }

    /**
     * 手動でマスキング処理を実行
     */
    public String maskSensitiveData(String text) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        LogMaskConfig config = getConfig();
        if (!config.isEnableLogMasking()) {
            return text;
        }

        if (config.isTextTooLongForMasking(text)) {
            return "<text_too_long_for_masking>";
        }

        return SensitiveDataMaskUtil.maskSensitiveData(text);
    }

    /**
     * 機密情報タイプ分析を取得
     */
    public Map<String, Boolean> analyzeSensitiveDataTypes(String text) {
        return SensitiveDataMaskUtil.getSensitiveDataTypes(text);
    }

    /**
     * 総合統計情報を取得
     */
    public Map<String, Object> getComprehensiveStatistics() {
        Map<String, Object> stats = new ConcurrentHashMap<>();

        try {

            // 設定情報
            LogMaskConfig config = getConfig();
            stats.put("masking_enabled", config.isEnableLogMasking());
            stats.put("aop_masking_enabled", config.isEnableAopMasking());
            stats.put("mask_method_parameters", config.isMaskMethodParameters());
            stats.put("mask_return_values", config.isMaskMethodReturnValues());
            stats.put("mask_exceptions", config.isMaskExceptionMessages());

            // パフォーマンス情報
            stats.put("max_text_length", config.getMaxTextLengthForMasking());
            stats.put("custom_keywords_count", config.getCustomSensitiveKeywords().size());

            // システム情報
            stats.put("manager_initialized", initialized);
            stats.put("statistics_collection_active", scheduler != null && !scheduler.isShutdown());

        } catch (Exception e) {
            logger.error("統計情報の取得に失敗しました", e);
            stats.put("error", "統計情報の取得に失敗しました: " + e.getMessage());
        }

        return stats;
    }

    /**
     * すべての統計情報をリセット
     */
    public void resetAllStatistics() {
        try {
            statisticsCache.clear();
            logger.info("すべてのマスキング統計情報がリセットされました");
        } catch (Exception e) {
            logger.error("統計情報のリセットに失敗しました", e);
        }
    }

    /**
     * 統計情報収集を開始
     */
    private void startStatisticsCollection() {
        if (scheduler != null && !scheduler.isShutdown()) {
            return;
        }

        scheduler = Executors.newScheduledThreadPool(1, r -> {
            Thread t = new Thread(r, "LogMaskingStatistics");
            t.setDaemon(true);
            return t;
        });

        // 5分ごとに統計情報を収集
        scheduler.scheduleAtFixedRate(() -> {
            try {
                Map<String, Object> stats = getComprehensiveStatistics();
                statisticsCache.putAll(stats);
                logger.debug("統計情報収集が完了しました: {}", stats);
            } catch (Exception e) {
                logger.warn("統計情報収集に失敗しました: {}", e.getMessage());
            }
        }, 5, 5, TimeUnit.MINUTES);

        logger.info("統計情報収集が開始されました");
    }

    /**
     * 統計情報収集を停止
     */
    private void stopStatisticsCollection() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(5, TimeUnit.SECONDS)) {
                    scheduler.shutdownNow();
                }
                logger.info("統計情報収集が停止されました");
            } catch (InterruptedException e) {
                scheduler.shutdownNow();
                Thread.currentThread().interrupt();
            }
        }
    }

    /**
     * JVMシャットダウンフックを登録
     */
    private void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("ログマスキング管理器を終了中...");

            // 最終統計情報を出力
            try {
                Map<String, Object> finalStats = getComprehensiveStatistics();
                logger.info("最終マスキング統計情報: {}", finalStats);
            } catch (Exception e) {
                logger.warn("最終統計情報の出力に失敗しました: {}", e.getMessage());
            }

            // 統計収集を停止
            stopStatisticsCollection();

            logger.info("ログマスキング管理器が終了しました");
        }, "LogMaskingManagerShutdown"));
    }

    /**
     * ヘルスチェック
     */
    public Map<String, Object> healthCheck() {
        Map<String, Object> health = new ConcurrentHashMap<>();

        try {
            health.put("status", initialized ? "UP" : "DOWN");
            health.put("initialized", initialized);

            LogMaskConfig config = getConfig();
            health.put("masking_enabled", config.isEnableLogMasking());
            health.put("aop_enabled", config.isEnableAopMasking());

            // マスキング機能をテスト
            String testText = "password=secret123";
            boolean canDetect = testSensitiveDataDetection(testText);
            String masked = maskSensitiveData(testText);
            health.put("detection_working", canDetect);
            health.put("masking_working", !testText.equals(masked));

            health.put("last_check", System.currentTimeMillis());

        } catch (Exception e) {
            health.put("status", "ERROR");
            health.put("error", e.getMessage());
        }

        return health;
    }

    /**
     * 管理器を終了
     */
    public void shutdown() {
        stopStatisticsCollection();
        initialized = false;
        logger.info("ログマスキング管理器が手動で終了されました");
    }
}