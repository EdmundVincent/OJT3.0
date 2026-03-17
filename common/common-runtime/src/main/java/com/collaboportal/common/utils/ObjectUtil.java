package com.collaboportal.common.utils;

import java.util.Arrays;
import java.util.List;

/**
 * オブジェクト操作ユーティリティクラス
 */
public class ObjectUtil {

    // ログレベルリスト
    public static List<String> logLevelList = Arrays.asList("", "trace", "debug", "info", "warn", "error", "fatal");

    /**
     * オブジェクトを指定された型に変換する
     * 
     * @param <T> 変換先の型
     * @param obj 変換対象のオブジェクト
     * @param cs 変換先のクラス
     * @return 変換されたオブジェクト
     */
    @SuppressWarnings("unchecked")
    public static <T> T getValueByType(Object obj, Class<T> cs) {

        // オブジェクトがnullまたは既に指定された型の場合はそのまま返す
        if (obj == null || obj.getClass().equals(cs)) {
            return (T) obj;
        }

        // オブジェクトを文字列に変換
        String obj2 = String.valueOf(obj);
        Object obj3;
        
        // 指定された型に応じて適切な変換を行う
        if (cs.equals(String.class)) {
            obj3 = obj2;
        } else if (cs.equals(int.class) || cs.equals(Integer.class)) {
            obj3 = Integer.valueOf(obj2);
        } else if (cs.equals(long.class) || cs.equals(Long.class)) {
            obj3 = Long.valueOf(obj2);
        } else if (cs.equals(short.class) || cs.equals(Short.class)) {
            obj3 = Short.valueOf(obj2);
        } else if (cs.equals(byte.class) || cs.equals(Byte.class)) {
            obj3 = Byte.valueOf(obj2);
        } else if (cs.equals(float.class) || cs.equals(Float.class)) {
            obj3 = Float.valueOf(obj2);
        } else if (cs.equals(double.class) || cs.equals(Double.class)) {
            obj3 = Double.valueOf(obj2);
        } else if (cs.equals(boolean.class) || cs.equals(Boolean.class)) {
            obj3 = Boolean.valueOf(obj2);
        } else if (cs.equals(char.class) || cs.equals(Character.class)) {
            obj3 = obj2.charAt(0);
        } else {
            obj3 = obj;
        }
        return (T) obj3;
    }

    /**
     * ログレベル文字列を対応する数値に変換する
     * 
     * @param level ログレベル文字列
     * @return ログレベル数値
     */
    public static int translateLogLevelToInt(String level) {
        int levelInt = logLevelList.indexOf(level);
        // 無効な値の場合はデフォルト値（1）を返す
        if (levelInt <= 0 || levelInt >= logLevelList.size()) {
            levelInt = 1;
        }
        return levelInt;
    }

    /**
     * ログレベル数値を対応する文字列に変換する
     * 
     * @param level ログレベル数値
     * @return ログレベル文字列
     */
    public static String translateLogLevelToString(int level) {
        // 無効な値の場合はデフォルト値（1）を使用
        if (level <= 0 || level >= logLevelList.size()) {
            level = 1;
        }
        return logLevelList.get(level);
    }

}
