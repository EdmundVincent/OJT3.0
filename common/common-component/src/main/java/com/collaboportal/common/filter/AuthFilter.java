package com.collaboportal.common.filter;

import java.util.List;

import com.collaboportal.common.strategy.authorization.AuthorizationStrategy;
import com.collaboportal.common.strategy.authorization.AuthorizationErrorStrategy;


@SuppressWarnings("rawtypes")
public interface AuthFilter {

    AuthFilter addInclude(String... patterns);

    AuthFilter addExclude(String... patterns);


    AuthFilter setIncludeList(List<String> pathList);

    /**
     * [ 除外ルート ] コレクションを設定
     * @param pathList ルートコレクション
     * @return オブジェクト自身
     */
    AuthFilter setExcludeList(List<String> pathList); 

        // ------------------------ フック関数

    /**
     * [ 認証関数 ]を設定: リクエストごとに実行
     * @param auth 注記を参照
     * @return オブジェクト自身
     */
    AuthFilter setAuth(AuthorizationStrategy auth);

    /**
     * [ 前処理関数 ]を設定：[ 認証関数 ]の前に毎回実行される。
     *      <b>注意点：前処理認証関数は includeList と excludeList の制限を受けず、すべてのルートのリクエストが beforeAuth に入る</b>
     * @param beforeAuth /
     * @return オブジェクト自身
     */
    AuthFilter setBeforeAuth(AuthorizationStrategy beforeAuth);

    /**
     * [ エラー処理関数 ]を設定：[ 認証関数 ]の後に毎回実行される。
     * @param error /
     * @return オブジェクト自身
     */
    AuthFilter setError(AuthorizationErrorStrategy error);
}
