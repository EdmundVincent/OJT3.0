package com.collaboportal.common.oauth2.repository;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import com.collaboportal.common.oauth2.entity.DTO.UserMasterCollabo;

@Mapper
public interface Oauth2Mapper {
    /**
     * ユーザーマスタを取得する
     * 
     * @param userMail
     * @return
     */
    UserMasterCollabo findUserMasterByEmail(@Param("userMail") String userMail);

}
