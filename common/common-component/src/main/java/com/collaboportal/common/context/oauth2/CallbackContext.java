package com.collaboportal.common.context.oauth2;

import java.io.Serializable;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

/**
 * OAuth2 コールバック処理コンテキストクラス
 * OAuth2 認証コールバック処理中のデータ受け渡しに使用
 */
public class CallbackContext implements Serializable {

    // ユーザーメールアドレス（フォームパラメータから取得）
    private String emailFromForm;

    // OAuth2 認証コード
    private String code;

    // OAuth2 ステートパラメータ
    private String state;

    // 認証ステートトークン（Cookieから取得）
    private String authStateToken;

    // 移動URL（Cookieから取得）
    private String moveUrl;

    // HTTPリクエストオブジェクト
    private BaseRequest request;

    // HTTPレスポンスオブジェクト
    private BaseResponse response;

    // クライアントID（認証ステートトークンから解析）
    private String clientId;

    // クライアントシークレット（認証ステートトークンから解析）
    private String clientSecret;

    // オーディエンス（認証ステートトークンから解析）
    private String audience;

    // スコープ（認証ステートトークンから解析）
    private String scope;

    // トークン（認証ステートトークンから解析）
    private String token;

    // ストラテジーキー（test または prod）
    private String strategyKey;

    // リダイレクトURI
    private String redirectUri;

    // ホームページURL
    private String homePage;

    // 選択されたプロバイダーID
    private String selectedProviderId;

    // 認証プロバイダーURL
    private String authProviderUrl;

    // 発行者
    private String issuer;

    public CallbackContext() {
    }

    public CallbackContext(String emailFromForm, String code, String state, String authStateToken,
            String moveUrl, BaseRequest request, BaseResponse response, String clientId,
            String clientSecret, String audience, String scope, String token,
            String strategyKey, String redirectUri, String homePage,
            String selectedProviderId, String authProviderUrl, String issuer) {
        this.emailFromForm = emailFromForm;
        this.code = code;
        this.state = state;
        this.authStateToken = authStateToken;
        this.moveUrl = moveUrl;
        this.request = request;
        this.response = response;
        this.clientId = clientId;
        this.clientSecret = clientSecret;
        this.audience = audience;
        this.scope = scope;
        this.token = token;
        this.strategyKey = strategyKey;
        this.redirectUri = redirectUri;
        this.homePage = homePage;
        this.selectedProviderId = selectedProviderId;
        this.authProviderUrl = authProviderUrl;
        this.issuer = issuer;
    }

    public String getEmailFromForm() {
        return emailFromForm;
    }

    public String getCode() {
        return code;
    }

    public String getState() {
        return state;
    }

    public String getAuthStateToken() {
        return authStateToken;
    }

