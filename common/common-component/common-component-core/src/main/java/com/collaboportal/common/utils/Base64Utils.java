package com.collaboportal.common.utils;

import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class Base64Utils {
    public static String decode(String value) {
        if (value == null) {
            return null;
        }
        try {
            String normalized = normalizePadding(value);
            byte[] decodedBytes = Base64.getUrlDecoder().decode(normalized.getBytes(StandardCharsets.UTF_8));
            String decodedString = new String(decodedBytes, StandardCharsets.UTF_8);
            if (decodedString.contains("\uFFFD")) {
                return value;
            }
            return decodedString;
        } catch (IllegalArgumentException e) {
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
