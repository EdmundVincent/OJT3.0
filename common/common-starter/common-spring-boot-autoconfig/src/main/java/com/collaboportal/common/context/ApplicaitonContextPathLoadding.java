package com.collaboportal.common.context;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;

import com.collaboportal.common.application.ApplicationInfo;
import com.microsoft.sqlserver.jdbc.StringUtils;

public class ApplicaitonContextPathLoadding implements ApplicationRunner {

    @Value("${server.servlet.context-path:}")
    String contextPath;

    @Value("${spring.mvc.servlet.path:}")
    String servletPath;

    @Override
    public void run(ApplicationArguments args) throws Exception {

        String routePrefix = "";

        if (!StringUtils.isEmpty(contextPath)) {
            if (!contextPath.startsWith("/")) {
                contextPath = "/" + contextPath;
            }
            if (contextPath.endsWith("/")) {
                contextPath = contextPath.substring(0, contextPath.length() - 1);
            }
            routePrefix += contextPath;
        }

        if (!StringUtils.isEmpty(servletPath)) {
            if (!servletPath.startsWith("/")) {
                servletPath = "/" + servletPath;
            }
            if (servletPath.endsWith("/")) {
                servletPath = servletPath.substring(0, servletPath.length() - 1);
            }
            routePrefix += servletPath;
        }


        if (!StringUtils.isEmpty(routePrefix) && !routePrefix.equals("/")) {
            ApplicationInfo.routePrefix = routePrefix;
        }
    }
}
