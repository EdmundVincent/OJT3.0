package com.collaboportal.common.config;

import java.io.Serializable;

/**
 * 全ての設定クラスの基底インターフェース
 * 設定オブジェクトの共通機能を提供する
 */
public interface BaseConfig extends Serializable {
    /**
     * この設定クラスのプレフィックスを取得する
     * このプレフィックスはプロパティファイルから設定を読み込む際に使用される
     * @return 設定プレフィックス
     */
    String getConfigPrefix();
} 