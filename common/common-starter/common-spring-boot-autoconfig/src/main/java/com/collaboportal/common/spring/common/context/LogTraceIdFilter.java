package com.collaboportal.common.spring.common.context;

import org.springframework.boot.web.servlet.FilterRegistrationBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * ログにtraceIDとIPアドレスとトラッキングクッキーの値を設定し、トラッキングクッキーを設定するフィルター
 * traceIDはリクエストごとに生成する値であり、ログに記録する。
 * IPアドレスはリクエストのIPアドレスである。
 * トラッキングクッキーはクライアントの追跡を補助するためのクッキーである。IPアドレスだけでは追跡できない場合があるため追加。
 * traceIDとトラッキングクッキーは認証に使用せず、流出しても問題が無い範囲で利用すること
 */
@Configuration
public class LogTraceIdFilter {
    Logger logger = LoggerFactory.getLogger(LogTraceIdFilter.class);

    @Bean
    FilterRegistrationBean<com.collaboportal.common.filter.LogTraceIdFilter> loggingTraceIdFilter() {
        FilterRegistrationBean<com.collaboportal.common.filter.LogTraceIdFilter> registrationBean = new FilterRegistrationBean<>();
        registrationBean.setFilter(new com.collaboportal.common.filter.LogTraceIdFilter());
        registrationBean.addUrlPatterns("/*");
        registrationBean.setOrder(Integer.MIN_VALUE);
        return registrationBean;
    }

}
