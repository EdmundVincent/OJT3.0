package com.collaboportal.common.oauth2.entity.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserMasterCollabo implements com.collaboportal.common.jwt.entity.UserMasterCollabo {

    private String userId;
    private Byte role;
    private String userName;
    private String userMail;
    private String projectId;
    private String jobCode;
    private String companyCode;
    private String departmentCode;
    private String branchCode;
    private String sectionCode;

}
