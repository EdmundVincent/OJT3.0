package com.collaboportal.common.jwt.entity;

/**
 * ユーザー共通情報の最小インタフェース
 */
public interface UserMaster {

    String getUserId();

    Byte getRole();
}
