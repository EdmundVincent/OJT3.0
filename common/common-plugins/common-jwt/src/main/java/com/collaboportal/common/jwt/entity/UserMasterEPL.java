package com.collaboportal.common.jwt.entity;

public interface UserMasterEPL extends UserMaster {

    Integer getProjectId();

    String getPassword();

    String getUserName();

}
