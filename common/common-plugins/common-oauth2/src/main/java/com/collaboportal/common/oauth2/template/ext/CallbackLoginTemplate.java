package com.collaboportal.common.oauth2.template.ext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.collaboportal.common.context.oauth2.CallbackContext;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.common.oauth2.entity.DTO.UserMasterCollabo;

import com.collaboportal.common.oauth2.exception.OAuth2AuthorizationException;
import com.collaboportal.common.oauth2.exception.OAuth2UserException;
import com.collaboportal.common.oauth2.model.OauthTokenResult;
import com.collaboportal.common.oauth2.processor.AuthProcessor;
import com.collaboportal.common.oauth2.registry.LoginStrategyRegistry;
import com.collaboportal.common.oauth2.service.Oauth2UserMasterService;
import com.collaboportal.common.oauth2.template.OAuth2LoginTemplate;
import com.collaboportal.common.utils.Message;
import com.collaboportal.common.utils.MaintenanceModeUtil;
import jakarta.annotation.PostConstruct;

/**
 * OAuth2コールバック処理のテンプレートを拡張したクラス。
 * テスト環境と本番環境のログインフローを処理し、詳細なログ記録と堅牢なエラーハンドリングを提供します。
 */
public class CallbackLoginTemplate extends OAuth2LoginTemplate {

    private static final Logger logger = LoggerFactory.getLogger(CallbackLoginTemplate.class);

    // 定数化して、タイプミスを防ぎ、管理しやすくする
    private static final String ERROR_REDIRECT_PATH = "/#/error";

    private final AuthProcessor authProcessor;
    private final LoginStrategyRegistry strategyRegistry;
    private final Oauth2UserMasterService oauth2UserMasterService;
    private final JwtService jwtService;

    public CallbackLoginTemplate(AuthProcessor authProcessor, Oauth2UserMasterService oauth2UserMasterService,
            LoginStrategyRegistry strategyRegistry, JwtService jwtService) {
        this.strategyRegistry = strategyRegistry;
        this.oauth2UserMasterService = oauth2UserMasterService;
        this.authProcessor = authProcessor;
        this.jwtService = jwtService;
    }

    @PostConstruct
    public void init() {
        logger.info("ログインストラテジーの初期化を開始します。");
        strategyRegistry.register("test", (context) -> handleTestLogin((CallbackContext) context));
        strategyRegistry.register("prod", (context) -> handleProdLogin((CallbackContext) context));
        logger.info("ログインストラテジーの初期化が完了しました。'test'と'prod'が登録されました。");
    }

    /**
     * テスト環境向けのログイン処理。
     * フォームから送信されたメールアドレスを使用してユーザーを認証します。
     * 
     * @param context 認証コンテキスト
     */
    private void handleTestLogin(CallbackContext context) {
        final String email = context.getEmailFromForm();
        logger.info("【テスト環境ログイン開始】ユーザー: {}", email);

        try {
            // ユーザー情報を取得
            logger.debug("データベースからユーザー情報を検索します。メールアドレス: {}", email);
            UserMasterCollabo user = oauth2UserMasterService.loadByKey(email);
            if (user == null || user.getUserMail() == null) {
                // ユーザーが見つからない場合はAuthorizationExceptionをスロー
                throw new OAuth2AuthorizationException("指定されたメールアドレスのユーザーが見つかりません: " + email);
            }
            logger.debug("ユーザー情報: {}", user);
            user.setRole(user.getRole() == null ? 2 : user.getRole());// テストユーザーには2を加算
            logger.debug("ユーザー情報: {}", user);
            logger.info("ユーザー情報の取得に成功しました。ユーザーID: {}", user.getUserMail());
            // JWTトークンを生成
            logger.debug("ユーザーID {} のためのJWTトークンを生成中...", user.getUserMail());
            String token = jwtService.generateToken(user, JwtConstants.GENERATE_COLLABO_USER_TOKEN);
            // セキュリティのため、トークン自体はログに出力しない
            logger.info("JWTトークンの生成に成功しました。");

            // 認証情報をCookieに設定
            CookieUtil.setSameSiteCookie(context.getResponse(), Message.Cookie.AUTH, token);
            logger.debug("認証用Cookie（{}）の設定が完了しました。", Message.Cookie.AUTH);

            // 成功時のリダイレクト処理
            performSuccessRedirect(context.getResponse(), "/index.html", email);

        } catch (Exception e) {
            // 失敗時のリダイレクト処理
            logger.error("【テスト環境ログイン失敗】ユーザー: {} の処理中にエラーが発生しました。", email, e);
            performErrorRedirect(context.getResponse(), "テスト環境ログインに失敗しました。");
        }
    }

