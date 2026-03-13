package com.collaboportal.common.strategy;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

/**
 * CORSヘッダーの設定を行う関数型インターフェース。
 */
@FunctionalInterface
public interface CorsHandleFunction {

    /**
     * リクエスト/レスポンスに対してCORSヘッダーを設定する。
     *
     * @param request  現在のリクエスト
     * @param response 現在のレスポンス
     */
    void execute(BaseRequest request, BaseResponse response);
}
