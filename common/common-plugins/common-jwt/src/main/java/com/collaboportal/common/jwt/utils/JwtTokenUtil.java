package com.collaboportal.common.jwt.utils;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;
import javax.crypto.spec.SecretKeySpec;
import org.apache.tomcat.util.codec.binary.Base64;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import org.springframework.stereotype.Component;
import com.collaboportal.common.ConfigManager;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;

/**
 * JWTトークン関連のユーティリティクラス
 */
@Component
public class JwtTokenUtil implements Serializable {

    // JWTトークンの有効期間
    private long JWT_TOKEN_VALIDATION = ConfigManager.getConfig().getCookieExpiration() * 1000;

    // JWTトークンのデリミタ
    private final static String delimiter = "\\.";

    // JWTクレームのキー名
    private final String nameKey = "name";
    private final String subKey = "sub";
    private final String emailKey = "email";
    private final String givenNameKey = "given_name";
    private final String familyNameKey = "family_name";

    // 本部フラグ

    // シークレットキー
    private String secretKey = ConfigManager.getConfig().getSecretKey();

    /**
     * シークレットキーを取得
     * 
     * @return シークレットキー
     */
    private Key getKey() {
        byte[] secretKeyBytes = Base64.decodeBase64(secretKey);
        SecretKeySpec SecretKeySpec = new SecretKeySpec(secretKeyBytes, "HmacSHA256");
        return SecretKeySpec;
    }

    // ロガー
    static Logger logger = LoggerFactory.getLogger(JwtTokenUtil.class);

    /**
     * トークンからユーザ名を取得
     * 
     * @param token JWTトークン
     * @return ユーザ名
     */
    public String getUsernameFromToken(String token) {
        return getClaimFromToken(token, Claims::getSubject);
    }

    /**
     * トークンから本部フラグを取得
     * 
     * @param token JWTトークン
     * @return 本部フラグ
     */
    public String getHonbuFlg(String token) {
        return getAllClaimsFromToken(token).get("honbuFlg", String.class);
    }

    /**
     * トークンから有効期限を取得
     * 
     * @param token JWTトークン
     * @return 有効期限
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimFromToken(token, Claims::getExpiration);
    }

    /**
     * トークンからクレームを取得
     * 
     * @param token          JWTトークン
     * @param claimsResolver クレーム解決関数
     * @return クレームの値
     */
    public <T> T getClaimFromToken(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = getAllClaimsFromToken(token);
        return claimsResolver.apply(claims);
    }

    /**
     * トークンから全てのクレームを取得
     * 
     * @param token JWTトークン
     * @return 全てのクレーム
     */
    private Claims getAllClaimsFromToken(String token) {
        return Jwts.parserBuilder().setSigningKey(getKey()).build().parseClaimsJws(token).getBody();
    }

    /**
     * トークンが期限切れかどうかを判定
     * 
     * @param token JWTトークン
     * @return 期限切れならtrue
     * @throws ExpiredJwtException
     */
    public Boolean isTokenExpired(String token) throws ExpiredJwtException {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * IDトークンから項目を取得
     * 
     * @param token IDトークン
     * @return 取得した項目のマップ
     */
    public Map<String, String> getItemsFromIdToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JSONObject playload = new JSONObject(
                    new String(Base64.decodeBase64URLSafe(token.split(delimiter)[1]), "UTF-8"));
            String name = (String) playload.get(nameKey);
            String sub = (String) playload.get(subKey);
            String email = (String) playload.get(emailKey);
            String given_name = (String) playload.get(givenNameKey);
            String family_name = (String) playload.get(familyNameKey);
            logger.debug(
                    "getItemsFromIdTokenで取得項目が取れているか：name:{}、sub:{}、email:{}、given_name:{}、family_name:{}、",
                    name, sub, email, given_name, family_name);
            items.put(nameKey, name);
            items.put(subKey, sub);
            items.put(emailKey, email);
            items.put(givenNameKey, given_name);
            items.put(familyNameKey, family_name);
            logger.debug(
                    "getItemsFromIdTokenで返却値に値が入っているか：name:{}、sub:{}、email:{}、given_name:{}、family_name:{}、",
                    items.get(nameKey), items.get(subKey), items.get(emailKey), items.get(givenNameKey),
                    items.get(familyNameKey));
            return items;
        } catch (JSONException ex) {
            logger.error("JWT変換エラー");
            return items;
        } catch (UnsupportedEncodingException ex) {
            logger.error("JWTエンコードエラー");
            return items;
        }
    }

    /**
     * 認証トークンの有効期限を更新
     * 
     * @param token JWTトークン
     * @return 更新されたトークン
     */
    public String updateExpiresAuthToken(String token) {
        Claims claims = getAllClaimsFromToken(token);
        return Jwts.builder().setClaims(claims).signWith(getKey())
                .setExpiration(new Date(System.currentTimeMillis() + JWT_TOKEN_VALIDATION * 1000)).compact();
    }

    /**
     * セッショントークンから項目を取得
     * 
     * @param token セッショントークン
     * @return 取得した項目のマップ
     */
    public Map<String, String> getItemsFromSessionToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JSONObject playload = new JSONObject(
                    new String(Base64.decodeBase64URLSafe(token.split(delimiter)[1]), "UTF-8"));
            String employeeType = (String) playload.get("employeeType");
            String adminFlg = (String) playload.get("adminFlg");
            items.put("employeeType", employeeType);
            items.put("adminFlg", adminFlg);
            if (playload.has("tokSstCod")) {
                String tokSstCod = (String) playload.get("tokSetCod");
                items.put("tolSstCod", tokSstCod);
            }
            return items;
        } catch (JSONException ex) {
            logger.error("JWT変換エラー", ex);
            return items;
        } catch (UnsupportedEncodingException ex) {
            logger.error("JWTエンコードエラー", ex);
            return items;
        }
    }

    public static Map<String, String> getItemsFromToken(String token) {
        Map<String, String> items = new HashMap<>();
        try {
            JSONObject playload = new JSONObject(
                    new String(Base64.decodeBase64URLSafe(token.split(delimiter)[1]), "UTF-8"));
            String honbuFlg = (String) playload.get("honbuFlg");
            String email = (String) playload.get("sub");
            items.put("sub", email);
            items.put("honbuFlg", honbuFlg);
            return items;

        } catch (JSONException ex) {
            logger.error("JWT変換エラー");
            return items;
        } catch (UnsupportedEncodingException ex) {
            logger.error("JWTエンコードエラー");
            return items;
        }
    }
}
