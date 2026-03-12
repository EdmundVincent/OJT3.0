package com.collaboportal.common.oauth2.model;
import java.io.Serializable;

import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * OAuthトークン応答ボディクラス
 * OAuth認証からのトークン応答を保持する
 */
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class OauthTokenResponseBody implements Serializable {
    
    // アクセストークン
    private String access_token;
    // リフレッシュトークン
    private String refresh_token;
    // IDトークン
    private String id_token;
    // トークンタイプ
    private String token_type;

}
