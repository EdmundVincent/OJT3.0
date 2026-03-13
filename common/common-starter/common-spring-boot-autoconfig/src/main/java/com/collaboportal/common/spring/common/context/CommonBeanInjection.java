package com.collaboportal.common.spring.common.context;

import org.springframework.beans.factory.annotation.Autowired;

import com.collaboportal.common.ConfigManager;

import com.collaboportal.common.context.CommonContext;

public class CommonBeanInjection {

    @Autowired(required = false)
    public void setCommonContext(CommonContext commonContext) {
        ConfigManager.setCommonContext(commonContext);
    }
}
