package com.collaboportal.common.interceptor.impl;

import java.util.List;
import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.AutoConfigureBefore;
import org.springframework.boot.autoconfigure.web.servlet.WebMvcAutoConfiguration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.config.CommonConfig;
import com.collaboportal.common.context.CommonHolder;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.interceptor.AuthInterceptor;
import com.collaboportal.common.jwt.constants.JwtConstants;
import com.collaboportal.common.jwt.service.JwtService;
import com.collaboportal.common.jwt.utils.JwtClaimUtils;
import com.collaboportal.common.utils.Message;
import com.collaboportal.common.utils.WebContextUtil;

@AutoConfiguration
@AutoConfigureBefore(WebMvcAutoConfiguration.class)
public class InterceptorConfig implements WebMvcConfigurer {

    private static final Logger logger = LoggerFactory.getLogger(InterceptorConfig.class);

    // ★ 企画IDとして認識するリクエストパラメータ名のリストを定義
    private static final List<String> PROJECT_ID_PARAM_NAMES = Arrays.asList("projectId", "kkkId", "KikakuId",
            "kikaku_id", "pre_kikaku_id");

    private final JwtService jwtService;

    public InterceptorConfig(JwtService jwtService) {
        logger.info("認証インターセプタの登録が成功しました");
        this.jwtService = jwtService;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        CommonConfig commonConfig = ConfigManager.getConfig();
        if (!commonConfig.isAuthInterceptorEnabled()) {
            logger.info("認証インターセプタは設定により無効化されています");
            return;
        }

        List<String> authenticationList = commonConfig.getAuthenticationList();
        registry.addInterceptor(new AuthInterceptor(
                handler -> {
                    logger.info("認証用パス: {}", authenticationList);
                    CommonRouter.match(authenticationList).check(
                            r -> {
                                logger.info("認証開始");
                                if (CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH) != null
                                        && !CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH).isEmpty()) {
                                    boolean result = jwtService.validateToken(
                                            CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH),
                                            JwtConstants.ROLE_VALIDATION);
                                    logger.info("認証完了");
                                    logger.debug("JWT検証結果: {}", result);
                                    if (!result) {
                                        throw new StopMatchException("認証に失敗しました");
                                    }
                                } else {
                                    logger.info("トークンが存在しません");
                                }
                            });
                })).addPathPatterns("/**");
        registry.addInterceptor(new AuthInterceptor(
                handler -> {
                    logger.info("企画IDマーチ");
                    CommonRouter.match("/**").check(
                            r -> {
                                logger.info("企画ID検証処理を開始");

                                // ★ リスト内のパラメータ名を順番にチェックして企画IDを取得
                                String projectIdParam = null;
                                for (String paramName : PROJECT_ID_PARAM_NAMES) {
                                    String value = WebContextUtil.getParameters(paramName);
                                    if (value != null && !value.isEmpty()) {
                                        projectIdParam = value;
                                        logger.info("リクエストパラメータ '{}' から企画ID '{}' を取得しました", paramName, projectIdParam);
                                        break; // 最初に見つかったパラメータを使用する
                                    }
                                }

                                if (CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH) == null
                                        || CommonHolder.getRequest().getCookieValue(Message.Cookie.AUTH).isEmpty()) {
                                    logger.info("トークンが存在しません");
                                    return;

                                }
                                List<String> projectIdJwt = JwtClaimUtils
                                        .getProjectIds(WebContextUtil.getCookieValue(Message.Cookie.AUTH));
                                logger.info("JWT内の企画IDリスト: {}", projectIdJwt);

                                // ★ 取得した企画IDで後続のチェック処理を実行
                                if (projectIdParam != null && !projectIdParam.isEmpty()) {
                                    if (projectIdJwt.contains(projectIdParam)) {
                                        logger.info("企画IDの検証に成功しました");
                                    } else {
                                        // ログのメッセージを具体的に変更
                                        throw new StopMatchException(
                                                String.format("リクエストされた企画ID '%s' の権限がありません", projectIdParam));
                                    }
                                } else {
                                    logger.info("リクエストに検証対象の企画IDパラメータが存在しません");
                                }
                            });
                })).addPathPatterns("/**");

    }

}