package com.collaboportal.common.utils;

import java.util.regex.Pattern;
import java.util.regex.Matcher;
import java.util.Map;
import java.util.HashMap;
import java.util.function.Function;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.JsonNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 機密情報マスキングユーティリティクラス
 * ログ出力において機密情報をマスキング処理し、ユーザープライバシーを保護し、コンプライアンス要件を満たす
 *
 * 注意：このクラスは現在、処理機能が一時的に無効化されています
 */
public class SensitiveDataMaskUtil {

    // ▼▼▼ 両ブランチの変更を統合 ▼▼▼
    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataMaskUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();

    // マスキング文字
    private static final String REPLACEMENT_PATTERN = "****";

    // 機能スイッチ - 処理を一時的に無効化
    private static final boolean MASKING_ENABLED = true;

    /**
     * 機密情報パターン列挙型
     */
    public enum SensitiveDataPattern {
        // パスワード関連
        PASSWORD("(?i)(password|pwd|pass|secret|token|key)\\s*[:=]\\s*[\"']?([^\\s\"',}]+)",
                "パスワード情報", SensitiveDataMaskUtil::maskPassword),

        // メールアドレス
        EMAIL("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "メールアドレス", SensitiveDataMaskUtil::maskEmail),

        // JWT Token
        JWT_TOKEN("\\b[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]+\\.[A-Za-z0-9-_]*\\b",
                "JWTトークン", SensitiveDataMaskUtil::maskJwtToken);

        private final Pattern pattern;
        private final String description;
        private final Function<String, String> maskFunction;

        SensitiveDataPattern(String regex, String description, Function<String, String> maskFunction) {
            this.pattern = Pattern.compile(regex);
            this.description = description;
            this.maskFunction = maskFunction;
        }

        public Pattern getPattern() {
            return pattern;
        }

        public String getDescription() {
            return description;
        }

        public Function<String, String> getMaskFunction() {
            return maskFunction;
        }
    }

    /**
     * テキストに対して包括的な機密情報マスキング処理を行う
     *
     * @param text 元のテキスト
     * @return マスキング後のテキスト（機能が無効化されている場合は元のテキストを直接返す）
     */
    public static String maskSensitiveData(String text) {
        // 機能が無効化されているため、元のテキストを直接返す
        if (!MASKING_ENABLED) {
            return text;
        }

        if (text == null || text.trim().isEmpty()) {
            return text;
        }

        String maskedText = text;

        try {
            // 全てのマスキングパターンを順次適用
            for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
                maskedText = applyMaskPattern(maskedText, pattern);
            }

            // JSON形式の特別処理
            if (isJsonFormat(text)) {
                maskedText = maskJsonSensitiveData(maskedText);
            }

        } catch (Exception e) {
            logger.warn("機密情報のマスキング処理に失敗しました: {}", e.getMessage());
            // マスキングが失敗した場合、元のテキストを返す
            return text;
        }

        return maskedText;
    }

    /**
     * 特定のマスキングパターンを適用
     */
    private static String applyMaskPattern(String text, SensitiveDataPattern sensitivePattern) {
        if (!MASKING_ENABLED) {
            return text;
        }

        try {
            Matcher matcher = sensitivePattern.getPattern().matcher(text);
            if (matcher.find()) {
                logger.debug("機密情報のタイプを検知しました: {}", sensitivePattern.getDescription());
            }
            // find()で内部状態が変わるため、再度matcherを生成して置換する
            return sensitivePattern.getPattern().matcher(text)
                    .replaceAll(match -> sensitivePattern.getMaskFunction().apply(match.group()));
        } catch (Exception e) {
            logger.warn("マスキングパターンの適用に失敗しました [{}]: {}", sensitivePattern.getDescription(), e.getMessage());
            return text;
        }
    }

    /**
     * JSON形式かどうかを確認
     */
    private static boolean isJsonFormat(String text) {
        text = text.trim();
        return (text.startsWith("{") && text.endsWith("}")) ||
                (text.startsWith("[") && text.endsWith("]"));
    }