    /**
     * 本番環境向けのログイン処理。
     * OAuth2の認可コードを使用してトークンを取得し、ユーザーを認証します。
     * 
     * @param context 認証コンテキスト
     */
    private void handleProdLogin(CallbackContext context) {
        String temporaryLogId = "AuthCode(末尾5桁):"
                + (context.getCode() != null ? context.getCode().substring(Math.max(0, context.getCode().length() - 5))
                        : "N/A");
        logger.info("【本番環境ログイン開始】{}", temporaryLogId);

        try {
            validateState(context);

            // 認可コードを使ってOAuthトークンを取得
            logger.debug("認可エンドポイントからOAuthトークンの取得を開始します。{}", temporaryLogId);
            OauthTokenResult tokenResult = authProcessor.getOauthTokenFromEndpoint(
                    "authorization_code", context.getCode(), context.getRedirectUri(),
                    context.getAudience(), context.getClientId(), context.getClientSecret(), context.getResponse());

            if (!tokenResult.isSuccess()) {
                throw new OAuth2AuthorizationException("OAuth認可コードの交換に失敗しました。", tokenResult);
            }
            logger.info("OAuthトークンの取得に成功しました。{}", temporaryLogId);

            String email = tokenResult.getEmail();
            logger.debug("tokenResult内容：{}", tokenResult);
            if (email == null || email.isEmpty()) {
                throw new OAuth2AuthorizationException("OAuthレスポンスにユーザーのメールアドレスが含まれていません。", tokenResult);
            }
            logger.debug("トークンからメールアドレスを取得しました: {}", email);

            // メールアドレスでユーザー情報を取得
            logger.debug("データベースからユーザー情報を検索します。メールアドレス: {}", email);
            UserMasterCollabo user = oauth2UserMasterService.loadByKey(email);
            if (user == null) {
                throw new OAuth2UserException(String.format("ユーザー情報が見つかりません：%s", email), email);
            }
            user.setRole(user.getRole() == null ? 2 : user.getRole());// テストユーザーには2を加算
            logger.info("ユーザー情報の取得に成功しました。ユーザーID: {}", user.getUserMail());
            logger.debug("ユーザー情報: {}", user);
            // JWTトークンを生成
            logger.debug("ユーザーID {} のためのJWTトークンを生成中...", user.getUserMail());
            String token = jwtService.generateToken(user, JwtConstants.GENERATE_COLLABO_USER_TOKEN);
            logger.info("JWTトークンの生成に成功しました。");

            // 認証情報をCookieに設定
            CookieUtil.setNoneSameSiteCookie(context.getResponse(), Message.Cookie.AUTH, token);
            logger.debug("認証用Cookie（{}）の設定が完了しました。", Message.Cookie.AUTH);

            // 成功時のリダイレクト処理
            performSuccessRedirect(context.getResponse(), MaintenanceModeUtil.resolveIndexPage(), email);

        } catch (OAuth2AuthorizationException e) {
            // OAuth関連の制御された例外
            logger.warn("【本番環境ログイン失敗】OAuth2処理中に警告レベルのエラーが発生しました。{}", temporaryLogId, e);
            performErrorRedirect(context.getResponse(), "OAuth認証プロセスでエラーが発生しました。");
        } catch (OAuth2UserException e) {
            // 社員マスタ未登録など、ユーザー情報起因の例外
            logger.error("【本番環境ログイン失敗】社員マスタ未登録のためログインできませんでした。{}", temporaryLogId, e);
            performErrorRedirect(context.getResponse(), "OAuth認証プロセスでエラーが発生しました。");
        } catch (Exception e) {
            // 予期せぬ例外
            logger.error("【本番環境ログイン失敗】予期せぬエラーが発生しました。{}", temporaryLogId, e);
            performErrorRedirect(context.getResponse(), "ログイン処理中に予期せぬシステムエラーが発生しました。");
        }
    }

    /**
     * ログイン成功時のリダイレクト処理を行う補助メソッド。
     * 
     * @param response       HTTPレスポンスオブジェクト
     * @param successUrl     リダイレクト先のURL
     * @param userIdentifier ログ出力用のユーザー識別子（メールアドレスなど）
     */
    private void performSuccessRedirect(BaseResponse response, String successUrl, String userIdentifier) {
        try {
            logger.info("ログイン成功。ユーザー: {} を {} へリダイレクトします。", userIdentifier, successUrl);
            response.redirect(successUrl);
        } catch (Exception e) {
            logger.error("【エラー】成功後のリダイレクト処理に失敗しました。ユーザー: {}, URL: {}", userIdentifier, successUrl, e);
        }
    }

    /**
     * ログイン失敗時のエラーリダイレクト処理を行う補助メソッド。
     * このメソッドは、フロントエンドがCookieを読み取ってエラーページを表示する設計を前提としています。
     * 
     * @param response   HTTPレスポンスオブジェクト
     * @param logMessage ログに出力するエラーメッセージ
     */
    private void performErrorRedirect(BaseResponse response, String logMessage) {
        try {
            // 1. フロントエンドにエラーがあったことを伝えるためにCookieを設定
            CookieUtil.setSameSiteCookie(response, Message.Cookie.MOVE_URL, ERROR_REDIRECT_PATH);
            logger.debug("エラー通知用Cookie（{}={}）を設定しました。", Message.Cookie.MOVE_URL, ERROR_REDIRECT_PATH);

            // 2. ユーザーをアプリケーションのトップページにリダイレクト
            // (フロントエンドのJavaScriptがCookieを読み取り、/#/errorに遷移させる)
            String indexPage = MaintenanceModeUtil.resolveIndexPage();
            logger.warn("{}, トップページ ({}) へリダイレクトします。", logMessage, indexPage);

            response.redirect(indexPage);

        } catch (Exception e) {
            logger.error("【エラー】エラーリダイレクト処理の実行中に、さらなる例外が発生しました。", e);
        }
    }

    private void validateState(CallbackContext context) {
        String requestState = context.getState();
        // String cookieState = context.getAuthStateToken();
        if (requestState == null || requestState.isBlank()) {
            throw new OAuth2AuthorizationException("OAuth2レスポンスにstateパラメータが含まれていません。");
        }
        try {
            context.getResponse().deleteCookie(Message.Cookie.AUTH_STATE, "/", null);
        } catch (Exception e) {
            logger.debug("AuthState cookie cleanup failed: {}", e.getMessage());
        }
    }
}
