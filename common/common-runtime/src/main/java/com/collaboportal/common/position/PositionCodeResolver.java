package com.collaboportal.common.position;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.config.PositionCodeConfig;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public final class PositionCodeResolver {
    private PositionCodeResolver() {
    }

    private static final Map<String, String> RAW_RULES;

    private static final ConcurrentHashMap<String, PositionRule> CACHE = new ConcurrentHashMap<>();

    static {
        PositionCodeConfig cfg = ConfigManager.getPositionCodeConfig();
        Map<String, String> m = cfg.getRules();
        if (m == null || m.isEmpty()) {
            throw new IllegalStateException(
                    "役職ルールが見つかりません");
        }
        RAW_RULES = Collections.unmodifiableMap(new HashMap<>(m));
    }

    public static PositionRule get(String jobCode) {
        return get(jobCode, null);
    }

    public static PositionRule get(String jobCode, String context) {
        String probe = (context == null || context.isEmpty()) ? jobCode : jobCode + "_" + context;

        String rawCandidate = RAW_RULES.get(probe);
        if (rawCandidate == null)
            rawCandidate = RAW_RULES.get(jobCode);
        if (rawCandidate == null)
            rawCandidate = RAW_RULES.get("other");
        if (rawCandidate == null)
            throw new IllegalStateException(jobCode + "のルール存在しません。");

        final String raw = rawCandidate;
        return CACHE.computeIfAbsent(raw, PositionCodeResolver::parse);
    }

    // ----------------- helpers -----------------
    private static PositionRule parse(String raw) {
        String[] lr = raw.split("\\|");
        if (lr.length != 2)
            throw new IllegalArgumentException("Bad rule: " + raw);
        int perm = bitsToMask(lr[0]);
        int def = bitsToMask(lr[1]);
        return new PositionRule(perm, def);
    }

    private static int bitsToMask(String csv) {
        String[] arr = csv.split(",");
        if (arr.length != 5)
            throw new IllegalArgumentException("ルール不具合" + csv);
        int mask = 0;
        for (int i = 0; i < 5; i++) {
            if ("1".equals(arr[i].trim()))
                mask |= (1 << i);
        }
        return mask;
    }

    public static Map<String, String> snapshot() {
        return RAW_RULES;
    }
}
