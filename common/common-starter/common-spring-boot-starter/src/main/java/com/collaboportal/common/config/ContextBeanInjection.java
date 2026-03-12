package com.collaboportal.common.config;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.collaboportal.common.filter.CommonContextBindingFilter;

import jakarta.servlet.DispatcherType;

@Configuration
public class ContextBeanInjection {

    @Bean
    public FilterRegistrationBean<CommonContextBindingFilter> ctxBindingFilter() {
        FilterRegistrationBean<CommonContextBindingFilter> bean = new FilterRegistrationBean<>(
                new CommonContextBindingFilter());
        bean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        bean.addUrlPatterns("/*", "/error");
        bean.setDispatcherTypes(DispatcherType.REQUEST, DispatcherType.FORWARD, DispatcherType.ERROR, DispatcherType.INCLUDE, DispatcherType.ASYNC);

        return bean;
    }

}
