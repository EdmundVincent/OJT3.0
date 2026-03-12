package com.collaboportal.common.encoder;

import ch.qos.logback.classic.encoder.PatternLayoutEncoder;
import ch.qos.logback.classic.spi.ILoggingEvent;

import com.collaboportal.common.utils.SensitiveDataMaskUtil;

/**
 * 機密情報マスキングエンコーダー
 * PatternLayoutEncoderを継承し、エンコード過程で機密情報をマスキング処理する
 */
public class SensitiveDataMaskingEncoder extends PatternLayoutEncoder {

    private boolean enableMasking = true;
    private boolean preserveOriginalMessage = false;

    @Override
    public byte[] encode(ILoggingEvent event) {
        if (!enableMasking) {
            return super.encode(event);
        }

        try {
            // フィルターで前処理されたマスキングメッセージがあるかチェック
            String maskedMessage = event.getMDCPropertyMap().get("masked_message");
            String isMasked = event.getMDCPropertyMap().get("is_masked");

            if ("true".equals(isMasked) && maskedMessage != null) {
                // フィルターで前処理されたマスキングメッセージを使用
                ILoggingEvent maskedEvent = createMaskedEvent(event, maskedMessage);
                return super.encode(maskedEvent);
            } else {
                // エンコーダーで直接マスキング処理を実行
                String originalMessage = event.getFormattedMessage();
                if (originalMessage != null && SensitiveDataMaskUtil.containsSensitiveData(originalMessage)) {
                    String maskedMessage2 = SensitiveDataMaskUtil.maskSensitiveData(originalMessage);
                    ILoggingEvent maskedEvent = createMaskedEvent(event, maskedMessage2);
                    return super.encode(maskedEvent);
                }
            }
        } catch (Exception e) {
            // マスキング処理が失敗した場合、元のログを出力
            addError("エンコーダーマスキング処理が失敗しました: " + e.getMessage(), e);
        }

        return super.encode(event);
    }

    /**
     * マスキング後のログイベントを作成
     */
    private ILoggingEvent createMaskedEvent(final ILoggingEvent originalEvent, final String maskedMessage) {
        return new ILoggingEventWrapper(originalEvent) {
            @Override
            public String getFormattedMessage() {
                return maskedMessage;
            }

            @Override
            public String getMessage() {
                return maskedMessage;
            }
        };
    }

    // ================ 設定メソッド ================

    /**
     * マスキング機能を有効にするかどうかを設定
     */
    public void setEnableMasking(boolean enableMasking) {
        this.enableMasking = enableMasking;
    }

    public boolean isEnableMasking() {
        return enableMasking;
    }

    /**
     * 元のメッセージを保持するかどうかを設定（デバッグ用）
     */
    public void setPreserveOriginalMessage(boolean preserveOriginalMessage) {
        this.preserveOriginalMessage = preserveOriginalMessage;
    }

    public boolean isPreserveOriginalMessage() {
        return preserveOriginalMessage;
    }

    @Override
    public void start() {
        super.start();
        addInfo("機密情報マスキングエンコーダーが開始されました - enableMasking: " + enableMasking);
    }

    @Override
    public void stop() {
        addInfo("機密情報マスキングエンコーダーが停止されました");
        super.stop();
    }

    /**
     * ILoggingEventラッパー、ログメッセージを変更するために使用
     */
    private static abstract class ILoggingEventWrapper implements ILoggingEvent {
        protected final ILoggingEvent delegate;

        public ILoggingEventWrapper(ILoggingEvent delegate) {
            this.delegate = delegate;
        }

        // オーバーライドが必要な抽象メソッド
        @Override
        public abstract String getFormattedMessage();

        @Override
        public abstract String getMessage();

        // その他のメソッドはすべて元のイベントに委譲
        @Override
        public String getThreadName() {
            return delegate.getThreadName();
        }

        @Override
        public ch.qos.logback.classic.Level getLevel() {
            return delegate.getLevel();
        }

        @Override
        public String getLoggerName() {
            return delegate.getLoggerName();
        }

        @Override
        public long getTimeStamp() {
            return delegate.getTimeStamp();
        }

        @Override
        public Object[] getArgumentArray() {
            return delegate.getArgumentArray();
        }

        @Override
        public ch.qos.logback.classic.spi.IThrowableProxy getThrowableProxy() {
            return delegate.getThrowableProxy();
        }

        @Override
        public StackTraceElement[] getCallerData() {
            return delegate.getCallerData();
        }

        @Override
        public boolean hasCallerData() {
            return delegate.hasCallerData();
        }

        @Override
        public org.slf4j.Marker getMarker() {
            return delegate.getMarker();
        }

        @Override
        public java.util.Map<String, String> getMDCPropertyMap() {
            return delegate.getMDCPropertyMap();
        }

        @Override
        public java.util.Map<String, String> getMdc() {
            return delegate.getMdc();
        }

        @Override
        public void prepareForDeferredProcessing() {
            delegate.prepareForDeferredProcessing();
        }

        @Override
        public ch.qos.logback.classic.spi.LoggerContextVO getLoggerContextVO() {
            return delegate.getLoggerContextVO();
        }

        @Override
        public java.util.List<org.slf4j.event.KeyValuePair> getKeyValuePairs() {
            return delegate.getKeyValuePairs();
        }

        @Override
        public long getSequenceNumber() {
            return delegate.getSequenceNumber();
        }

        @Override
        public int getNanoseconds() {
            return delegate.getNanoseconds();
        }

        @Override
        public java.util.List<org.slf4j.Marker> getMarkerList() {
            return delegate.getMarkerList();
        }
    }
}