    /**
     * JSON形式の機密データをマスキング
     */
    private static String maskJsonSensitiveData(String jsonText) {
        if (!MASKING_ENABLED) {
            return jsonText;
        }

        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            String maskedJson = jsonNode.toString();

            // JSON内の機密フィールドをマスキング
            Map<String, String> sensitiveFields = new HashMap<>();
            sensitiveFields.put("password", "****");
            sensitiveFields.put("pwd", "****");
            sensitiveFields.put("token", "****");
            sensitiveFields.put("secret", "****");
            sensitiveFields.put("key", "****");

            for (Map.Entry<String, String> entry : sensitiveFields.entrySet()) {
                String fieldPattern = "\"" + entry.getKey() + "\"\\s*:\\s*\"[^\"]*\"";
                maskedJson = maskedJson.replaceAll(fieldPattern,
                        "\"" + entry.getKey() + "\":\"" + entry.getValue() + "\"");
            }

            return maskedJson;
        } catch (Exception e) {
            logger.debug("JSONのマスキング処理に失敗しました。元のテキストを使用します: {}", e.getMessage());
            return jsonText;
        }
    }

    // ==================== 具体的なマスキングメソッド ====================

    /**
     * パスワードマスキング処理
     */
    private static String maskPassword(String match) {
        if (!MASKING_ENABLED) {
            return match;
        }

        // フィールド名を保持し、値を完全に隠す
        if (match.contains(":") || match.contains("=")) {
            int separatorIndex = Math.max(match.indexOf(":"), match.indexOf("="));
            return match.substring(0, separatorIndex + 1) + " \"****\"";
        }
        return REPLACEMENT_PATTERN;
    }

    /**
     * メールマスキング処理 - 先頭2文字とドメイン名を保持
     */
    private static String maskEmail(String email) {
        if (!MASKING_ENABLED) {
            return email;
        }

        int atIndex = email.indexOf("@");
        if (atIndex > 2) {
            String prefix = email.substring(0, 2);
            String suffix = email.substring(atIndex);
            return prefix + "****" + suffix;
        }
        return "****@****.com";
    }

    /**
     * JWT Tokenマスキング処理 - 前後8文字ずつを保持
     */
    private static String maskJwtToken(String token) {
        if (!MASKING_ENABLED) {
            return token;
        }

        if (token.length() > 16) {
            String prefix = token.substring(0, 8);
            String suffix = token.substring(token.length() - 8);
            return prefix + "......" + suffix;
        }
        return "******.******.******";
    }

    /**
     * カスタムマスキング処理
     *
     * @param text        元のテキスト
     * @param regex       正規表現
     * @param replacement 置換文字列
     * @return マスキング後のテキスト（機能が無効化されている場合は元のテキストを直接返す）
     */
    public static String maskCustomPattern(String text, String regex, String replacement) {
        // 機能が無効化されているため、元のテキストを直接返す
        if (!MASKING_ENABLED) {
            return text;
        }

        if (text == null || regex == null || replacement == null) {
            return text;
        }

        try {
            return text.replaceAll(regex, replacement);
        } catch (Exception e) {
            logger.warn("カスタムマスキング処理に失敗しました: {}", e.getMessage());
            return text;
        }
    }

    /**
     * テキストに機密情報が含まれているかを確認
     *
     * @param text 確認対象のテキスト
     * @return 機密情報が含まれているかどうか（機能が無効化されている場合は常にfalseを返す）
     */
    public static boolean containsSensitiveData(String text) {
        // 機能が無効化されているため、常にfalseを返す
        if (!MASKING_ENABLED) {
            return false;
        }

        if (text == null || text.trim().isEmpty()) {
            return false;
        }

        for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
            if (pattern.getPattern().matcher(text).find()) {
                logger.debug("機密情報を検知しました: {}", pattern.getDescription());
                return true;
            }
        }

        return false;
    }

    /**
     * テキストに含まれる機密情報の種類を取得
     *
     * @param text 確認対象のテキスト
     * @return 機密情報の種類リスト（機能が無効化されている場合は空の結果を返す）
     */
    public static Map<String, Boolean> getSensitiveDataTypes(String text) {
        Map<String, Boolean> result = new HashMap<>();

        // 機能が無効化されているため、空の結果を返す
        if (!MASKING_ENABLED) {
            return result;
        }

        if (text == null || text.trim().isEmpty()) {
            return result;
        }

        for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
            boolean found = pattern.getPattern().matcher(text).find();
            result.put(pattern.getDescription(), found);
            if (found) {
                logger.debug("機密情報のタイプを発見しました: {}", pattern.getDescription());
            }
        }

        return result;
    }
}