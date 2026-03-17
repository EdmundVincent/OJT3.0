package com.collaboportal.common.login.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.collaboportal.common.login.model.DTO.UserMasterEPL;

/**
 * ログイン関連のMyBatis Mapperインターフェース
 * データベースからユーザー情報を取得するための抽象メソッドを定義します。
 */
@Mapper
public interface LoginMapper {

    /**
     * ユーザー名でユーザー情報を検索する
     *
     * @param username ユーザー名
     * @return 一致するユーザー情報。存在しない場合はnullを返します。
     */
    UserMasterEPL findUserByUserId(@Param("userId") String userId);

}