package com.collaboportal.common.oauth2.processor.impl;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.oauth2.model.OauthTokenResponseBody;
import com.collaboportal.common.oauth2.model.OauthTokenResult;
import com.collaboportal.common.oauth2.processor.APIClientProcessor;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.utils.APIClient;
import com.collaboportal.common.jwt.utils.JwtTokenUtil;
import com.collaboportal.common.utils.Message;

import retrofit2.Response;

@Component
public class AuthProcessorImpl implements AuthProcessor {

        private static final Logger logger = LoggerFactory.getLogger(AuthProcessorImpl.class);

        private final JwtTokenUtil jwtTokenUtil;

        // ベースURL
        private final String baseUrl = ConfigManager.getConfig().getCollaboidBaseurl();
        // WebクライアントID/SECRET（必要なら外から受け取った引数で上書き可）
        private final String defaultClientIdWeb = ConfigManager.getConfig().getCollaboportalClientIdWeb();
        private final String defaultClientSecretWeb = ConfigManager.getConfig().getCollaboportalClientSecretWeb();

        // Cookieの有効期限
        private final int COOKIE_EXPIRATION = ConfigManager.getConfig().getCookieExpiration();

        public AuthProcessorImpl(JwtTokenUtil jwtTokenUtil) {
                this.jwtTokenUtil = jwtTokenUtil;
                logger.debug("AuthProcessorImpl initialized. baseUrl={}", baseUrl);
                logger.debug(defaultClientIdWeb);
                logger.debug(defaultClientSecretWeb);
        }

        @Override
        public OauthTokenResult getOauthTokenFromEndpoint(
                        String grant_type,
                        String code,
                        String redirect_uri,
                        String audience,
                        String client_id,
                        String client_secret,
                        BaseResponse response) {

                APIClientProcessor client = new APIClient(baseUrl).getClient();

                try {
                        logger.debug("コラボトークン取得API呼出開始");
                        logger.info(baseUrl);
                        logger.debug("コラボトークン取得API URL：{}",
                                        client.getOauthToken(grant_type, code, redirect_uri, audience, client_id,
                                                        client_secret)
                                                        .request().url());

                        Response<OauthTokenResponseBody> apiResponse = client
                                        .getOauthToken(grant_type, code, redirect_uri, audience, client_id,
                                                        client_secret)
                                        .execute();

                        int statusCode = apiResponse.code();
                        logger.debug("コラボトークン取得API呼出結果のステータスコード: {} @response {}", statusCode, apiResponse.toString());

                        if (statusCode == HttpStatus.OK.value()) {
                                OauthTokenResponseBody body = apiResponse.body();
                                if (body == null) {
                                        logger.error("コラボトークン取得API: 空ボディ");
                                        return new OauthTokenResult(null, false, null, null, null, null, null);
                                }

                                String accessToken = body.getAccess_token();
                                String idToken = body.getId_token();
                                String refreshToken = body.getRefresh_token();

                                logger.debug("アクセストークン：{}", accessToken);
                                logger.debug("IDトークン：{}", idToken);
                                logger.debug("リフレッシュトークン：{}", refreshToken);

                                response.addHeader(
                                                "Set-Cookie",
                                                Message.Cookie.CLB_REFRESH_TOKEN + "=" + refreshToken
                                                                + ";path=/;MAX-AGE=" + COOKIE_EXPIRATION
                                                                + ";SameSite=strict; ");

                                Map<String, String> items = jwtTokenUtil.getItemsFromIdToken(idToken);
                                return new OauthTokenResult(
                                                accessToken,
                                                true,
                                                items.get("name"),
                                                items.get("sub"),
                                                items.get("email"),
                                                items.get("given_name"),
                                                items.get("family_name"));
                        } else {
                                logger.error(
                                                "コラボトークン取得APIステータスエラー。statusCode：{}、responseBody：{}、grant_type：{}、code：{}、redirect_uri：{}、audience：{}、client_id：{}、client_secret：{}",
                                                statusCode, apiResponse.body(), grant_type, code, redirect_uri,
                                                audience, client_id, client_secret);
                                return new OauthTokenResult(null, false, null, null, null, null, null);
                        }
                } catch (IOException ex) {
                        logger.error(
                                        "コラボAPI呼び出しエラー: grant_type:{}, code:{}, redirect_uri:{}, audience:{}, client_id:{}, client_secret:{}",
                                        grant_type, code, redirect_uri, audience, client_id, client_secret, ex);
                        return new OauthTokenResult(null, false, null, null, null, null, null);
                } catch (Exception ex) {
                        logger.error(
                                        "コラボAPI呼び出しで予期せぬエラー: grant_type:{}, code:{}, redirect_uri:{}, audience:{}, client_id:{}, client_secret:{}",
                                        grant_type, code, redirect_uri, audience, client_id, client_secret, ex);
                        return new OauthTokenResult(null, false, null, null, null, null, null);
                }
        }

