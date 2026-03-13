// ========== テンプレートメソッド抽象基底クラス - OAuth2ログインテンプレート ==========
package com.collaboportal.common.oauth2.template;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.context.web.BaseCookie;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.oauth2.context.OAuth2ProviderContext;
import com.collaboportal.common.oauth2.model.OAuth2ClientRegistration;
import com.collaboportal.common.utils.Message;
import com.collaboportal.common.oauth2.factory.OAuth2ClientRegistrationFactory;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

/**
 * テンプレートメソッドパターン - OAuth2ログインフロー抽象テンプレート
 * 責務：OAuth2ログインの標準フローを定義し、変化点を抽象化してサブクラスで実現
 * 利点：統一されたフロー制御、コード重複の削減、保守・拡張の容易性
 */
public abstract class OAuth2LoginTemplate {

    protected final Logger logger = LoggerFactory.getLogger(getClass());
    protected final OAuth2ClientRegistrationFactory clientRegistrationFactory;

    public OAuth2LoginTemplate(OAuth2ClientRegistrationFactory clientRegistrationFactory) {
        this.clientRegistrationFactory = clientRegistrationFactory;
    }

    public OAuth2LoginTemplate() {
        this.clientRegistrationFactory = null;
    }

    /**
     * メインロジック処理 - 非抽象メソッド、デフォルト実装を提供
     */
    public void executeLogin(OAuth2ProviderContext context) {
        // デフォルト実装を提供、サブクラスでオーバーライド可能
        logger.debug("デフォルトログイン処理を実行");
    }

    /**
     * コールバックロジック処理 - 非抽象メソッド、デフォルト実装を提供
     */
    public void executeCallback(OAuth2ProviderContext context, String code, String state) {
        // デフォルト実装を提供、サブクラスでオーバーライド可能
        logger.debug("デフォルトコールバック処理を実行");
    }

    // ========== 具象メソッド - サブクラスでオーバーライド可能だがデフォルト実装あり ==========

    /**
     * ログインリクエストの検証 - サブクラスでオーバーライド可能
     */
    protected boolean validateLoginRequest(OAuth2ProviderContext context) {
        return context.getSelectedProviderId() != null &&
                context.getRequest() != null &&
                context.getResponse() != null;
    }

    /**
     * コールバックリクエストの検証 - サブクラスでオーバーライド可能
     */
    protected boolean validateCallbackRequest(OAuth2ProviderContext context, String code, String state) {
        return code != null && !code.isEmpty() &&
                state != null && !state.isEmpty();
    }

    /**
     * クライアント登録情報の取得
     */
    protected OAuth2ClientRegistration getClientRegistration(OAuth2ProviderContext context) {
        if (clientRegistrationFactory == null) {
            return null;
        }
        return clientRegistrationFactory.getClientRegistration(context.getSelectedProviderId());
    }

    /**
     * リダイレクトの実行
     * 注意：ここでのリダイレクトが正常に動作しない可能性がある理由：
     * 1. redirect実行後にflush()を呼び出してレスポンスの送信を確実にしていない
     * 2. redirect後に他のコードが継続実行され、レスポンスが上書きされる可能性
     * 3. レスポンスステータスコードが302または303に正しく設定されていない可能性
     */
    protected void performRedirect(OAuth2ProviderContext context, String authorizationUrl) throws IOException {
        BaseResponse response = context.getResponse();
        try {
            // 適切なステータスコードを設定
            response.setStatus(302);
            // リダイレクトを実行
            response.redirect(authorizationUrl);
            // レスポンスが即座に送信されることを確保
            response.flush();
            logger.debug("リダイレクトを正常に実行しました: {}", authorizationUrl);
        } catch (Exception e) {
            logger.error("リダイレクトに失敗しました、対象URL: {}", authorizationUrl, e);
            throw new IOException("リダイレクトに失敗しました", e);
        }
    }

    /**
     * ログインエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleLoginError(OAuth2ProviderContext context, String errorMessage) {
        try {
            BaseResponse response = context.getResponse();
            response.addCookie(new BaseCookie(Message.Cookie.MOVE_URL, "/#/error").setPath("/")
                    .setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None")
                    .setSecure(ConfigManager.getConfig().isCookieSecure()));
            // エラー処理後もflushを実行することを確保
            response.flush();
        } catch (Exception e) {
            logger.error("ログインエラーの処理中に例外が発生しました", e);
        }
    }

    /**
     * コールバックエラーの処理 - サブクラスでオーバーライド可能
     */
    protected void handleCallbackError(OAuth2ProviderContext context, String errorMessage) {
        try {
            BaseResponse response = context.getResponse();
            response.addCookie(new BaseCookie(Message.Cookie.MOVE_URL, "/#/error").setPath("/")
                    .setMaxAge(ConfigManager.getConfig().getCookieExpiration()).setSameSite("None")
                    .setSecure(ConfigManager.getConfig().isCookieSecure()));
            // エラー処理後もflushを実行することを確保
            response.flush();
        } catch (Exception e) {
            logger.error("コールバックエラーの処理中に例外が発生しました", e);
        }
    }

    // ========== 抽象メソッドを具象メソッドに変更、デフォルト実装を提供 ==========

    /**
     * 状態情報の保存 - デフォルト実装を提供、サブクラスでオーバーライド可能
     */
    protected void storeStateInformation(OAuth2ProviderContext context, OAuth2ClientRegistration registration) {
        // デフォルト実装を提供
        logger.debug("状態情報を保存");
    }

    /**
     * 状態情報の保存（オーバーロードメソッド） - JwtValidationTemplateをサポートするため
     */
    protected void storeStateInformation(OAuth2ProviderContext context) {
        // デフォルト実装を提供
        logger.debug("状態情報を保存（registrationパラメータなし）");
    }

    /**
     * 認可コードとアクセストークンの交換 - デフォルト実装を提供、サブクラスでオーバーライド可能
     */
    protected String exchangeCodeForToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String code) {
        // デフォルト実装を提供
        logger.debug("認可コードをアクセストークンに交換");
        return null;
    }

    /**
     * ユーザー情報の取得 - デフォルト実装を提供、サブクラスでオーバーライド可能
     */
    protected Object fetchUserInfo(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String accessToken) {
        // デフォルト実装を提供
        logger.debug("ユーザー情報を取得");
        return null;
    }

    /**
     * JWTトークンの生成 - デフォルト実装を提供、サブクラスでオーバーライド可能
     */
    protected String generateJwtToken(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            Object userInfo) {
        // デフォルト実装を提供
        logger.debug("JWTトークンを生成");
        return null;
    }

    /**
     * ログインの完了 - デフォルト実装を提供、サブクラスでオーバーライド可能
     */
    protected void completeLogin(OAuth2ProviderContext context, OAuth2ClientRegistration registration,
            String jwtToken, Object userInfo) {
        // デフォルト実装を提供
        logger.debug("ログインを完了");
    }
}
