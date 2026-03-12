package com.collaboportal.common.helper;

import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Before;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.LogMaskConfig;
import com.collaboportal.common.utils.SensitiveDataMaskUtil;

@Aspect
public class LogAOP {
    // ログ出力のためのクラス
    Logger logger = LoggerFactory.getLogger(LogAOP.class);

    // AOPを行う箇所の設定
    @Pointcut("execution(* com.collaboportal.spnohin.controller.*Controller.*(..))")
    public void controller() {
    }

    @Pointcut("execution(* com.collaboportal.spnohin.repository.*Service.*(..))")
    public void service() {
    }

    @Pointcut("execution(* com.collaboportal.spnohin.repository.*Mapper.*(..))")
    public void repository() {
    }

    /**
     * アラウンド通知 - 完全なメソッド実行ログとマスク処理を提供
     * 
     * @param pjp プログラム結合点
     * @return メソッド実行結果
     * @throws Throwable メソッド実行例外
     */
    @Around("service() || controller() || repository()")
    public Object aroundLog(ProceedingJoinPoint pjp) throws Throwable {
        // マスク設定を取得
        LogMaskConfig maskConfig = getMaskConfig();

        // AOPマスクが有効かどうかをチェック
        if (!maskConfig.isEnableAopMasking()) {
            return executeWithBasicLogging(pjp);
        }

        // このパッケージまたはメソッドをスキップするかどうかをチェック
        String className = pjp.getTarget().getClass().getName();
        String methodName = pjp.getSignature().getName();

        if (maskConfig.shouldSkipPackage(className) || maskConfig.shouldSkipMethod(methodName)) {
            return executeWithBasicLogging(pjp);
        }

        return executeWithMaskedLogging(pjp, maskConfig);
    }

