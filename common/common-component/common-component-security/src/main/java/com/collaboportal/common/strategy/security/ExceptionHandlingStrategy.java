package com.collaboportal.common.strategy.security;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;

public class ExceptionHandlingStrategy implements SecurityConfigStrategy {
    private static final Logger logger = LoggerFactory.getLogger(ExceptionHandlingStrategy.class);
    @Override
    public void configure(HttpSecurity http) throws Exception {
        logger.info("例外処理設定を開始します");
        http.exceptionHandling(handling -> handling
            .authenticationEntryPoint((request, response, authException) -> {
                try {
                    logger.warn("認証エラーが発生しました。401エラーレスポンスを返します");
                    sendJson(response, 401, "このページ接続できません");
                } catch (Exception e) {
                    logger.error("JSONレスポンスの送信に失敗しました", e);
                    throw new RuntimeException("Failed to send JSON response", e);
                }
            })
            .accessDeniedHandler((request, response, exception) -> {
                try {
                    logger.warn("アクセス拒否が発生しました。403エラーレスポンスを返します");
                    sendJson(response, 403, "アクセスが拒否されました");
                } catch (Exception e) {
                    logger.error("JSONレスポンスの送信に失敗しました", e);
                    throw new RuntimeException("Failed to send JSON response", e);
                }
            }));
        logger.info("例外処理設定が完了しました");
    }
    private void sendJson(HttpServletResponse response, int code, String msg) throws Exception {
        logger.debug("JSONレスポンスの生成を開始します。コード: {}, メッセージ: {}", code, msg);
        Map<String, Object> body = new HashMap<>();
        response.setStatus(code == 403 ? 200 : code);
        body.put("nb_err_cod", String.valueOf(code));
        body.put(code == 403 ? "err_msg" : "message", msg);
        body.put("err_level", "W");
        response.setHeader("content-type", "application/json");
        response.setCharacterEncoding("UTF-8");
        response.getWriter().write(new ObjectMapper().writeValueAsString(body));
        logger.debug("JSONレスポンスの送信が完了しました");
    }
}
