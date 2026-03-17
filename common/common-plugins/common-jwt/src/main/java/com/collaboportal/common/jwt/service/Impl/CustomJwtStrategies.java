package com.collaboportal.common.jwt.service.Impl;

import com.collaboportal.common.jwt.strategy.JwtTokenGenerator;
import com.collaboportal.common.jwt.strategy.JwtTokenValidator;
import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.entity.UserMasterCollabo;
import com.collaboportal.common.jwt.entity.UserMasterEPL;
import com.collaboportal.common.jwt.registry.JwtStrategyRegistry;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

import org.apache.tomcat.util.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.util.Date;

import javax.crypto.spec.SecretKeySpec;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.security.Key;
import java.util.LinkedHashMap;
import java.util.Map;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.jwt.config.JwtConfig;

/**
 * カスタムJWTストラテジー実装クラス
 * JWT関連の各種ストラテジー（Resolver、Generator、Validator）を定義し、
 * JwtStrategyRegistryに登録する
 */
@Component
public class CustomJwtStrategies implements CommandLineRunner {

        private static final Logger logger = LoggerFactory.getLogger(CustomJwtStrategies.class);
        private String secretKey = ConfigManager.getConfig().getSecretKey();
        private final JwtStrategyRegistry registry;
        private final Map<String, Integer> endpointPermissionMap;

        /**
         * コンストラクタ
         * 
         * @param registry JWTストラテジーレジストリ
         */
        public CustomJwtStrategies(JwtStrategyRegistry registry, JwtConfig jwtConfig) {
                this.registry = registry;
                this.endpointPermissionMap = jwtConfig.getEndpointPermissions();
        }

        /**
         * 統一された署名キーの取得
         * 
         * @return HMAC署名用の秘密鍵
         */
        private Key secretKey() {
                byte[] secretKeyBytes = Base64.decodeBase64(secretKey);
                SecretKeySpec SecretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
                return SecretKeySpec;
        }

