package com.collaboportal.common.jwt.entity;

public interface UserMasterCollabo extends UserMaster {

    String getUserName();

    String getProjectId();

    String getUserMail();

    String getJobCode();

    String getCompanyCode();

    String getDepartmentCode();

    String getBranchCode();

    String getSectionCode();

}
