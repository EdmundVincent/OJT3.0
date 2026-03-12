package com.collaboportal.common.filter;

import ch.qos.logback.classic.spi.ILoggingEvent;
import ch.qos.logback.core.filter.Filter;
import ch.qos.logback.core.spi.FilterReply;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.utils.SensitiveDataMaskUtil;

import java.util.concurrent.atomic.AtomicLong;

/**
 * Logback機密情報マスキングフィルター
 * ログ出力レベルで機密情報のマスキング処理を行う
 */
public class SensitiveDataMaskingFilter extends Filter<ILoggingEvent> {

    // マスキング統計カウンター
    private static final AtomicLong totalLogEvents = new AtomicLong(0);
    private static final AtomicLong maskedLogEvents = new AtomicLong(0);

    // 設定キャッシュ（頻繁な設定取得を避けるため）
    private LogMaskConfig cachedConfig;
    private long lastConfigUpdate = 0;
    private static final long CONFIG_CACHE_DURATION = 60000; // 1分間キャッシュ

    @Override
    public FilterReply decide(ILoggingEvent event) {
        if (event == null) {
            return FilterReply.NEUTRAL;
        }

        totalLogEvents.incrementAndGet();

        try {
            // マスキング設定を取得
            LogMaskConfig maskConfig = getMaskConfig();

            // ログマスキングが有効かどうかを確認
            if (!maskConfig.isEnableLogMasking()) {
                return FilterReply.NEUTRAL;
            }

            // ログレベルにマスキングが必要かどうかを確認
            String logLevel = event.getLevel().toString();
            if (!maskConfig.shouldMaskLogLevel(logLevel)) {
                return FilterReply.NEUTRAL;
            }

            // このパッケージをスキップするかどうかを確認
            String loggerName = event.getLoggerName();
            if (maskConfig.shouldSkipPackage(loggerName)) {
                return FilterReply.NEUTRAL;
            }

            // ログメッセージにマスキング処理を実行
            String originalMessage = event.getFormattedMessage();
            if (originalMessage != null && !originalMessage.trim().isEmpty()) {

                // テキスト長制限を確認
                if (maskConfig.isTextTooLongForMasking(originalMessage)) {
                    return FilterReply.NEUTRAL;
                }

                // 機密情報が含まれているかどうかを確認
                if (SensitiveDataMaskUtil.containsSensitiveData(originalMessage)) {
                    maskedLogEvents.incrementAndGet();

                    // マスキング後のログイベントを作成
                    String maskedMessage = SensitiveDataMaskUtil.maskSensitiveData(originalMessage);

                    // LoggingEventは不変のため、MDCを使用してマスキング後のメッセージを伝達
                    // ここでは一旦NEUTRALを返し、後続のAppenderに処理を委ねる
                    // 実際のマスキング処理はカスタムEncoderで行う
                    event.getMDCPropertyMap().put("masked_message", maskedMessage);
                    event.getMDCPropertyMap().put("original_message", originalMessage);
                    event.getMDCPropertyMap().put("is_masked", "true");
                }
            }

        } catch (Exception e) {
            // マスキングフィルターは通常のログ出力に影響してはならない
            addError("マスキングフィルター処理が失敗しました: " + e.getMessage(), e);
        }

        return FilterReply.NEUTRAL;
    }

    /**
     * マスキング設定を取得（キャッシュ付き）
     */
    private LogMaskConfig getMaskConfig() {
        long currentTime = System.currentTimeMillis();

        // キャッシュが期限切れかどうかを確認
        if (cachedConfig == null || (currentTime - lastConfigUpdate) > CONFIG_CACHE_DURATION) {
            try {
                cachedConfig = ConfigManager.getConfig(LogMaskConfig.class);
                lastConfigUpdate = currentTime;
            } catch (Exception e) {
                addWarn("マスキング設定を取得できません、デフォルト設定を使用します: " + e.getMessage());
                if (cachedConfig == null) {
                    cachedConfig = new LogMaskConfig();
                }
            }
        }

        return cachedConfig;
    }

    @Override
    public void start() {
        super.start();
        addInfo("機密情報マスキングフィルターが開始されました");
    }

    @Override
    public void stop() {
        addInfo("機密情報マスキングフィルターが停止されました");
        super.stop();
    }
}