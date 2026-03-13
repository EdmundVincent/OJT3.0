package com.collaboportal.common.filter;

import org.springframework.web.filter.OncePerRequestFilter;

import com.collaboportal.common.ConfigManager;
import com.collaboportal.common.spring.ContextForSpringInJakartaServlet;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;

public class CommonContextBindingFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(CommonContextBindingFilter.class);

    @Override
    protected void doFilterInternal(HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain)
            throws ServletException, IOException {
        String uri = request.getRequestURI();
        String dt  = request.getDispatcherType().name();
        long tid   = Thread.currentThread().getId();
        if (uri.startsWith("/.well-known/")) {
            filterChain.doFilter(request, response);
            return;
        }

        logger.debug("[CTX-SET] tid={} dt={} uri={}", tid, dt, uri); 
        ConfigManager.setCommonContext(new ContextForSpringInJakartaServlet());
        try {
            logger.info("[CTX-CLEAR] tid={} dt={} uri={}", tid, dt, uri); 
            filterChain.doFilter(request, response);
        } finally {
            ConfigManager.clearContext();
        }
    }

    @Override
    protected boolean shouldNotFilterAsyncDispatch() {
        return false; // 非同期ディスパッチ時もフィルターを実行する
    }
}