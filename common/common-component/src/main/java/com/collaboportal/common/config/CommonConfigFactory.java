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

/**
 * 共通設定ファクトリクラス
 * 設定オブジェクトの作成と初期化を行う
 */
public class CommonConfigFactory {

    private static final Logger logger = LoggerFactory.getLogger(CommonConfigFactory.class);

    // インスタンス化防止用プライベートコンストラクタ
    private CommonConfigFactory() {
        // インスタンス化防止
    }

    // デフォルト設定ファイルパス
    private static final String DEFAULT_CONFIG_PATH = "application-common.properties";
    // ${...}プレースホルダーをマッチする正規表現パターン
    private static final Pattern ENV_PATTERN = Pattern.compile("\\$\\{(.*?)\\}");

    /**
     * デフォルト設定ファイルパスを使用して設定を作成
     * 
     * @return 初期化されたCommonConfigオブジェクト
     */
    public static CommonConfig createConfig() {
        logger.debug("デフォルト設定ファイルを使用して設定を作成");
        return createConfig(DEFAULT_CONFIG_PATH);
    }

    /**
     * 指定された設定ファイルパスを使用して設定を作成
     * 
     * @param configPath 設定ファイルのパス
     * @return 初期化されたCommonConfigオブジェクト
     */
    public static CommonConfig createConfig(String configPath) {
        logger.debug("指定された設定ファイルパスを使用して設定を作成: {}", configPath);
        return createConfig(configPath, CommonConfig.class);
    }

    /**
     * デフォルト設定ファイルを使用して指定された型の設定オブジェクトを作成
     * 
     * @param configClass インスタンス化する設定クラス
     * @param <T>         作成する設定の型
     * @return 初期化された設定オブジェクト
     */
    public static <T extends BaseConfig> T createConfig(Class<T> configClass) {
        logger.debug("デフォルト設定ファイルを使用して{}型の設定を作成", configClass.getSimpleName());
        return createConfig(DEFAULT_CONFIG_PATH, configClass);
    }

    /**
     * 指定された設定ファイルを使用して指定された型の設定オブジェクトを作成
     * 
     * @param configPath  設定ファイルのパス
     * @param configClass インスタンス化する設定クラス
     * @param <T>         作成する設定の型
     * @return 初期化された設定オブジェクト
     */
    public static <T extends BaseConfig> T createConfig(String configPath, Class<T> configClass) {
        try {
            logger.debug("{}型の設定オブジェクトを作成開始", configClass.getSimpleName());
            T config = configClass.getDeclaredConstructor().newInstance();
            Map<String, String> configMap = readPropToMap(configPath, config.getConfigPrefix());
            if (configMap == null) {
                logger.error("設定ファイルが見つかりません: {}", configPath);
                throw new RuntimeException("設定ファイルが見つかりません: " + configPath);
            }
            // 環境変数を設定マップにマージ
            mergeEnvironmentVariables(configMap, config.getConfigPrefix());
            return (T) initPropByMap(configMap, config);
        } catch (Exception e) {
            logger.error("設定の作成に失敗しました: {}", configClass.getName(), e);
            throw new RuntimeException("設定の作成に失敗しました: " + configClass.getName(), e);
        }
    }

    /**
     * プロパティファイルから設定を読み込み、Mapに変換
     * 
     * @param configPath 設定ファイルのパス
     * @param prefix     プロパティをフィルタリングするためのプレフィックス
     * @return 設定キーと値のペアを含むMap
     */
    private static Map<String, String> readPropToMap(String configPath, String prefix) {
        Map<String, String> configMap = new HashMap<>();
        try (InputStream is = CommonConfigFactory.class.getClassLoader().getResourceAsStream(configPath)) {
            if (is == null) {
                logger.warn("指定された設定ファイルが見つかりません: {}", configPath);
                return null;
            }
            Properties prop = new Properties();
            prop.load(is);

            // プロパティをMapに変換、プレフィックスでフィルタリング
            String prefixWithDot = prefix + ".";
            for (String key : prop.stringPropertyNames()) {
                if (key.startsWith(prefixWithDot)) {
                    // キーからプレフィックスを削除
                    String newKey = key.substring(prefixWithDot.length());
                    String value = prop.getProperty(key);
                    // 値の中の環境変数を解決
                    value = resolveEnvironmentVariables(value);
                    configMap.put(newKey, value);
                }
            }
        } catch (IOException e) {
            logger.error("プロパティファイルの読み込みに失敗しました: {}", configPath, e);
            throw new RuntimeException("プロパティファイルの読み込みに失敗しました: " + configPath, e);
        }
        return configMap;
    }

    /**
     * 文字列値の中の環境変数を解決
     * 
     * @param value ${...}プレースホルダーを含む文字列値
     * @return プレースホルダーが環境変数の値に置き換えられた文字列
     */
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
                logger.warn("環境変数{}が見つかりません", envKey);
                envValue = "";
            }
            matcher.appendReplacement(result, envValue);
        }
        matcher.appendTail(result);
        return result.toString();
    }

    /**
     * Mapの値を使用してオブジェクトのプロパティを初期化
     * 
     * @param map プロパティのキーと値のペアを含むMap
     * @param obj 初期化するオブジェクト
     * @return 初期化されたオブジェクト
     */
    private static Object initPropByMap(Map<String, String> map, Object obj) {
        if (map == null) {
            map = new HashMap<>(10);
        }
        Class<?> clazz = obj.getClass();

        // オブジェクトの全フィールドを初期化
        for (Field field : clazz.getDeclaredFields()) {

            try {
                field.setAccessible(true);

                if (Map.class.isAssignableFrom(field.getType())) {
                    field.set(obj, new HashMap<>(map)); // shallow copy, 类型 <String,String>
                    continue;
                }

                String value = map.get(field.getName());
                if (value == null) {
                    continue;
                }

                // フィールドがList型かどうかを判断
                if (List.class.isAssignableFrom(field.getType())) {
                    // カンマで文字列を分割し、リストに変換
                    List<String> listValue = Arrays.asList(value.split(","));
                    field.set(obj, listValue);
                } else {
                    // List型でない場合は、元のロジックを使用
                    Object valueConvert = ObjectUtil.getValueByType(value, field.getType());
                    field.set(obj, valueConvert);
                }
            } catch (Exception e) { // Catch Exception to be safer
                logger.error("フィールド値の設定に失敗しました: {}", field.getName(), e);
                throw new RuntimeException("フィールド値の設定に失敗しました: " + field.getName(), e);
            }
        }
        return obj;
    }

    /**
     * 環境変数を設定マップにマージ
     * 
     * @param configMap マージ先の設定マップ
     * @param prefix    環境変数をフィルタリングするためのプレフィックス
     */
    private static void mergeEnvironmentVariables(Map<String, String> configMap, String prefix) {
        String envPrefix = prefix.toUpperCase().replace('.', '_') + "_";
        Map<String, String> envMap = System.getenv();

        for (Map.Entry<String, String> entry : envMap.entrySet()) {
            String key = entry.getKey();
            if (key.startsWith(envPrefix)) {
                // 環境変数キーを設定キー形式に変換
                String configKey = key.substring(envPrefix.length())
                        .toLowerCase()
                        .replace('_', '.');
                // 設定ファイルに既に設定されていない場合のみ上書き
                if (!configMap.containsKey(configKey)) {
                    configMap.put(configKey, entry.getValue());
                }
            }
        }
    }
}
