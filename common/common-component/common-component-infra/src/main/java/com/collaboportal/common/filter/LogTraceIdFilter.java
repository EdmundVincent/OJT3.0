package com.collaboportal.common.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.MDC;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.IOException;
import java.util.UUID;
import jakarta.servlet.http.Cookie;

public class LogTraceIdFilter implements Filter {
    private static final String TRACE_ID = "X-Track";
    private static final String IP_ADDRESS = "ipAddress";
    private static final String TRACKING_COOKIE_VALUE = "trackingCookieValue";
    Logger logger = LoggerFactory.getLogger(LogTraceIdFilter.class);

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterchain)
            throws IOException, ServletException {
        if (!isHttpServlet(request, response)) {
            filterchain.doFilter(request, response);
            return;
        }
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        HttpServletResponse httpResponse = (HttpServletResponse) response;
        String traceId = determineTraceId(httpRequest);
        String ipAddress = determineIpAddress(httpRequest);
        String trackingCookie = getTrackingCookieValue(httpRequest, httpResponse);
        MDC.put(TRACE_ID, traceId);
        MDC.put(IP_ADDRESS, ipAddress);
        MDC.put(TRACKING_COOKIE_VALUE, trackingCookie);
        try {
            filterchain.doFilter(request, response);
        } finally {
            MDC.clear();
        }
    }

    private boolean isHttpServlet(ServletRequest request, ServletResponse response) {
        return ((request instanceof HttpServletRequest) && (response instanceof HttpServletResponse));
    }

    private String determineTraceId(HttpServletRequest request) {
        String traceId = UUID.randomUUID().toString();
        String headerTraceId = request.getHeader(TRACE_ID);
        if (headerTraceId != null && !headerTraceId.isEmpty()) {
            traceId = headerTraceId;
        }
        return traceId;
    }

    private String determineIpAddress(HttpServletRequest request) {
        String ipAddress = request.getRemoteAddr();
        String xForwardedFor = request.getHeader("X-Forwarded-For");
        if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
            ipAddress = xForwardedFor.split(",")[0];
        }
        return ipAddress;
    }

    private String getTrackingCookieValue(HttpServletRequest request, HttpServletResponse response) {
        Cookie[] cookies = request.getCookies();
        Cookie retcookie = null;
        if (cookies != null) {
            for (Cookie cookie : cookies) {
                if (cookie.getName().equals(TRACKING_COOKIE_VALUE)
                        && cookie.getValue() != null
                        && !cookie.getValue().isEmpty()) {
                    retcookie = cookie;
                }
            }
        }
        if (retcookie == null) {
            retcookie = new Cookie(TRACKING_COOKIE_VALUE, UUID.randomUUID().toString());
        }
        retcookie.setHttpOnly(true);
        retcookie.setMaxAge(60 * 60 * 24 * 30);
        retcookie.setPath("/");
        retcookie.setAttribute("SameSite", "Strict");
        response.addCookie(retcookie);
        return retcookie.getValue();
    }
}
