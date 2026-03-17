package com.collaboportal.common.utils;

import java.util.Arrays;
import java.util.List;

public class ObjectUtil {

    public static List<String> logLevelList = Arrays.asList("", "trace", "debug", "info", "warn", "error", "fatal");

    @SuppressWarnings("unchecked")
    public static <T> T getValueByType(Object obj, Class<T> cs) {
        if (obj == null || obj.getClass().equals(cs)) {
            return (T) obj;
        }
        String obj2 = String.valueOf(obj);
        Object obj3;
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

    public static int translateLogLevelToInt(String level) {
        int levelInt = logLevelList.indexOf(level);
        if (levelInt <= 0 || levelInt >= logLevelList.size()) {
            levelInt = 1;
        }
        return levelInt;
    }

    public static String translateLogLevelToString(int level) {
        if (level <= 0 || level >= logLevelList.size()) {
            level = 1;
        }
        return logLevelList.get(level);
    }
}
