package com.collaboportal.common.spring.common.context;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.collaboportal.common.context.ApplicaitonContextPathLoadding;

@Configuration
public class CommonBeanRegister {

    @Bean
    public ApplicaitonContextPathLoadding getApplicationContextPathLoading() {
        return new ApplicaitonContextPathLoadding();
    }

}
