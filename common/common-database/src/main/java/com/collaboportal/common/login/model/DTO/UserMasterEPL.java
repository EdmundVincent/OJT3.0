package com.collaboportal.common.login.model.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * テーブルの検索結果をマッピングするためのDTOクラスです。
 * LoginMapper.xmlのfindUserByEmailのresultTypeに対応しています。
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMasterEPL implements com.collaboportal.common.jwt.entity.UserMasterEPL {
    private String userId;
    private String password;
    private Byte role;
    private Integer projectId;
    private String userName;
}
