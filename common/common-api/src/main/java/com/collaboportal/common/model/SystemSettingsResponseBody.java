package com.collaboportal.common.model;

public class SystemSettingsResponseBody extends BaseResponseBody {

    /** パスワード変更URL */
    private String pass_change_url;

    /** ログアウトURL */
    private String logout_url;

    public SystemSettingsResponseBody() {
        super();
    }

    public SystemSettingsResponseBody(String pass_change_url, String logout_url) {
        super();
        this.pass_change_url = pass_change_url;
        this.logout_url = logout_url;
    }

    public String getPass_change_url() {
        return pass_change_url;
    }

    public String getLogout_url() {
        return logout_url;
    }
}
