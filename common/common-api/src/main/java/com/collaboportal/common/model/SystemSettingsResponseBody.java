package com.collaboportal.common.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class SystemSettingsResponseBody extends BaseResponseBody {

    /** パスワード変更URL */
    private String pass_change_url;

    /** ログアウトURL */
    private String logout_url;
}
