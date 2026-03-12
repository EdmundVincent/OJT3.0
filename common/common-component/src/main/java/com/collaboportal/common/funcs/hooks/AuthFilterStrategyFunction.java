package com.collaboportal.common.funcs.hooks;

import java.io.IOException;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;


/**
 * 認証フィルター戦略関数インターフェース
 * 認証フィルターの処理をカスタマイズするための関数型インターフェース
 */
@FunctionalInterface
public interface AuthFilterStrategyFunction {
    /**
     * 認証フィルターの処理を実行するメソッド
     * @param request HTTPリクエストオブジェクト
     * @param response HTTPレスポンスオブジェクト
     * @param chain フィルターチェーン
     * @throws IOException 入出力エラーが発生した場合
     * @throws ServletException サーブレット処理中にエラーが発生した場合
     */
    void handle(BaseRequest request, BaseResponse response, FilterChain chain) throws IOException, ServletException;

}
