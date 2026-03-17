package com.collaboportal.common.login.controller;

import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.jwt.utils.CookieUtil;
import com.collaboportal.api.login.LoginRequest;
import com.collaboportal.api.login.LoginResponseBody;
import com.collaboportal.api.login.LoginResult;
import com.collaboportal.api.login.LoginService;
import com.collaboportal.common.utils.Message;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 認証コントローラー
 * /auth/login はデータベース認証のみを処理する。
 * CORS対応と安全なCookie設定を含む
 */
@RestController
@RequestMapping("/auth")
public class DatabaseAuthController {

    private static final Logger logger = LoggerFactory.getLogger(DatabaseAuthController.class);

    private final LoginService loginService;

    public DatabaseAuthController(LoginService loginService) {
        this.loginService = loginService;
        logger.info("データベース認証コントローラーが初期化されました");
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponseBody> login(@RequestBody LoginRequest loginRequest) {

        logger.info("ログインリクエストを受信しました。ユーザー: {}", loginRequest.getEmail());
        BaseResponse response = CommonHolder.getResponse();

        if (loginRequest.getEmail() == null || loginRequest.getEmail().trim().isEmpty()) {
            logger.warn("無効なログインリクエスト: メールアドレスが空です");
            return ResponseEntity
                    .badRequest()
                    .body(LoginResponseBody.fail("400", "メールアドレスは必須項目です"));
        }

        if (loginRequest.getPassword() == null || loginRequest.getPassword().trim().isEmpty()) {
            logger.warn("無効なログインリクエスト: パスワードが空です");
            return ResponseEntity
                    .badRequest()
                    .body(LoginResponseBody.fail("400", "パスワードは必須項目です"));
        }

        try {
            LoginResult result = loginService.login(loginRequest);

            if (result.success()) {
                logger.info("ログインが成功しました。ユーザーID: {}, メールアドレス: {}",
                        result.userId(), loginRequest.getEmail());

                if (result.token() != null && !result.token().isEmpty()) {
                    CookieUtil.setNoneSameSiteCookie(response, Message.Cookie.AUTH, result.token());
                    CookieUtil.setNoneSameSiteCookie(response, "role", result.role().toString());
                    logger.info("認証Cookieが設定されました。ユーザーID: {}", result.userId());
                } else {
                    logger.warn("JWTトークンが生成されませんでした。ユーザーID: {}", result.userId());
                }

                // リダイレクトではなくJSONレスポンスを返し、フロントエンドで遷移を処理させる
                return ResponseEntity
                        .ok()
                        .header("Content-Type", "application/json")
                        .body(LoginResponseBody.ok("/index.html"));

            } else {
                logger.warn("ログインが失敗しました。ユーザー: {}, 理由: {}",
                        loginRequest.getEmail(),
                        result.message() != null ? result.message() : "不明な理由");
                return ResponseEntity
                        .status(401)
                        .header("Content-Type", "application/json")
                        .body(LoginResponseBody.fail("401", "ユーザー名またはパスワードが間違っています"));
            }

        } catch (Exception e) {
            logger.error("ログイン処理中にエラーが発生しました。ユーザー: {}, エラー: {}",
                    loginRequest.getEmail(), e.getMessage(), e);

            return ResponseEntity
                    .status(500)
                    .header("Content-Type", "application/json")
                    .body(LoginResponseBody.fail("500", "内部サーバーエラー"));
        }
    }

}
