package com.collaboportal.common.model;

/**
 * メンテナンスフラグを管理するクラス
 */
public class MaintenanceBody {
    // メンテナンスフラグ（デフォルト値: "0"）
    private static String mentFlg= "0";

    /**
     * メンテナンスフラグを取得する
     * @return 現在のメンテナンスフラグ
     */
    public static String getMentFlg() {
        return mentFlg;
    }

    /**
     * メンテナンスフラグを設定する
     * @param Flg 設定するメンテナンスフラグ
     */
    public static void setMentFlg(String Flg) {
        MaintenanceBody.mentFlg = Flg;
    }

}