    public String getMoveUrl() {
        return moveUrl;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public BaseResponse getResponse() {
        return response;
    }

    public String getClientId() {
        return clientId;
    }

    public String getClientSecret() {
        return clientSecret;
    }

    public String getAudience() {
        return audience;
    }

    public String getScope() {
        return scope;
    }

    public String getToken() {
        return token;
    }

    public String getStrategyKey() {
        return strategyKey;
    }

    public String getRedirectUri() {
        return redirectUri;
    }

    public String getHomePage() {
        return homePage;
    }

    public String getSelectedProviderId() {
        return selectedProviderId;
    }

    public String getAuthProviderUrl() {
        return authProviderUrl;
    }

    public String getIssuer() {
        return issuer;
    }

    public CallbackContext setEmailFromForm(String emailFromForm) {
        this.emailFromForm = emailFromForm;
        return this;
    }

    public CallbackContext setCode(String code) {
        this.code = code;
        return this;
    }

    public CallbackContext setState(String state) {
        this.state = state;
        return this;
    }

    public CallbackContext setAuthStateToken(String authStateToken) {
        this.authStateToken = authStateToken;
        return this;
    }

    public CallbackContext setMoveUrl(String moveUrl) {
        this.moveUrl = moveUrl;
        return this;
    }

    public CallbackContext setRequest(BaseRequest request) {
        this.request = request;
        return this;
    }

    public CallbackContext setResponse(BaseResponse response) {
        this.response = response;
        return this;
    }

    public CallbackContext setClientId(String clientId) {
        this.clientId = clientId;
        return this;
    }

    public CallbackContext setClientSecret(String clientSecret) {
        this.clientSecret = clientSecret;
        return this;
    }

    public CallbackContext setAudience(String audience) {
        this.audience = audience;
        return this;
    }

    public CallbackContext setScope(String scope) {
        this.scope = scope;
        return this;
    }

    public CallbackContext setToken(String token) {
        this.token = token;
        return this;
    }

    public CallbackContext setStrategyKey(String strategyKey) {
        this.strategyKey = strategyKey;
        return this;
    }

    public CallbackContext setRedirectUri(String redirectUri) {
        this.redirectUri = redirectUri;
        return this;
    }

    public CallbackContext setHomePage(String homePage) {
        this.homePage = homePage;
        return this;
    }

    public CallbackContext setSelectedProviderId(String selectedProviderId) {
        this.selectedProviderId = selectedProviderId;
        return this;
    }

    public CallbackContext setAuthProviderUrl(String authProviderUrl) {
        this.authProviderUrl = authProviderUrl;
        return this;
    }

    public CallbackContext setIssuer(String issuer) {
        this.issuer = issuer;
        return this;
    }

    public static CallbackContextBuilder builder() {
        return new CallbackContextBuilder();
    }

    public static class CallbackContextBuilder {
        private String emailFromForm;
        private String code;
        private String state;
        private String authStateToken;
        private String moveUrl;
        private BaseRequest request;
        private BaseResponse response;
        private String clientId;
        private String clientSecret;
        private String audience;
        private String scope;
        private String token;
        private String strategyKey;
        private String redirectUri;
        private String homePage;
        private String selectedProviderId;
        private String authProviderUrl;
        private String issuer;

        public CallbackContextBuilder emailFromForm(String emailFromForm) {
            this.emailFromForm = emailFromForm;
            return this;
        }

        public CallbackContextBuilder code(String code) {
            this.code = code;
            return this;
        }

        public CallbackContextBuilder state(String state) {
            this.state = state;
            return this;
        }

        public CallbackContextBuilder authStateToken(String authStateToken) {
            this.authStateToken = authStateToken;
            return this;
        }

        public CallbackContextBuilder moveUrl(String moveUrl) {
            this.moveUrl = moveUrl;
            return this;
        }

        public CallbackContextBuilder request(BaseRequest request) {
            this.request = request;
            return this;
        }

        public CallbackContextBuilder response(BaseResponse response) {
            this.response = response;
            return this;
        }

        public CallbackContextBuilder clientId(String clientId) {
            this.clientId = clientId;
            return this;
        }

        public CallbackContextBuilder clientSecret(String clientSecret) {
            this.clientSecret = clientSecret;
            return this;
        }

        public CallbackContextBuilder audience(String audience) {
            this.audience = audience;
            return this;
        }

        public CallbackContextBuilder scope(String scope) {
            this.scope = scope;
            return this;
        }

        public CallbackContextBuilder token(String token) {
            this.token = token;
            return this;
        }

        public CallbackContextBuilder strategyKey(String strategyKey) {
            this.strategyKey = strategyKey;
            return this;
        }

        public CallbackContextBuilder redirectUri(String redirectUri) {
            this.redirectUri = redirectUri;
            return this;
        }

        public CallbackContextBuilder homePage(String homePage) {
            this.homePage = homePage;
            return this;
        }

        public CallbackContextBuilder selectedProviderId(String selectedProviderId) {
            this.selectedProviderId = selectedProviderId;
            return this;
        }

        public CallbackContextBuilder authProviderUrl(String authProviderUrl) {
            this.authProviderUrl = authProviderUrl;
            return this;
        }

        public CallbackContextBuilder issuer(String issuer) {
            this.issuer = issuer;
            return this;
        }

        public CallbackContext build() {
            return new CallbackContext(emailFromForm, code, state, authStateToken, moveUrl,
                    request, response, clientId, clientSecret, audience,
                    scope, token, strategyKey, redirectUri, homePage,
                    selectedProviderId, authProviderUrl, issuer);
        }
    }

    @Override
    public String toString() {
        return "CallbackContext{" +
                "emailFromForm='" + emailFromForm + '\'' +
                ", code='" + code + '\'' +
                ", state='" + state + '\'' +
                ", authStateToken='" + authStateToken + '\'' +
                ", moveUrl='" + moveUrl + '\'' +
                ", request=" + request +
                ", response=" + response +
                ", clientId='" + clientId + '\'' +
                ", clientSecret='" + clientSecret + '\'' +
                ", audience='" + audience + '\'' +
                ", scope='" + scope + '\'' +
                ", token='" + token + '\'' +
                ", strategyKey='" + strategyKey + '\'' +
                ", redirectUri='" + redirectUri + '\'' +
                ", homePage='" + homePage + '\'' +
                ", selectedProviderId='" + selectedProviderId + '\'' +
                ", authProviderUrl='" + authProviderUrl + '\'' +
                ", issuer='" + issuer + '\'' +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (this == o)
            return true;
        if (o == null || getClass() != o.getClass())
            return false;

        CallbackContext that = (CallbackContext) o;

        if (emailFromForm != null ? !emailFromForm.equals(that.emailFromForm) : that.emailFromForm != null)
            return false;
        if (code != null ? !code.equals(that.code) : that.code != null)
            return false;
        if (state != null ? !state.equals(that.state) : that.state != null)
            return false;
        if (authStateToken != null ? !authStateToken.equals(that.authStateToken) : that.authStateToken != null)
            return false;
        if (moveUrl != null ? !moveUrl.equals(that.moveUrl) : that.moveUrl != null)
            return false;
        if (request != null ? !request.equals(that.request) : that.request != null)
            return false;
        if (response != null ? !response.equals(that.response) : that.response != null)
            return false;
        if (clientId != null ? !clientId.equals(that.clientId) : that.clientId != null)
            return false;
        if (clientSecret != null ? !clientSecret.equals(that.clientSecret) : that.clientSecret != null)
            return false;
        if (audience != null ? !audience.equals(that.audience) : that.audience != null)
            return false;
        if (scope != null ? !scope.equals(that.scope) : that.scope != null)
            return false;
        if (token != null ? !token.equals(that.token) : that.token != null)
            return false;
        if (strategyKey != null ? !strategyKey.equals(that.strategyKey) : that.strategyKey != null)
            return false;
        if (redirectUri != null ? !redirectUri.equals(that.redirectUri) : that.redirectUri != null)
            return false;
        if (homePage != null ? !homePage.equals(that.homePage) : that.homePage != null)
            return false;
        if (selectedProviderId != null ? !selectedProviderId.equals(that.selectedProviderId)
                : that.selectedProviderId != null)
            return false;
        if (authProviderUrl != null ? !authProviderUrl.equals(that.authProviderUrl) : that.authProviderUrl != null)
            return false;
        return issuer != null ? issuer.equals(that.issuer) : that.issuer == null;
    }

    @Override
    public int hashCode() {
        int result = emailFromForm != null ? emailFromForm.hashCode() : 0;
        result = 31 * result + (code != null ? code.hashCode() : 0);
        result = 31 * result + (state != null ? state.hashCode() : 0);
        result = 31 * result + (authStateToken != null ? authStateToken.hashCode() : 0);
        result = 31 * result + (moveUrl != null ? moveUrl.hashCode() : 0);
        result = 31 * result + (request != null ? request.hashCode() : 0);
        result = 31 * result + (response != null ? response.hashCode() : 0);
        result = 31 * result + (clientId != null ? clientId.hashCode() : 0);
        result = 31 * result + (clientSecret != null ? clientSecret.hashCode() : 0);
        result = 31 * result + (audience != null ? audience.hashCode() : 0);
        result = 31 * result + (scope != null ? scope.hashCode() : 0);
        result = 31 * result + (token != null ? token.hashCode() : 0);
        result = 31 * result + (strategyKey != null ? strategyKey.hashCode() : 0);
        result = 31 * result + (redirectUri != null ? redirectUri.hashCode() : 0);
        result = 31 * result + (homePage != null ? homePage.hashCode() : 0);
        result = 31 * result + (selectedProviderId != null ? selectedProviderId.hashCode() : 0);
        result = 31 * result + (authProviderUrl != null ? authProviderUrl.hashCode() : 0);
        result = 31 * result + (issuer != null ? issuer.hashCode() : 0);
        return result;
    }

}