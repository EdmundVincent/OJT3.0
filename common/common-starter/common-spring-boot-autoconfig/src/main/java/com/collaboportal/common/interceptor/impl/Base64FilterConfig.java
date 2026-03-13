package com.collaboportal.common.interceptor.impl;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;

import com.collaboportal.common.filter.base64.Base64DecodingFilter;

/**
 * Base64DecodingFilterを登録するための設定。
 */
@Configuration
public class Base64FilterConfig {

    /**
     * Base64DecodingFilterを登録し、その順序を最高の優先順位に設定します。
     * @return Base64DecodingFilterのFilterRegistrationBean
     */
    @Bean
    public FilterRegistrationBean<Base64DecodingFilter> base64DecodingFilter() {
        FilterRegistrationBean<Base64DecodingFilter> registrationBean = new FilterRegistrationBean<>();
        
        registrationBean.setFilter(new Base64DecodingFilter());
        // このフィルターをすべてのURLパターンに適用する
        registrationBean.addUrlPatterns("/*");
        // 他のフィルターやインターセプターより先に実行されるように、最高の優先順位を設定する
        registrationBean.setOrder(Ordered.HIGHEST_PRECEDENCE);
        
        return registrationBean;
    }
}