package com.collaboportal.common.login.service;

import java.util.Optional;

import org.springframework.stereotype.Service;

import com.collaboportal.common.login.mapper.LoginMapper;
import com.collaboportal.common.login.model.DTO.UserMasterEPL;
import com.collaboportal.common.utils.AbstractMasterLoader;

@Service
public class LoginUserMasterService extends AbstractMasterLoader<UserMasterEPL> {

    private final LoginMapper loginMapper;

    public LoginUserMasterService(LoginMapper loginMapper) {
        super(null, null);
        this.loginMapper = loginMapper;
    }

    @Override
    public UserMasterEPL loadByKey(String userId) {

        return Optional.ofNullable(loginMapper.findUserByUserId(userId))
                .orElseGet(() -> UserMasterEPL.builder()
                        .password(null)
                        .userName(null)
                        .role(null)
                        .userId(null)
                        .build());
    }
}
