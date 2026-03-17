package com.collaboportal.common.login.service;

import com.collaboportal.common.login.model.LoginRequest;
import com.collaboportal.common.login.model.LoginResult;

/**
 * ログインサービスのインターフェース定義
 * 核心のログイン認証ロジックを封装します。
 */
public interface LoginService {

    /**
     * ユーザーログイン認証
     *
     * @param loginRequest ユーザー認証情報（メールアドレスとパスワードなど）を含むログインリクエストオブジェクト
     * @return 認証トークンを含むログインレスポンスオブジェクト
     */
    LoginResult login(LoginRequest loginRequest);

}
