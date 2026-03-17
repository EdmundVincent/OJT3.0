package com.collaboportal.common.config;

import java.util.Set;
import java.util.HashSet;
import java.util.Arrays;

public class LogMaskConfig implements BaseConfig {

    private static final long serialVersionUID = 1L;

    private boolean enableLogMasking = true;
    private boolean enableAopMasking = true;
    private boolean maskMethodParameters = true;
    private boolean maskMethodReturnValues = false;
    private boolean maskExceptionMessages = true;
    private boolean enableMaskingStats = false;

    private Set<String> skipMaskingPackages = new HashSet<>(Arrays.asList(
            "com.collaboportal.common.utils",
            "org.springframework",
            "java.lang"));

    private Set<String> skipMaskingMethods = new HashSet<>(Arrays.asList(
            "toString",
            "hashCode",
            "equals"));

    private boolean enablePasswordMasking = true;
    private boolean enableEmailMasking = true;
    private boolean enableJwtTokenMasking = true;
    private Set<String> maskingLogLevels = new HashSet<>(Arrays.asList("INFO", "WARN", "ERROR"));
    private int maxTextLengthForMasking = 1024;

    @Override
    public String getConfigPrefix() {
        return "log.masking";
    }

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
}
