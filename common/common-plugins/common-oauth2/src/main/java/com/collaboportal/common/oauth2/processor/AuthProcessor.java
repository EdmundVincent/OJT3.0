package com.collaboportal.common.oauth2.processor;

import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.oauth2.model.OauthTokenResult;

public interface AuthProcessor {

        /**
         * アクセストークン情報取得
         *
         * @param grant_type    種類
         * @param code          コード
         * @param redirect_uri  リダイレクトURL
         * @param audience      オーディエンス
         * @param client_id     クライアントID
         * @param client_secret クライアントシークレット
         * @param response      HTTPレスポンス
         * 
         * @return OauthTokenResult アクセストークン情報
         */
        OauthTokenResult getOauthTokenFromEndpoint(String grant_type, String code, String redirect_uri,
                        String audience, String client_id, String client_secret, BaseResponse response);

        /**
         * トークンリフレッシュ
         * 
         * @param client_id     クライアントID
         * @param client_secret クライアントシークレット
         * @param refreshToken  リフレッシュトークン
         * @param response      HTTPレスポンス
         * 
         * @return アクセストークン情報
         */
        public OauthTokenResult getOauthTokenByRefreshToken(String client_id, String client_secret, String refreshToken,
                        BaseResponse response);

}
