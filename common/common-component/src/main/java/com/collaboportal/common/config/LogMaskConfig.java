package com.collaboportal.common.config;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

/**
 * ログマスク設定クラス
 * 機密情報マスクの設定とルールを管理
 */
public class LogMaskConfig implements BaseConfig {

    private static final long serialVersionUID = 1L;

    /** ログマスク機能を有効にするかどうか */
    private boolean enableLogMasking = true;

    /** AOPログでマスクを適用するかどうか */
    private boolean enableAopMasking = true;

    /** メソッドパラメータをマスクするかどうか */
    private boolean maskMethodParameters = true;

    /** メソッド戻り値をマスクするかどうか */
    private boolean maskMethodReturnValues = false;

    /** 例外情報をマスクするかどうか */
    private boolean maskExceptionMessages = true;

    /** マスク統計情報を記録するかどうか */
    private boolean enableMaskingStats = false;

    /** マスクをスキップするパッケージ名リスト */
    private Set<String> skipMaskingPackages = new HashSet<>(Arrays.asList(
            "com.collaboportal.common.utils",
            "org.springframework",
            "java.lang"));

    /** マスクをスキップするメソッド名リスト */
    private Set<String> skipMaskingMethods = new HashSet<>(Arrays.asList(
            "toString",
            "hashCode",
            "equals"));

    /** 機密情報パターンの有効状態 */
    private boolean enablePasswordMasking = true;
    private boolean enableEmailMasking = true;
    private boolean enableJwtTokenMasking = true;

    /** ログレベル制限 - 指定されたレベルのみマスクする */
    private Set<String> maskingLogLevels = new HashSet<>(Arrays.asList(
            "TRACE", "DEBUG", "INFO", "WARN", "ERROR"));

    /** 最大処理テキスト長、これを超えるとマスクをスキップ */
    private int maxTextLengthForMasking = 10000;

    /** カスタム機密キーワードリスト */
    private Set<String> customSensitiveKeywords = new HashSet<>();

    @Override
    public String getConfigPrefix() {
        return "log.masking";
    }

    // ================ Getter and Setter ================

    public boolean isEnableLogMasking() {
        return enableLogMasking;
    }

    public void setEnableLogMasking(boolean enableLogMasking) {
        this.enableLogMasking = enableLogMasking;
    }

    public boolean isEnableAopMasking() {
        return enableAopMasking;
    }

    public void setEnableAopMasking(boolean enableAopMasking) {
        this.enableAopMasking = enableAopMasking;
    }

    public boolean isMaskMethodParameters() {
        return maskMethodParameters;
    }

    public void setMaskMethodParameters(boolean maskMethodParameters) {
        this.maskMethodParameters = maskMethodParameters;
    }

    public boolean isMaskMethodReturnValues() {
        return maskMethodReturnValues;
    }

    public void setMaskMethodReturnValues(boolean maskMethodReturnValues) {
        this.maskMethodReturnValues = maskMethodReturnValues;
    }

    public boolean isMaskExceptionMessages() {
        return maskExceptionMessages;
    }

    public void setMaskExceptionMessages(boolean maskExceptionMessages) {
        this.maskExceptionMessages = maskExceptionMessages;
    }

    public boolean isEnableMaskingStats() {
        return enableMaskingStats;
    }

    public void setEnableMaskingStats(boolean enableMaskingStats) {
        this.enableMaskingStats = enableMaskingStats;
    }

    public Set<String> getSkipMaskingPackages() {
        return skipMaskingPackages;
    }

    public void setSkipMaskingPackages(Set<String> skipMaskingPackages) {
        this.skipMaskingPackages = skipMaskingPackages;
    }

    public Set<String> getSkipMaskingMethods() {
        return skipMaskingMethods;
    }

    public void setSkipMaskingMethods(Set<String> skipMaskingMethods) {
        this.skipMaskingMethods = skipMaskingMethods;
    }

    public boolean isEnablePasswordMasking() {
        return enablePasswordMasking;
    }

    public void setEnablePasswordMasking(boolean enablePasswordMasking) {
        this.enablePasswordMasking = enablePasswordMasking;
    }

    public boolean isEnableEmailMasking() {
        return enableEmailMasking;
    }

    public void setEnableEmailMasking(boolean enableEmailMasking) {
        this.enableEmailMasking = enableEmailMasking;
    }

    public boolean isEnableJwtTokenMasking() {
        return enableJwtTokenMasking;
    }

    public void setEnableJwtTokenMasking(boolean enableJwtTokenMasking) {
        this.enableJwtTokenMasking = enableJwtTokenMasking;
    }

    public Set<String> getMaskingLogLevels() {
        return maskingLogLevels;
    }

    public void setMaskingLogLevels(Set<String> maskingLogLevels) {
        this.maskingLogLevels = maskingLogLevels;
    }

    public int getMaxTextLengthForMasking() {
        return maxTextLengthForMasking;
    }

    public void setMaxTextLengthForMasking(int maxTextLengthForMasking) {
        this.maxTextLengthForMasking = maxTextLengthForMasking;
    }

    public Set<String> getCustomSensitiveKeywords() {
        return customSensitiveKeywords;
    }

    public void setCustomSensitiveKeywords(Set<String> customSensitiveKeywords) {
        this.customSensitiveKeywords = customSensitiveKeywords;
    }

    // ================ 便利方法 ================

    /**
     * 檢查是否應該跳過指定包的掩碼
     */
    public boolean shouldSkipPackage(String packageName) {
        if (packageName == null) {
            return false;
        }
        return skipMaskingPackages.stream()
                .anyMatch(skipPackage -> packageName.startsWith(skipPackage));
    }

    /**
     * 檢查是否應該跳過指定方法的掩碼
     */
    public boolean shouldSkipMethod(String methodName) {
        return skipMaskingMethods.contains(methodName);
    }

    /**
     * 檢查指定日誌級別是否需要掩碼
     */
    public boolean shouldMaskLogLevel(String logLevel) {
        return maskingLogLevels.contains(logLevel.toUpperCase());
    }

    /**
     * 檢查文本長度是否超過掩碼限制
     */
    public boolean isTextTooLongForMasking(String text) {
        return text != null && text.length() > maxTextLengthForMasking;
    }

    /**
     * 添加自定義敏感關鍵詞
     */
    public void addCustomSensitiveKeyword(String keyword) {
        if (keyword != null && !keyword.trim().isEmpty()) {
            customSensitiveKeywords.add(keyword.trim());
        }
    }

    /**
     * 移除自定義敏感關鍵詞
     */
    public void removeCustomSensitiveKeyword(String keyword) {
        customSensitiveKeywords.remove(keyword);
    }

    @Override
    public String toString() {
        return "LogMaskConfig{" +
                "enableLogMasking=" + enableLogMasking +
                ", enableAopMasking=" + enableAopMasking +
                ", maskMethodParameters=" + maskMethodParameters +
                ", maskMethodReturnValues=" + maskMethodReturnValues +
                ", maskExceptionMessages=" + maskExceptionMessages +
                ", enableMaskingStats=" + enableMaskingStats +
                ", maxTextLengthForMasking=" + maxTextLengthForMasking +
                ", customSensitiveKeywords=" + customSensitiveKeywords.size() +
                '}';
    }
}