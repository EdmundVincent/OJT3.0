package com.collaboportal.common.context.oauth2;

import java.io.Serializable;
import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

public class CallbackContext implements Serializable {
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
    public CallbackContext() {}
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
    public String getEmailFromForm() { return emailFromForm; }
    public String getCode() { return code; }
    public String getState() { return state; }
    public String getAuthStateToken() { return authStateToken; }
    public String getMoveUrl() { return moveUrl; }
    public BaseRequest getRequest() { return request; }
    public BaseResponse getResponse() { return response; }
    public CallbackContext request(BaseRequest request) { this.request = request; return this; }
    public CallbackContext response(BaseResponse response) { this.response = response; return this; }
    public String getClientId() { return clientId; }
    public String getClientSecret() { return clientSecret; }
    public String getAudience() { return audience; }
    public String getScope() { return scope; }
    public String getToken() { return token; }
    public String getStrategyKey() { return strategyKey; }
    public String getRedirectUri() { return redirectUri; }
    public String getHomePage() { return homePage; }
    public String getSelectedProviderId() { return selectedProviderId; }
    public String getAuthProviderUrl() { return authProviderUrl; }
    public String getIssuer() { return issuer; }
    public CallbackContext setAuthStateToken(String authStateToken) { this.authStateToken = authStateToken; return this; }
    public CallbackContext setSelectedProviderId(String selectedProviderId) { this.selectedProviderId = selectedProviderId; return this; }
    public CallbackContext setStrategyKey(String strategyKey) { this.strategyKey = strategyKey; return this; }
    public static CallbackContext builder() { return new CallbackContext(); }
    public CallbackContext emailFromForm(String email) { this.emailFromForm = email; return this; }
    public CallbackContext setIssuer(String issuer) { this.issuer = issuer; return this; }
    public CallbackContext code(String code) { this.code = code; return this; }
    public CallbackContext state(String state) { this.state = state; return this; }
    public CallbackContext moveUrl(String moveUrl) { this.moveUrl = moveUrl; return this; }
    public CallbackContext setClientId(String clientId) { this.clientId = clientId; return this; }
    public CallbackContext setClientSecret(String clientSecret) { this.clientSecret = clientSecret; return this; }
    public CallbackContext setAudience(String audience) { this.audience = audience; return this; }
    public CallbackContext setScope(String scope) { this.scope = scope; return this; }
    public CallbackContext setToken(String token) { this.token = token; return this; }
    public CallbackContext setRedirectUri(String redirectUri) { this.redirectUri = redirectUri; return this; }
    public CallbackContext setHomePage(String homePage) { this.homePage = homePage; return this; }
    public CallbackContext setAuthProviderUrl(String authProviderUrl) { this.authProviderUrl = authProviderUrl; return this; }
    public CallbackContext build() { return this; }
}
