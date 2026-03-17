package com.collaboportal.common.filter.base64;

import java.io.IOException;

import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Base64エンコードされたパラメータをデコードするために、リクエストをBase64DecodedRequestWrapperでラップするサーブレットフィルタ。
 */
public class Base64DecodingFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        
        // リクエストをカスタムラッパーでラップする
        Base64DecodedRequestWrapper wrappedRequest = new Base64DecodedRequestWrapper(request);
        
        // ラップされたリクエストでフィルターチェーンを続行する
        filterChain.doFilter(wrappedRequest, response);
    }
}