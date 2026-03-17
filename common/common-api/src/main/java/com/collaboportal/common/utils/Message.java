package com.collaboportal.common.utils;

public class Message {

	public static final String W400 = "パラメータエラー";
	public static final String W401 = "認証情報が不正です";
	public static final String W403 = "アクセスが拒否されました";
	public static final String W404 = "業務エラー";
	public static final String W409 = "DB排他エラー";
	public static final String W500 = "システムエラー";
	public static final String E404 = "指定されたURLは存在しません";
	public static final String ERROR_LEVEL_ERROR = "E";
	public static final String ERROR_LEVEL_WARNING = "W";
	public static final String ERROR_LEVEL_INFO = "I";
	public static final String ERROR_LEVEL_SUCCESS = "S";
	public static final String DUPRICATE_ERROR = "already registered.";
	public static final int OK = 200;
	public static final int REDIRECTION = 302;
	public static final int BAD_REQUEST = 400;
	public static final int UNAUTHORIZED = 401;
	public static final int FORBIDDEN = 403;
	public static final int NOT_FOUND = 404;
	public static final int CONFLICT = 409;
	public static final int SYSTEM_ERROR = 500;
	public static final String SHAIN_KUBUN_OTOKUISAMA = "0";
	public static final String SHAIN_KUBUN_SHAIN = "1";
	public static final String SHAIN_KUBUN_GROUP_SHAIN = "2";
	public static final String LOG_LEVEL_ERROR = "1";
	public static final String LOG_LEVEL_WARNING = "2";
	public static final String LOG_LEVEL_INFO = "3";
	public static final String LOG_LEVEL_DEBUG = "4";

	public static final String VALIDATION_ONLY_HALF_NUMERIC = "^[0-9]+$";
	public static final String VALIDATION_ONLY_NUMERIC = "^[0-9０-９]+$";
	public static final String VALIDATION_ONLY_LOW_ALPHABET = "^[a-z]+$";
	public static final String VALIDATION_ONLY_UP_ALPHABET = "^[A-Z]+$";
	public static final String VALIDATION_ONLY_ALPHABET = "^[a-zA-Z]+$";
	public static final String VALIDATION_ALPHA_NUMERIC = "^[a-zA-Z0-9]+$";
	public static final String VALIDATION_ONLY_HALF_STRING = "^[ -~]+$";
	public static final String VALIDATION_ONLY_DOUBLE_BYTES_STRING = "^[^\\x01-\\x7E\\uFF61-\\uFF9F]+$";
	public static final String VALIDATION_HALF_BYTES_STRING = "^[\\x01-\\x7E\\uFF61-\\uFF9F]+$";
	public static final String USER_VERSION_INFO = "user_version_info";

	/**
	 * クッキー定数クラス
	 */
	public static class Cookie {

		public static final String PARAMETER_NAME_R = "r";
		public static final String PARAMETER_NAME_S = "s";
		public static final String PARAMETER_NAME_H = "h";

		public static final String HONBU_FLAG = "honbu_flag";
		public static final String AUTH_STATE = "AuthState";
		public static final String MOVE_URL = "MoveUrl";
		/** 認証トークン */
		public static final String AUTH = "AuthToken";

		/** 社員区分 */
		public static final String SHN_KBN = "shn_kbn";

		/** コラボポータルID */
		public static final String CLB_ID = "clb_id";

		/** 得意先コード */
		public static final String TOK_COD = "tok_cod";

		/** 得意先コード 所属医療機関確認画面用 */
		public static final String LOGINED_USER_TOK_COD = "logined_user_tok_cod";

		/** 提案準備 */
		public static final String IS_READY_FOR_PROPOSAL = "is_ready_for_proposal";

		/** 月初回ログイン（所属医療機関変更） */
		public static final String SZK_IK_GMN_KBN = "szk_ik_gmn_kbn";

		/** ログインID */
		public static final String ID = "ID";

		public static final String xxx ="xXX";
		/** デバイス登録削除呼び出し */
		public static final String LOGINED = "LOGINED";

		/** リトライ回数（基盤登録失敗） */
		public static final String REGISTER_RETRY = "REGISTER-RETRY";

		/** トレースID */
		public static final String SPNOHIN_TRACE_ID = "spnohin_trace_id";

		/** ホワイトリスト */
		public static final String IS_ON_WHITELIST = "is_on_whitelist";

		/** 得意先名 */
		public static final String TOK_NM = "tok_nm";

		/** 得意先施設名 */
		public static final String TOK_SST_NM = "tok_sst_nm";

		/** 提案曜日 */
		public static final String TEI_DAYOFWEEK = "tei_dayofweek";

		/** コラボポータルAPI用リフレッシュトークン */
		public static final String CLB_REFRESH_TOKEN = "collabo_refresh_token";

		/** コラボポータル仮登録フラグ */
		public static final String IS_CLB_PROVISIONAL_REGISTRATION = "is_collabo_provisional_registration";

		/** プラットフォーム */
		public static final String PLATFORM = "platform";

		/** アプリバージョン */
		public static final String APP_VER = "app_ver";

		/** 初回提案日時 */
		public static final String PROPOSAL_START_DATE = "proposal_start_date";

	}

	public static class ContextInfo {
		/** 客戶端ID */
		public static final String CLIENT_ID = "clientId";

		/** 客戶端密鑰 */
		public static final String CLIENT_SECRET = "clientSecret";

		/** 受眾 */
		public static final String AUDIENCE = "audience";
	}
}
