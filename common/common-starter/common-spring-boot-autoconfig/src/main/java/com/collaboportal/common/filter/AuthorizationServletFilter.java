
package com.collaboportal.common.filter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import jakarta.servlet.*;

import com.collaboportal.common.Router.CommonRouter;
import com.collaboportal.common.context.CommonHolder;

import com.collaboportal.common.exception.BackResultException;
import com.collaboportal.common.exception.StopMatchException;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * 認可サーブレットフィルター
 * リクエストの認証と認可を処理し、指定されたルートパターンに基づいてアクセス制御を行います。
 */
public class AuthorizationServletFilter implements Filter, AuthFilter {

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationServletFilter.class);

    // ------------------------ このフィルターのインターセプトと通過のルート設定

    /**
     * インターセプトルート
     * このリストに含まれるパターンにマッチするリクエストは認証が必要です
     */
    public List<String> includeList = new ArrayList<>();

    /**
     * 通過ルート
     * このリストに含まれるパターンにマッチするリクエストは認証をスキップします
     */
    public List<String> excludeList = new ArrayList<>();

    /**
     * インクルードパスを追加します
     * @param paths 追加するパスパターン
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter addInclude(String... paths) {
        includeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * エクスクルードパスを追加します
     * @param paths 追加するパスパターン
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter addExclude(String... paths) {
        excludeList.addAll(Arrays.asList(paths));
        return this;
    }

    /**
     * インクルードリストを設定します
     * @param pathList パスリスト
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter setIncludeList(List<String> pathList) {
        includeList = pathList;
        return this;
    }

    /**
     * エクスクルードリストを設定します
     * @param pathList パスリスト
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter setExcludeList(List<String> pathList) {
        excludeList = pathList;
        return this;
    }

    /**
     * デフォルトの認証戦略
     * 何も処理しないデフォルト実装
     */
    public AuthorizationStrategy auth = (req, resp) -> {
    };

    /**
     * デフォルトのエラー処理戦略
     * エラーをそのまま返すデフォルト実装
     */
    public AuthorizationErrorStrategy error = e -> {
        return e;
    };

    /**
     * デフォルトの前処理認証戦略
     * 何も処理しないデフォルト実装
     */
    public AuthorizationStrategy beforeAuth = (req, resp) -> {
    };

    /**
     * 認証戦略を設定します
     * @param auth 認証戦略
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter setAuth(AuthorizationStrategy auth) {
        this.auth = auth;
        return this;
    }

    /**
     * エラー処理戦略を設定します
     * @param error エラー処理戦略
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter setError(AuthorizationErrorStrategy error) {
        this.error = error;
        return this;
    }

    /**
     * 前処理認証戦略を設定します
     * @param beforeAuth 前処理認証戦略
     * @return このフィルターインスタンス
     */
    @Override
    public AuthorizationServletFilter setBeforeAuth(AuthorizationStrategy beforeAuth) {
        this.beforeAuth = beforeAuth;
        return this;
    }

    /**
     * フィルターのメイン処理メソッド
     * すべてのHTTPリクエストに対して実行されます
     * @param request サーブレットリクエスト
     * @param response サーブレットレスポンス
     * @param chain フィルターチェーン
     * @throws IOException IO例外
     * @throws ServletException サーブレット例外
     */
    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        try {
            logger.info("[AUTH    ] tid={} uri={}", Thread.currentThread().getId(), CommonHolder.getRequest().getRequestPath());

            // グローバル前処理フィルターを実行
            // この処理はすべてのリクエストに対して実行されます（includeList/excludeListの制限を受けません）
            beforeAuth.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
            if (response.isCommitted()) {
                logger.debug("前処理認証がレスポンスを確定させました。フィルターチェーンを終了します。");
                return;
            }

            if ("OPTIONS".equalsIgnoreCase(CommonHolder.getRequest().getMethod())) {
                CommonHolder.getResponse().setStatus(204);
                return;
            }
            
            // ルートマッチングを実行し、条件に合致した場合のみ認証処理を実行
            CommonRouter.match(includeList).notMatch(excludeList).check(r -> {
                // メイン認証処理を実行
                auth.authenticate(CommonHolder.getRequest(), CommonHolder.getResponse());
            });
            if (response.isCommitted()) {
                logger.debug("認証戦略がレスポンスを確定させました。フィルターチェーンを終了します。");
                return;
            }

        } catch (StopMatchException e) {
            // ルートマッチング失敗時の例外処理
            throw new RuntimeException("ルートマッチングに失敗しました");

        } catch (Throwable e) {
            // 1. エラー処理戦略の結果を取得
            // BackResultExceptionの場合はメッセージをそのまま使用、それ以外はエラー戦略を適用
            String errorResult = (e instanceof BackResultException) ? e.getMessage() : String.valueOf(error.run(e));

            // レスポンスのコンテンツタイプが設定されていない場合はJSONに設定
            if (response.getContentType() == null) {
                response.setContentType("application/json;charset=UTF-8");
            }
            // エラー結果をクライアントに返送
            response.getWriter().print(errorResult);
            return;
        }

        // 正常処理時：次のフィルターまたはサーブレットに処理を委譲
            chain.doFilter(request, response);
    }

}
