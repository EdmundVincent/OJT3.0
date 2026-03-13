package com.collaboportal.common.oauth2.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * OAuth2クライアント登録情報
 * ビルダーパターンを使用して複雑な設定を構築
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuth2ClientRegistration {
    /** プロバイダーID */
    private String providerId;
    /** 発行者（Issuer） */
    private String issuer;
    /** 認可タイプ */
    private String grantType;
    /** クライアントID */
    private String clientId;
    /** クライアントシークレット */
    private String clientSecret;
    /** オーディエンス */
    private String audience;
    /** リダイレクトURI */
    private String redirectUri;
    /** スコープリスト */
    private List<String> scope;
    /** パスパターンリスト */
    private List<String> pathPatterns;
    /** ユーザー名属性 */
    private String userNameAttribute;
    /** 表示名 */
    private String displayName;

}
