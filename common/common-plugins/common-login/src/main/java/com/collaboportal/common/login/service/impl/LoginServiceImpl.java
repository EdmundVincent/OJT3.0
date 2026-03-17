package com.collaboportal.common.login.service.impl;

import com.collaboportal.common.encoder.UserIdSaltedBCryptPasswordEncoder;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.login.mapper.LoginMapper;
import com.collaboportal.common.login.model.DTO.UserMasterEPL;
import com.collaboportal.api.login.LoginRequest;
import com.collaboportal.api.login.LoginResult;
import com.collaboportal.api.login.LoginService;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * ログインサービスの実装クラス
 * パスワード認証、失敗ロック、JWT生成等の主要なログインロジックを実装。
 */
@Service
public class LoginServiceImpl implements LoginService {

    private static final Logger logger = LoggerFactory.getLogger(LoginServiceImpl.class);

    private final LoginMapper loginMapper;
    private final UserIdSaltedBCryptPasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    private static final String DUMMY_BCRYPT = "$2a$12$C8GJp3o0yD0c3nEwP2LqJeX3h2u0sP8wF4k0gD8nNn2m6Kq3h6TGG";

    /**
     * コンストラクタ
     * 依存性注入により必要なサービスとコンポーネントを初期化。
     *
     * @param loginMapper     データベース操作用のMyBatis Mapper
     * @param passwordEncoder パスワード暗号化・検証サービス
     * @param jwtService      JWT生成サービス
     */
    public LoginServiceImpl(LoginMapper loginMapper, JwtService jwtService,
            UserIdSaltedBCryptPasswordEncoder passwordEncoder) {
        this.loginMapper = loginMapper;
        this.jwtService = jwtService;
        this.passwordEncoder = passwordEncoder;
        logger.info("ログインサービスが初期化されました - LoginMapper: {}, JwtService: {}, PasswordEncoder: {}",
                loginMapper != null ? "有効" : "無効",
                jwtService != null ? "有効" : "無効",
                passwordEncoder != null ? "有効" : "無効");
    }

    /**
     * ユーザーログイン認証
     * 1. アカウントがロックされているかを確認。
     * 2. データベースからユーザー情報を取得。
     * 3. パスワードを検証。
     * 4. ログイン成功後、JWTを生成して返却。
     * 5. ログイン失敗後、失敗回数を記録。
     *
     * @param loginRequest ユーザー認証情報を含むログインリクエスト
     * @return JWTを含むログインレスポンス
     * @throws AuthenticationException 認証失敗時にスロー
     */
    @Override
    public LoginResult login(LoginRequest loginRequest) {
        logger.info("ログイン処理を開始 - ユーザー: {}", loginRequest.getEmail());
        final String email = safeTrim(loginRequest.getEmail());
        final String raw = safeTrim(loginRequest.getPassword());
        try {
            // ユーザー検索開始
            logger.debug("データベースからユーザー情報を検索 - メールアドレス: {}", loginRequest.getEmail());
            UserMasterEPL user = loginMapper.findUserByUserId(loginRequest.getEmail());

            if (user == null) {
                // ダミー計算でタイミングを揃える（パラメータ順を正しく設定）
                passwordEncoder.matches(raw, "dummyUser", DUMMY_BCRYPT);
                logger.warn("ユーザーが見つかりません - メール: {}", email);
                return LoginResult.fail("401", "ユーザー名またはパスワードが間違っています");
            }

            logger.debug("ユーザーが見つかりました - ユーザーID: {}", user.getUserId());

            final String userId = safeTrim(user.getUserId());
            final String stored = user.getPassword().trim();
            // パスワード検証開始
            logger.debug("パスワード検証を開始 - ユーザーID: {}", user.getUserId());

            if (isBlank(userId) || isBlank(stored)) {
                logger.warn("ユーザー情報不整合 - userIdまたはパスワードが空 - メール: {}", email);
                return LoginResult.fail("401", "ユーザー名またはパスワードが間違っています");
            }

            if (!passwordEncoder.matches(raw, userId, stored)) {
                logger.warn("パスワード検証NG - userId: {}, 既存のパスワード：{}", userId, stored);
                return LoginResult.fail("401", "ユーザー名またはパスワードが間違っています");
            }

            logger.debug("パスワード検証に成功 - ユーザーID: {}", user.getUserId());

            // JWT生成開始
            logger.debug("JWTトークン生成を開始 - ユーザーID: {}", user.getUserId());
            String token = jwtService.generateToken(user, JwtConstants.GENERATE_EPL_USER_TOKEN);
            logger.debug("JWTトークン生成完了 - ユーザーID: {}, トークン長: {}", user.getUserId(), token != null ? token.length() : 0);

            Byte role = user.getRole();
            String id = user.getUserId();

            logger.info("ログイン処理が正常に完了 - ユーザーID: {}, ロール: {}", id, role);
            // tokenを結果に含め、Controller層でCookie設定を処理
            return LoginResult.okWithRole(id, role, token);

        } catch (Exception e) {
            logger.error("ログイン処理中にエラーが発生 - メールアドレス: {}, エラー: {}",
                    loginRequest.getEmail(), e.getMessage(), e);
            return LoginResult.fail("500", "ログイン処理中にエラーが発生しました");
        }
    }

    private static String safeTrim(String s) {
        return s == null ? null : s.trim();
    }

    private static boolean isBlank(String s) {
        return s == null || s.isEmpty();
    }
}
