package com.collaboportal.common.config;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.collaboportal.common.utils.ObjectUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class CommonConfigFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommonConfigFactory.class);
    private CommonConfigFactory() {
    }

    private static final String DEFAULT_CONFIG_PATH = "application-common.properties";
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    public static CommonConfig createConfig() {
        return createConfig(DEFAULT_CONFIG_PATH);
    }

    public static CommonConfig createConfig(String configPath) {
        return createConfig(configPath, CommonConfig.class);
    }

    public static <T extends BaseConfig> T createConfig(Class<T> configClass) {
        return createConfig(DEFAULT_CONFIG_PATH, configClass);
    }

    public static <T extends BaseConfig> T createConfig(String configPath, Class<T> configClass) {
        try {
            T config = configClass.getDeclaredConstructor().newInstance();
            Map<String, String> configMap = readPropToMap(configPath, config.getConfigPrefix());
            if (configMap == null) {
                throw new RuntimeException("設定ファイルが見つかりません: " + configPath);
            }
            mergeEnvironmentVariables(configMap, config.getConfigPrefix());
            return (T) initPropByMap(configMap, config);
        } catch (Exception e) {
            throw new RuntimeException("設定の作成に失敗しました: " + configClass.getName(), e);
        }
    }

    private static Map<String, String> readPropToMap(String configPath, String prefix) {
        Map<String, String> configMap = new HashMap<>();
        try (InputStream is = CommonConfigFactory.class.getClassLoader().getResourceAsStream(configPath)) {
            if (is == null) {
                return null;
            }
            Properties prop = new Properties();
            prop.load(is);
            String prefixWithDot = prefix + ".";
            for (String key : prop.stringPropertyNames()) {
                if (key.startsWith(prefixWithDot)) {
                    String newKey = key.substring(prefixWithDot.length());
                    String value = prop.getProperty(key);
                    value = resolveEnvironmentVariables(value);
                    configMap.put(newKey, value);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException("プロパティファイルの読み込みに失敗しました: " + configPath, e);
        }
        return configMap;
    }

    private static String resolveEnvironmentVariables(String value) {
        if (value == null) {
            return null;
        }
        Matcher matcher = ENV_PATTERN.matcher(value);
        StringBuffer result = new StringBuffer();
        while (matcher.find()) {
            String envKey = matcher.group(1);
            String envValue = System.getenv(envKey);
            if (envValue == null) {
                envValue = "";
            }
            matcher.appendReplacement(result, envValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    private static Object initPropByMap(Map<String, String> map, Object obj) {
        if (map == null) {
            map = new HashMap<>(10);
        }
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getDeclaredFields()) {
            try {
                field.setAccessible(true);
                if (Map.class.isAssignableFrom(field.getType())) {
                    field.set(obj, new HashMap<>(map));
                    continue;
                }
                String value = map.get(field.getName());
                if (value == null) {
                    continue;
                }
                if (List.class.isAssignableFrom(field.getType())) {
                    List<String> listValue = Arrays.asList(value.split(","));
                    field.set(obj, listValue);
                } else {
                    Object valueConvert = ObjectUtil.getValueByType(value, field.getType());
                    field.set(obj, valueConvert);
                }
            } catch (Exception e) {
                throw new RuntimeException("フィールド値の設定に失敗しました: " + field.getName(), e);
            }
        }
        return obj;
    }

    private static void mergeEnvironmentVariables(Map<String, String> configMap, String prefix) {
        String envPrefix = prefix.toUpperCase().replace('.', '_') + "_";
        Map<String, String> envMap = System.getenv();
        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(envPrefix)) {
                String configKey = key.substring(envPrefix.length())
                        .toLowerCase()
                        .replace('_', '.');
                if (!configMap.containsKey(configKey)) {
                    configMap.put(configKey, entry.getValue());
                }
            }
        }
    }
}
