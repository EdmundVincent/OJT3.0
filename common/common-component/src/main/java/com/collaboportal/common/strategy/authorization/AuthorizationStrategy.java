package com.collaboportal.common.strategy.authorization;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.exception.AuthenticationException;

/**
 * 認証戦略を定義する関数型インターフェースです。
 */
@FunctionalInterface
public interface AuthorizationStrategy {
    /**
     * 認証処理を実行します。
     * @param req 現在のリクエスト
     * @param resp 現在のレスポンス
     * @throws AuthenticationException 認証に失敗した場合
     */
    void authenticate(BaseRequest req, BaseResponse resp) throws AuthenticationException;
}