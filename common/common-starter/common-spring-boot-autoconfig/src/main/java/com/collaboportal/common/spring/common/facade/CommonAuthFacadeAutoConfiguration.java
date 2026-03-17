package com.collaboportal.common.spring.common.facade;

import com.collaboportal.api.facade.CommonAuthFacade;
import com.collaboportal.common.jwt.service.JwtService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnClass(name = "com.collaboportal.common.jwt.service.JwtService")
public class CommonAuthFacadeAutoConfiguration {

    @Bean
    public CommonAuthFacade commonAuthFacade(CommonAuthFacadeImpl impl) {
        return impl;
    }
}
