package com.collaboportal.common.oauth2.processor;

import com.collaboportal.common.oauth2.model.OauthTokenResponseBody;

import retrofit2.Call;

import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;

import retrofit2.http.POST;

public interface APIClientProcessor {

        /**
         * アクセストークン情報取得
         * 
         * @param grant_type    種類
         * @param code          コード
         * @param redirect_uri  リダイレクトURL
         * @param audience      オーディエンス
         * @param client_id     クライアントID
         * @param client_secret クライアントシークレット
         * 
         * @return OauthTokenResult アクセストークン情報
         */
        @FormUrlEncoded
        @POST("oauth/token")
        Call<OauthTokenResponseBody> getOauthToken(@Field("grant_type") String grant_type, @Field("code") String code,
                        @Field("redirect_uri") String redirect_uri, @Field("audience") String audience,
                        @Field("client_id") String client_id, @Field("client_secret") String client_secret);

        /**
         * アクセストークン再取得
         * 
         * @param grant_type    種類
         * @param client_id     クライアントID
         * @param client_secret クライアントシークレット
         * @param refresh_token リフレッシュトークン
         * 
         * @return アクセストークン情報
         */
        @FormUrlEncoded
        @POST("oauth/token")
        Call<OauthTokenResponseBody> getOauthTokenByRefreshToken(@Field("grant_type") String grant_type,
                        @Field("client_id") String client_id, @Field("client_secret") String client_secret,
                        @Field("refresh_token") String refresh_token);

}