        @Override
        public OauthTokenResult getOauthTokenByRefreshToken(
                        String client_id,
                        String client_secret,
                        String refreshToken,
                        BaseResponse response) {

                APIClientProcessor client = new APIClient(baseUrl).getClient();

                try {
                        logger.debug("トークンリフレッシュ開始");
                        logger.debug("トークンリフレッシュAPI URL：{}",
                                        client.getOauthTokenByRefreshToken("refresh_token", client_id, client_secret,
                                                        refreshToken)
                                                        .request().url());

                        Response<OauthTokenResponseBody> apiResponse = client
                                        .getOauthTokenByRefreshToken("refresh_token", client_id, client_secret,
                                                        refreshToken)
                                        .execute();

                        int statusCode = apiResponse.code();
                        logger.debug("トークンリフレッシュAPI呼出結果のステータスコード: {} @response {}", statusCode, apiResponse.toString());

                        if (statusCode == HttpStatus.OK.value()) {
                                OauthTokenResponseBody body = apiResponse.body();
                                if (body == null) {
                                        logger.error("トークンリフレッシュ: 空ボディ");
                                        return new OauthTokenResult(null, false, null, null, null, null, null);
                                }

                                String accessToken = body.getAccess_token();
                                String idToken = body.getId_token();
                                String newRefresh = body.getRefresh_token() != null ? body.getRefresh_token()
                                                : body.getRefresh_token(); // 互換考慮

                                logger.debug("アクセストークン：{}", accessToken);
                                logger.debug("IDトークン：{}", idToken);
                                logger.debug("リフレッシュトークン：{}", newRefresh);

                                response.addHeader(
                                                "Set-Cookie",
                                                Message.Cookie.CLB_REFRESH_TOKEN + "=" + newRefresh
                                                                + ";path=/;MAX-AGE=" + COOKIE_EXPIRATION
                                                                + ";SameSite=strict; ");

                                Map<String, String> items = jwtTokenUtil.getItemsFromIdToken(idToken);
                                return new OauthTokenResult(
                                                accessToken,
                                                true,
                                                items.get("name"),
                                                items.get("sub"),
                                                items.get("email"),
                                                items.get("given_name"),
                                                items.get("family_name"));
                        } else {
                                logger.error(
                                                "トークンリフレッシュAPI呼び出しでステータスエラー。statusCode：{}、responseBody：{}、grant_type：{}、client_id：{}、client_secret：{}、refreshToken：{}",
                                                statusCode, apiResponse.body(), "refresh_token", client_id,
                                                client_secret, refreshToken);
                                return new OauthTokenResult(null, false, null, null, null, null, null);
                        }
                } catch (IOException ex) {
                        logger.error(
                                        "トークンリフレッシュAPI呼び出しエラー: grant_type:{}, client_id:{}, client_secret:{}、refreshToken：{}",
                                        "refresh_token", client_id, client_secret, refreshToken, ex);
                        return new OauthTokenResult(null, false, null, null, null, null, null);
                } catch (Exception ex) {
                        logger.error(
                                        "トークンリフレッシュAPI呼び出しで予期せぬエラー: grant_type:{}, client_id:{}, client_secret:{}、refreshToken：{}",
                                        "refresh_token", client_id, client_secret, refreshToken, ex);
                        return new OauthTokenResult(null, false, null, null, null, null, null);
                }
        }
}