        @Override
        public void run(String... args) {

                /* --------------- ① Claimリゾルバー（Resolver） ---------------- */
                // ユーザーIDの抽出
                registry.registerClaimResolver("sub", claims -> claims.get("sub", String.class));

                registry.registerClaimResolver("uid", claims -> claims.get("uid", Long.class));
                // メールアドレスの抽出
                registry.registerClaimResolver("email", Claims::getSubject);
                // ロールの抽出
                registry.registerClaimResolver("role", claims -> claims.get("role", Byte.class));

                registry.registerClaimResolver("all", claims -> new LinkedHashMap<>(claims));

                /* --------------- ② トークンジェネレーター（Generator） --------------- */

                // 2-2 Map → StateToken（コールバック後の状態用）
                JwtTokenGenerator<Map<String, Object>> mapGenerator = map -> Jwts.builder()
                                .addClaims(map)
                                .setIssuedAt(new Date())
                                .claim("token_type", JwtConstants.TOKEN_TYPE_STATE)
                                .setExpiration(new Date(System.currentTimeMillis()
                                                + ConfigManager.getConfig().getCookieExpiration() * 1000L))
                                .signWith(secretKey())
                                .compact();
                registry.registerTokenGenerator(JwtConstants.GENERATE_STATE_MAP, mapGenerator);

                // 2-3 User → RefreshToken（長期有効）AccessTokenの更新用、内部使用
                JwtTokenGenerator<UserMasterCollabo> oauth2Generator = user -> {
                        Date now = new Date();
                        Date exp = new Date(now.getTime() + ConfigManager.getConfig().getCookieExpiration() * 1000L);

                        Claims claims = Jwts.claims();
                        claims.setSubject(user.getUserMail());
                        claims.put("userName", user.getUserName());
                        claims.put("role", user.getRole());
                        claims.put("projectId", user.getProjectId());
                        claims.put("jobCode", user.getJobCode());
                        claims.put("userId", user.getUserId());
                        claims.put("companyCode", user.getCompanyCode());
                        claims.put("departmentCode", user.getDepartmentCode());
                        claims.put("branchCode", user.getBranchCode());
                        claims.put("sectionCode", user.getSectionCode());
                        claims.setIssuedAt(now);
                        claims.setExpiration(exp);
                        claims.put("token_type", JwtConstants.GENERATE_COLLABO_USER_TOKEN);
                        return Jwts.builder()
                                        .setClaims(claims)
                                        .signWith(secretKey())
                                        .compact();
                };

                registry.registerTokenGenerator(JwtConstants.GENERATE_COLLABO_USER_TOKEN, oauth2Generator);
                JwtTokenGenerator<UserMasterEPL> eplGenerator = user -> {
                        Date now = new Date();
                        Date exp = new Date(now.getTime() + ConfigManager.getConfig().getCookieExpiration() * 1000L);

                        Claims claims = Jwts.claims();
                        claims.setSubject(user.getUserId());
                        claims.put("userName", user.getUserName());
                        claims.put("userId", user.getUserId());
                        claims.put("role", user.getRole());
                        claims.put("projectId", user.getProjectId());
                        claims.setIssuedAt(now);
                        claims.setExpiration(exp);
                        claims.put(JwtConstants.TOKEN_TYPE, JwtConstants.GENERATE_EPL_USER_TOKEN);

                        return Jwts.builder()
                                        .setClaims(claims)
                                        .signWith(secretKey())
                                        .compact();
                };
                registry.registerTokenGenerator(JwtConstants.GENERATE_EPL_USER_TOKEN, eplGenerator);

                JwtTokenGenerator<Object> objectGenerator = obj -> {

                        Map<String, Object> claimMap = new LinkedHashMap<>();

                        Class<?> clazz = obj.getClass();
                        while (clazz != null && clazz != Object.class) {
                                for (Field f : clazz.getDeclaredFields()) {

                                        int mod = f.getModifiers();
                                        if (Modifier.isStatic(mod) || Modifier.isTransient(mod)) {
                                                continue;
                                        }

                                        try {
                                                f.setAccessible(true);
                                                Object v = f.get(obj);
                                                if (v != null) {
                                                        claimMap.put(f.getName(), v);
                                                }
                                        } catch (IllegalAccessException e) {

                                                throw new RuntimeException(
                                                                "Cannot access field: " + f.getName(), e);
                                        }
                                }
                                clazz = clazz.getSuperclass();
                        }

                        Date now = new Date();
                        Date exp = new Date(now.getTime()
                                        + ConfigManager.getConfig().getCookieExpiration() * 1000L);

                        return Jwts.builder()
                                        .addClaims(claimMap)
                                        .setIssuedAt(now)
                                        .setExpiration(exp)
                                        .claim(JwtConstants.TOKEN_TYPE, JwtConstants.TOKEN_TYPE_INTERNAL)
                                        .signWith(secretKey())
                                        .compact();
                };

                // レジストリ
                registry.registerTokenGenerator(
                                JwtConstants.GENERATE_OBJECT_TOKEN, objectGenerator);

                /* --------------- トークンバリデーター（Validator） ---------------- */

                // 3-1 有効期限のみ検証
                registry.registerTokenValidator(JwtConstants.VALIDATE_TYPE_EXPIRED,
                                (token, claims) -> claims.getExpiration().after(new Date()));

                // 3-2 ロール検証
                JwtTokenValidator roleValidator = (token, claims) -> {
                        if (!claims.getExpiration().after(new Date())) {
                                return false;
                        }

                        // BaseRequestを取得
                        BaseRequest request = CommonHolder.getRequest();
                        if (request == null) {
                                return true;
                        }
                        String requestUri = request.getRequestPath();
                        if (requestUri == null) {
                                return true;
                        }

                        // URLの最後のセグメントを取得
                        String endpoint = requestUri.substring(requestUri.lastIndexOf('/') + 1);

                        // マップにエンドポイントが存在するかチェック
                        if (endpointPermissionMap.containsKey(endpoint)) {
                                Integer requiredPermission = endpointPermissionMap.get(endpoint);
                                Object roleObj = claims.get("role");
                                if (roleObj instanceof Number) {
                                        int userRole = ((Number) roleObj).intValue();
                                        // ビット演算で権限をチェック
                                        return (userRole & requiredPermission) != 0;
                                }
                                return false; // ロールがない場合は検証失敗
                        }

                        return true; // マップにキーがなければチェックをスキップ（成功扱い）
                };

                registry.registerTokenValidator(JwtConstants.ROLE_VALIDATION, roleValidator);

                /* --------------- ② トークンジェネレーター（update） --------------- */
                // トークンの更新処理
                /**
                 * 既存トークンを受け取り、期限だけ延長して返す。
                 * - exp, iat を更新
                 * - その他の Claim はそのままコピー
                 * 失効／署名エラー時は RuntimeException を投げる想定。
                 */
                JwtTokenGenerator<String> refreshGenerator = oldToken -> {

                        // 1) 既存トークンをパース
                        Claims claims = Jwts.parserBuilder()
                                        .setSigningKey(secretKey())
                                        .build()
                                        .parseClaimsJws(oldToken)
                                        .getBody();

                        // 2) 新しい時間をセット
                        Date now = new Date();
                        Date exp = new Date(now.getTime()
                                        + ConfigManager.getConfig().getCookieExpiration() * 1000L);

                        // 3) exp / iat を上書きし再署名
                        return Jwts.builder()
                                        .setClaims(new LinkedHashMap<>(claims))
                                        .setIssuedAt(now)
                                        .setExpiration(exp)
                                        .signWith(secretKey())
                                        .compact();
                };

                // レジストリに登録（定数名は好きに決めてください）
                registry.registerTokenGenerator(
                                JwtConstants.GENERATE_REFRESH_FROM_OLD, refreshGenerator);
        }
}
