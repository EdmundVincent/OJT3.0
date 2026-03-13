package com.collaboportal.common.spring.common;

import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.strategy.CorsHandleFunction;
import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;

/**
 * CORS設定の自動構成。
 */
@AutoConfiguration
public class CorsConfiguration {

    private static final String DEFAULT_ALLOWED_METHODS = "GET,POST,PUT,DELETE,OPTIONS,PATCH";
    private static final String DEFAULT_ALLOWED_HEADERS = "Authorization,Content-Type,X-Requested-With";
    private static final String EXPOSE_HEADERS = "Authorization,Content-Disposition,X-Total-Count";

    @Bean
    @ConditionalOnMissingBean(CorsHandleFunction.class)
    public CorsHandleFunction corsHandleFunction() {
        return (request, response) -> {
            String origin = request.getHeader("Origin");
            if (origin == null || origin.isBlank()) {
                origin = "*";
            }

            String requestHeaders = request.getHeader("Access-Control-Request-Headers");
            if (requestHeaders == null || requestHeaders.isBlank()) {
                requestHeaders = DEFAULT_ALLOWED_HEADERS;
            }

            response.setHeader("Access-Control-Allow-Origin", origin)
                    .setHeader("Vary", "Origin")
                    .setHeader("Access-Control-Allow-Methods", DEFAULT_ALLOWED_METHODS)
                    .setHeader("Access-Control-Allow-Headers", requestHeaders)
                    .setHeader("Access-Control-Allow-Credentials", "true")
                    .setHeader("Access-Control-Max-Age", "3600")
                    .setHeader(BaseResponse.ACCESS_CONTROL_EXPOSE_HEADERS, EXPOSE_HEADERS);
        };
    }
}
