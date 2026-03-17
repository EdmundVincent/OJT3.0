package com.collaboportal.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

/**
 * Base64エンコード/デコード関連のユーティリティクラス。
 */
public class Base64Utils {

    /**
     * Base64URLエンコードされた文字列をデコードします。
     * デコードに失敗した場合や、デコード結果が不正な文字を含む場合は、元の文字列を返します。
     *
     * @param value デコードする文字列
     * @return デコードされた文字列、またはデコードできなかった場合は元の文字列
     */
    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            String normalized = normalizePadding(value);
            // Base64URLセーフなデコーダを使用
            byte[] decodedBytes = Base64.getUrlDecoder().decode(normalized.getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);

            // ヒューリスティック：デコード結果に置換文字(U+FFFD)が含まれる場合、
            // 元の文字列はBase64ではなく、たまたまBase64互換文字を含む平文であった可能性が高いと判断し、
            // 元の文字列を返す。
            if (decodedString.contains("\uFFFD")) {
                return value;
            }
            return decodedString;
        } catch (IllegalArgumentException e) {
            // パラメータがBase64エンコードされていない場合は元の値を返す
            return value;
        }
    }

    private static String normalizePadding(String value) {
        String trimmed = value.trim();
        int remainder = trimmed.length() % 4;
        if (remainder == 0) {
            return trimmed;
        }
        int paddingNeeded = 4 - remainder;
        StringBuilder builder = new StringBuilder(trimmed);
        for (int i = 0; i < paddingNeeded; i++) {
            builder.append('=');
        }
        return builder.toString();
    }
}
