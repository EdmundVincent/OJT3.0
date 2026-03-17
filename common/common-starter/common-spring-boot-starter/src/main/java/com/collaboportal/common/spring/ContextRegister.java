package com.collaboportal.common.spring;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.context.annotation.Bean;

import com.collaboportal.common.context.CommonContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


@AutoConfiguration
public class ContextRegister {

    private static final Logger logger = LoggerFactory.getLogger(ContextRegister.class);

    @Bean
	public CommonContext getCommonContextForSpringInJakartaServlet() {
        logger.info("CommonContextForSpringInJakartaServletを取得します");
		return new ContextForSpringInJakartaServlet();
	}
}
