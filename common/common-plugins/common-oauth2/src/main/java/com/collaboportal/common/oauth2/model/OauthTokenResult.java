package com.collaboportal.common.oauth2.model;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import java.io.Serializable;

/**
 * OAuthトークン結果クラス
 * OAuth認証の結果情報を保持する
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class OauthTokenResult implements Serializable {

	// アクセストークン
	private String accessToken;
	// 認証成功フラグ
	private boolean isSuccess;
	// ユーザー名
	private String name;
	// ユーザー識別子
	private String sub;
	// ユーザーのメールアドレス
	private String email;
	// ユーザーの名
	private String given_name;
	// ユーザーの姓
	private String family_name;

}
