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

public class SensitiveDataMaskUtil {
    private static final Logger logger = LoggerFactory.getLogger(SensitiveDataMaskUtil.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static final String REPLACEMENT_PATTERN = "****";
    private static final boolean MASKING_ENABLED = true;

    public enum SensitiveDataPattern {
        PASSWORD("(?i)(password|pwd|pass|secret|token|key)\\s*[:=]\\s*[\"']?([^\\s\"',}]+)",
                "パスワード情報", SensitiveDataMaskUtil::maskPassword),
        EMAIL("([a-zA-Z0-9._%+-]+)@([a-zA-Z0-9.-]+\\.[a-zA-Z]{2,})",
                "メールアドレス", SensitiveDataMaskUtil::maskEmail),
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
        public Pattern getPattern() { return pattern; }
        public String getDescription() { return description; }
        public Function<String, String> getMaskFunction() { return maskFunction; }
    }

    public static String maskSensitiveData(String text) {
        if (!MASKING_ENABLED) {
            return text;
        }
        if (text == null || text.trim().isEmpty()) {
            return text;
        }
        String maskedText = text;
        try {
            for (SensitiveDataPattern pattern : SensitiveDataPattern.values()) {
                maskedText = applyMaskPattern(maskedText, pattern);
            }
            if (isJsonFormat(text)) {
                maskedText = maskJsonSensitiveData(maskedText);
            }
        } catch (Exception e) {
            logger.warn("機密情報のマスキング処理に失敗しました: {}", e.getMessage());
            return text;
        }
        return maskedText;
    }

    private static String applyMaskPattern(String text, SensitiveDataPattern sensitivePattern) {
        if (!MASKING_ENABLED) {
            return text;
        }
        try {
            Matcher matcher = sensitivePattern.getPattern().matcher(text);
            if (matcher.find()) {
                logger.debug("機密情報のタイプを検知しました: {}", sensitivePattern.getDescription());
            }
            return sensitivePattern.getPattern().matcher(text)
                    .replaceAll(match -> sensitivePattern.getMaskFunction().apply(match.group()));
        } catch (Exception e) {
            logger.warn("マスキングパターンの適用に失敗しました [{}]: {}", sensitivePattern.getDescription(), e.getMessage());
            return text;
        }
    }

    private static boolean isJsonFormat(String text) {
        text = text.trim();
        return (text.startsWith("{") && text.endsWith("}")) ||
                (text.startsWith("[") && text.endsWith("]"));
    }

    private static String maskJsonSensitiveData(String jsonText) {
        if (!MASKING_ENABLED) {
            return jsonText;
        }
        try {
            JsonNode jsonNode = objectMapper.readTree(jsonText);
            String maskedJson = jsonNode.toString();
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

    private static String maskPassword(String match) {
        if (!MASKING_ENABLED) {
            return match;
        }
        if (match.contains(":") || match.contains("=")) {
            int separatorIndex = Math.max(match.indexOf(":"), match.indexOf("="));
            return match.substring(0, separatorIndex + 1) + " \"****\"";
        }
        return REPLACEMENT_PATTERN;
    }

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
}
