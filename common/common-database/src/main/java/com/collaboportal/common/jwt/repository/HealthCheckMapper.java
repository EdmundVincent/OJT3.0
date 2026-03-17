package com.collaboportal.common.jwt.repository;

import org.apache.ibatis.annotations.Mapper;

/**
 * ヘルスチェック用マッパーインターフェース
 * データベースの接続状態を確認するための機能を提供する
 */
@Mapper
public interface HealthCheckMapper {
    /**
     * データベースのヘルスチェックを実行する
     * 
     * @param keyWord 検索キーワード
     * @return 検索結果の件数
     */
    Integer getHealthCheck(String keyWord);
}
