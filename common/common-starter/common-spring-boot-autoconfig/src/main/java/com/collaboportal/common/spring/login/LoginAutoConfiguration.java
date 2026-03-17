package com.collaboportal.common.spring.login;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@AutoConfiguration
@ComponentScan(basePackages = {
        "com.collaboportal.common.login"
})
public class LoginAutoConfiguration {
}