    /**
     * 基本ログ記録を実行（マスクなし）
     */
    private Object executeWithBasicLogging(ProceedingJoinPoint pjp) throws Throwable {
        String signature = pjp.getSignature().toString();

        logger.trace("===== START: {} =====", signature);

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = pjp.proceed();
            long endTime = System.currentTimeMillis();
            logger.trace("===== END SUCCESS: {} [{}ms] =====", signature, endTime - startTime);
            return result;
        } catch (Throwable ex) {
            long endTime = System.currentTimeMillis();
            logger.trace("===== END ERROR: {} [{}ms] - {} =====",
                    signature, endTime - startTime, ex.getMessage());
            throw ex;
        }
    }

    /**
     * マスク付きログ記録を実行
     */
    private Object executeWithMaskedLogging(ProceedingJoinPoint pjp, LogMaskConfig maskConfig) throws Throwable {

        String signature = pjp.getSignature().toString();
        Object[] args = pjp.getArgs();

        // 開始ログを記録
        if (maskConfig.isMaskMethodParameters() && args != null && args.length > 0) {
            String maskedArgs = maskMethodArguments(args, maskConfig);
            logger.trace("===== START: {} with args: {} =====", signature, maskedArgs);
        } else {
            logger.trace("===== START: {} =====", signature);
        }

        long startTime = System.currentTimeMillis();
        Object result = null;

        try {
            result = pjp.proceed();
            long endTime = System.currentTimeMillis();

            // 成功終了ログを記録
            if (maskConfig.isMaskMethodReturnValues() && result != null) {
                String maskedResult = maskReturnValue(result, maskConfig);
                logger.trace("===== END SUCCESS: {} [{}ms] returns: {} =====",
                        signature, endTime - startTime, maskedResult);
            } else {
                logger.trace("===== END SUCCESS: {} [{}ms] =====", signature, endTime - startTime);
            }

            return result;

        } catch (Throwable ex) {
            long endTime = System.currentTimeMillis();

            // 例外終了ログを記録
            String maskedExceptionMessage = maskConfig.isMaskExceptionMessages()
                    ? maskSensitiveData(ex.getMessage(), maskConfig)
                    : ex.getMessage();

            logger.trace("===== END ERROR: {} [{}ms] - {} =====",
                    signature, endTime - startTime, maskedExceptionMessage);
            throw ex;
        }
    }

    /**
     * メソッド引数のマスク処理を行う
     */
    private String maskMethodArguments(Object[] args, LogMaskConfig maskConfig) {
        if (args == null || args.length == 0) {
            return "[]";
        }

        StringBuilder maskedArgs = new StringBuilder("[");
        for (int i = 0; i < args.length; i++) {
            if (i > 0)
                maskedArgs.append(", ");

            if (args[i] == null) {
                maskedArgs.append("null");
            } else {
                String argString = args[i].toString();
                if (maskConfig.isTextTooLongForMasking(argString)) {
                    maskedArgs.append("<too_long_to_mask>");
                } else {
                    String maskedArg = maskSensitiveData(argString, maskConfig);
                    maskedArgs.append(maskedArg);
                }
            }
        }
        maskedArgs.append("]");

        return maskedArgs.toString();
    }

    /**
     * 戻り値のマスク処理を行う
     */
    private String maskReturnValue(Object result, LogMaskConfig maskConfig) {
        if (result == null) {
            return "null";
        }

        String resultString = result.toString();
        if (maskConfig.isTextTooLongForMasking(resultString)) {
            return "<too_long_to_mask>";
        }

        return maskSensitiveData(resultString, maskConfig);
    }

    /**
     * 機密データのマスク処理を実行
     */
    private String maskSensitiveData(String text, LogMaskConfig maskConfig) {
        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        // 機密情報が含まれているかをチェック
        if (SensitiveDataMaskUtil.containsSensitiveData(text)) {
            return SensitiveDataMaskUtil.maskSensitiveData(text);
        }

        return text;
    }

    /**
     * マスク設定を取得
     */
    private LogMaskConfig getMaskConfig() {
        try {
            return ConfigManager.getConfig(LogMaskConfig.class);
        } catch (Exception e) {
            logger.debug("マスク設定の取得に失敗、デフォルト設定を使用: {}", e.getMessage());
            return new LogMaskConfig(); // デフォルト設定を返す
        }
    }

    // ==================== 元の基本ログメソッドを保持 ====================

    /**
     * Beforeアノテーションにより、指定したメソッドの前に処理を追加する
     * これらのメソッドはバックアップとして、アラウンド通知が無効な時に使用される
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @Before("service() || controller() || repository()")
    public void startLog(JoinPoint jp) {
        // アラウンド通知で既に処理されているかをチェック
        LogMaskConfig maskConfig = getMaskConfig();
        if (maskConfig.isEnableAopMasking()) {
            return; // アラウンド通知で処理済み、スキップ
        }

        // 開始ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== START: " + signature + " =====");
    }

    /**
     * Afterアノテーションにより、指定したメソッドの後に処理を追加する
     * Afterアノテーションの引数には、Pointcut式を指定
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @AfterReturning("service() || controller() || repository()")
    public void endSuccesLog(JoinPoint jp) {
        // アラウンド通知で既に処理されているかをチェック
        LogMaskConfig maskConfig = getMaskConfig();
        if (maskConfig.isEnableAopMasking()) {
            return; // アラウンド通知で処理済み、スキップ
        }

        // 終了ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== END SUCCESS : " + signature + " =====");
    }

    /**
     * Afterアノテーションにより、指定したメソッドの後に処理を追加する
     * Afterアノテーションの引数には、Pointcut式を指定
     *
     * @param jp 横断的な処理を挿入する場所
     */
    @AfterThrowing("service() || controller() || repository()")
    public void endErrorLog(JoinPoint jp) {
        // アラウンド通知で既に処理されているかをチェック
        LogMaskConfig maskConfig = getMaskConfig();
        if (maskConfig.isEnableAopMasking()) {
            return; // アラウンド通知で処理済み、スキップ
        }

        // 終了ログを出力
        String signature = jp.getSignature().toString();
        logger.trace("===== END ERROR : " + signature + " =====");
    }

}
