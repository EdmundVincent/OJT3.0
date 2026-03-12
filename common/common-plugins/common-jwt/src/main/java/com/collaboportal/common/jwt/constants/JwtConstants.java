package com.collaboportal.common.jwt.constants;

public class JwtConstants {

    // トークンタイプ
    public static final String ACCESS_TOKEN = "access_token";
    public static final String REFRESH_TOKEN = "refresh_token";

    // 内部用トーケンまたは認証情報保持
    public static final String TOKEN_TYPE_INTERNAL = "internal";
    public static final String TOKEN_TYPE_STATE = "state";
    public static final String GENERATE_OBJECT_TOKEN = "generate-from-object";
    // トークン生成タイプ
    // 認証情報から生成
    public static final String GENERATE_STATE_MAP = "state-map-generator";
    // 内部用JWTトーケン
    public static final String GENERATE_EPL_USER_TOKEN = "epl-user-token-generator";
    public static final String GENERATE_COLLABO_USER_TOKEN = "collabo-user-token-generator";

    public static final String RESOLVER_TYPE_SUB = "sub";
    public static final String RESOLVER_TYPE_UID = "uid";
    public static final String RESOLVER_TYPE_ROLE = "role";
    public static final String TOKEN_TYPE = "token_type";
    // トークン検証タイプ
    public static final String ROLE_VALIDATION = "role-validation";
    public static final String VALIDATE_TYPE_EXPIRED = "exp-only";

    // 自動更新
    public static final String GENERATE_REFRESH_FROM_OLD = "refresh-from-old";

}
