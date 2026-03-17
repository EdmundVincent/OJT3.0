package com.collaboportal.common.jwt.utils;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.position.Feature;
import com.collaboportal.common.position.PositionCodeResolver;
import com.collaboportal.common.position.PositionRule;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

public final class JwtClaimUtils {

    private JwtClaimUtils() {
    }

    private static SecretKey secretKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(ConfigManager.getConfig().getSecretKey()));
    }

    private static Claims parseClaimsSafely(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(secretKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException ex) {

            return ex.getClaims();
        }
    }

    public static Optional<String> getSubject(String token) {
        Claims c = parseClaimsSafely(token);
        String sub = c.getSubject();
        if (sub == null)
            sub = c.get("sub", String.class);
        return Optional.ofNullable(sub);
    }

    public static <T> Optional<T> get(String token, String key, Class<T> clazz) {
        Claims c = parseClaimsSafely(token);
        if ("sub".equals(key) && clazz == String.class) {
            return Optional.ofNullable(clazz.cast(
                    c.getSubject() != null ? c.getSubject() : c.get("sub", String.class)));
        }
        Object v = c.get(key);
        if (v == null)
            return Optional.empty();

        if (Number.class.isAssignableFrom(clazz) && v instanceof Number) {
            Number n = (Number) v;
            Object casted;
            if (clazz == Byte.class)
                casted = n.byteValue();
            else if (clazz == Short.class)
                casted = n.shortValue();
            else if (clazz == Integer.class)
                casted = n.intValue();
            else if (clazz == Long.class)
                casted = n.longValue();
            else if (clazz == Float.class)
                casted = n.floatValue();
            else if (clazz == Double.class)
                casted = n.doubleValue();
            else
                casted = null;
            return Optional.ofNullable(clazz.cast(casted));
        }
        if (clazz.isInstance(v)) {
            return Optional.of(clazz.cast(v));
        }

        if (v instanceof String && Number.class.isAssignableFrom(clazz)) {
            try {
                String s = (String) v;
                Object num;
                if (clazz == Integer.class)
                    num = Integer.valueOf(s);
                else if (clazz == Long.class)
                    num = Long.valueOf(s);
                else if (clazz == Double.class)
                    num = Double.valueOf(s);
                else if (clazz == Float.class)
                    num = Float.valueOf(s);
                else if (clazz == Short.class)
                    num = Short.valueOf(s);
                else if (clazz == Byte.class)
                    num = Byte.valueOf(s);
                else
                    num = null;
                return Optional.ofNullable(clazz.cast(num));
            } catch (NumberFormatException ignore) {
            }
        }
        return Optional.empty();
    }

    public static Optional<Integer> getRoleAsInt(String token) {
        Claims c = parseClaimsSafely(token);
        Object v = c.get("role");
        if (v == null)
            return Optional.empty();
        if (v instanceof Number)
            return Optional.of(((Number) v).intValue());
        if (v instanceof String) {
            try {
                return Optional.of(Integer.parseInt((String) v));
            } catch (NumberFormatException ignore) {
            }
        }
        return Optional.empty();
    }

    public static List<String> getProjectIds(String token) {
        Claims c = parseClaimsSafely(token);
        Object v = c.get("projectId");
        if (v == null)
            return Collections.emptyList();
        if (v instanceof Number) {
            return Collections.singletonList(String.valueOf(((Number) v).longValue()));
        }
        if (v instanceof String) {
            String s = ((String) v).trim();
            if (s.isEmpty())
                return Collections.emptyList();
            return Arrays.stream(s.split(","))
                    .map(String::trim)
                    .filter(x -> !x.isEmpty())
                    .collect(Collectors.toList());
        }

        return Collections.singletonList(String.valueOf(v));
    }

    public static Optional<String> getTokenType(String token) {
        return get(token, "token_type", String.class);
    }

    public static boolean isExpired(String token) {
        Claims c = parseClaimsSafely(token);
        Date exp = c.getExpiration();
        return exp != null && exp.before(new Date());
    }

    public static Optional<Date> getIssuedAt(String token) {
        Claims c = parseClaimsSafely(token);
        return Optional.ofNullable(c.getIssuedAt());
    }

    public static Optional<Date> getExpiration(String token) {
        Claims c = parseClaimsSafely(token);
        return Optional.ofNullable(c.getExpiration());
    }

    public static Map<String, Object> getAllClaims(String token) {
        Claims c = parseClaimsSafely(token);
        return Collections.unmodifiableMap(new LinkedHashMap<>(c));
    }

    // public static String getAffiliateCode() {
    // String authToken = CommonHolder.getRequest().getCookieValue("AuthToken");
    // if (authToken == null || authToken.isEmpty()) {
    // return null;
    // }
    // String jobCode = JwtClaimUtils.get(authToken, "jobCode",
    // String.class).orElse(null);
    // String positionRule = PositionCodeResolver.get(jobCode);
    // return positionRule;
    // }

    // public static String getNotChangeableAffiliateCode() {
    // String positionRule = getAffiliateCode();
    // if (positionRule == null || positionRule.isEmpty()) {
    // return "";
    // }
    // return positionRule.split("\\|")[0];
    // }

    public static String getDepartCodes() {
        String authToken = CommonHolder.getRequest().getCookieValue("AuthToken");
        if (authToken == null || authToken.isEmpty()) {
            return null;
        }
        Integer role = getRoleAsInt(authToken).orElse(null);
        String jobCode = JwtClaimUtils.get(authToken, "jobCode", String.class).orElse(null);
        String userId = JwtClaimUtils.get(authToken, "userId", String.class).orElse(null);
        String companyCode = JwtClaimUtils.get(authToken, "companyCode", String.class).orElse(null);
        String departmentCode = JwtClaimUtils.get(authToken, "departmentCode", String.class).orElse(null);
        String branchCode = JwtClaimUtils.get(authToken, "branchCode", String.class).orElse(null);
        String sectionCode = JwtClaimUtils.get(authToken, "sectionCode", String.class).orElse(null);

        if (isHonbu(role)) {
            return ""; 
        }
        PositionRule rule = PositionCodeResolver.get(nz(jobCode));

        String v1 = rule.can(Feature.Company) ? nz(companyCode) : "";
        String v2 = rule.can(Feature.Department) ? nz(departmentCode) : "";
        String v3 = rule.can(Feature.Branch) ? nz(branchCode) : "";
        String v4 = rule.can(Feature.Section) ? nz(sectionCode) : "";
        String v5 = rule.can(Feature.Occupation) ? nz(userId) : "";

        return String.join(",", v1, v2, v3, v4, v5);

    }

    private static String nz(String s) {
        return (s == null) ? "" : s.trim();
    }

    private static Boolean isHonbu(Integer role) {
        return role != null && ( (role & 1) != 0 );

    }
}
