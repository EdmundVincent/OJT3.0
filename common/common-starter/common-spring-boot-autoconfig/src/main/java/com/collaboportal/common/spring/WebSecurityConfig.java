package com.collaboportal.common.spring;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.encoder.UserIdSaltedBCryptPasswordEncoder;
import com.collaboportal.common.strategy.security.CsrfStrategy;
import com.collaboportal.common.strategy.security.ExceptionHandlingStrategy;
import com.collaboportal.common.strategy.security.FirewallStrategy;
import com.collaboportal.common.strategy.security.SecurityConfigStrategy;
import com.collaboportal.common.strategy.security.StatelessSessionStrategy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;

import java.util.List;

/**
 * Webセキュリティ設定クラス
 * Spring Securityの設定を管理します
 */
@AutoConfiguration
public class WebSecurityConfig {

    // ロガー
    private static final Logger logger = LoggerFactory.getLogger(WebSecurityConfig.class);

    /**
     * コンストラクタ
     * 初期化時にログを出力します
     */
    public WebSecurityConfig() {
        logger.debug("セキュリティ設定の初期化を完了しました");
    }

    // 認証不要モードの設定値
    private final String noAuthMode = ConfigManager.getConfig().getNoAuthorization();
    // 環境フラグ
    private final String envFlag = ConfigManager.getConfig().getEnvFlag();
    // 認証不要URL
    private final String noAuthUrl = ConfigManager.getConfig().getNoAuthUrl();

    /**
     * セキュリティフィルターチェーンを設定します
     * 
     * @param http HttpSecurityオブジェクト
     * @return SecurityFilterChain セキュリティフィルターチェーン
     * @throws Exception 設定中にエラーが発生した場合
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        logger.debug("セキュリティフィルターチェーンの設定を開始します");

        // セキュリティストラテジーのリストを作成
        List<SecurityConfigStrategy> strategies = List.of(
                new CsrfStrategy(), // CSRF対策
                new ExceptionHandlingStrategy(), // 例外処理
                new StatelessSessionStrategy(), // ステートレスセッション
                new FirewallStrategy(noAuthMode, envFlag, noAuthUrl.split(",")) // 認可設定
        );

        // 各ストラテジーを適用
        for (SecurityConfigStrategy strategy : strategies) {
            logger.debug("セキュリティストラテジーを適用中: {}", strategy.getClass().getSimpleName());
            strategy.configure(http);
        }

        logger.debug("セキュリティフィルターチェーンの設定を完了しました");
        return http.build();
    }

    /**
     * 認証マネージャーを取得します
     * 
     * @param config AuthenticationConfigurationオブジェクト
     * @return AuthenticationManager 認証マネージャー
     * @throws Exception 取得中にエラーが発生した場合
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        logger.debug("認証マネージャーを取得します");
        return config.getAuthenticationManager();
    }

    /**
     * パスワードエンコーダーを取得します
     * 
     * @return PasswordEncoder パスワードエンコーダー
     */
    @Bean
    public UserIdSaltedBCryptPasswordEncoder passwordEncoder() {
        return new UserIdSaltedBCryptPasswordEncoder(12);
    }

}
