package com.collaboportal.common.oauth2.context;

import java.io.Serializable;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class OAuth2ProviderContext implements Serializable {

    // リクエストパス
    private String requestPath;

    // ログインメールアドレス
    private String email;
    // 認証コード
    private String code;
    // ステート
    private String state;
    // リクエスト
    BaseRequest request;
    // レスポンス
    BaseResponse response;
    // 選択されたプロバイダーID
    private String selectedProviderId;
    // リダイレクトURI
    private String redirectUri;
    // 認証プロバイダーURL
    private String authProviderUrl;
    // 発行者
    private String issuer;
    // クライアントID
    private String clientId;
    // クライアントシークレット
    private String clientSecret;
    // ホームページ
    private String homePage;
    // オーディエンス
    private String audience;
    // スコープ
    private String scope;
    // 使用するストラテジーのキー
    private String strategyKey;
    // トークン
    private String token;

}