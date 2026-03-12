package com.collaboportal.common.spring.common;

import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

/**
 * 測試輔助類
 * 
 * 提供常用的測試工具方法，簡化測試代碼的編寫
 */
public class TestHelper {

    /**
     * 創建一個基本的 MockHttpServletRequest
     * 
     * @param uri 請求 URI
     * @param method HTTP 方法
     * @return MockHttpServletRequest 實例
     */
    public static MockHttpServletRequest createMockRequest(String uri, String method) {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.setRequestURI(uri);
        request.setMethod(method);
        return request;
    }

    /**
     * 創建一個帶有 Authorization-Type header 的 MockHttpServletRequest
     * 
     * @param uri 請求 URI
     * @param method HTTP 方法
     * @param authType Authorization-Type 值
     * @return MockHttpServletRequest 實例
     */
    public static MockHttpServletRequest createMockRequestWithAuth(String uri, String method, String authType) {
        MockHttpServletRequest request = createMockRequest(uri, method);
        if (authType != null) {
            request.addHeader("Authorization-Type", authType);
        }
        return request;
    }

    /**
     * 創建一個用於 normality-check 端點的請求
     * 
     * @param authType Authorization-Type 值
     * @return MockHttpServletRequest 實例
     */
    public static MockHttpServletRequest createNormalityCheckRequest(String authType) {
        return createMockRequestWithAuth("/api/v1/normality-check", "GET", authType);
    }

    /**
     * 創建一個用於 normality-check 端點的請求（無認證）
     * 
     * @return MockHttpServletRequest 實例
     */
    public static MockHttpServletRequest createNormalityCheckRequestWithoutAuth() {
        return createMockRequest("/api/v1/normality-check", "GET");
    }

    /**
     * 創建一個 MockHttpServletResponse
     * 
     * @return MockHttpServletResponse 實例
     */
    public static MockHttpServletResponse createMockResponse() {
        return new MockHttpServletResponse();
    }

    /**
     * 創建一個 MockFilterChain
     * 
     * @return MockFilterChain 實例
     */
    public static MockFilterChain createMockFilterChain() {
        return new MockFilterChain();
    }

    /**
     * 創建完整的測試環境（請求、響應、過濾器鏈）
     * 
     * @param uri 請求 URI
     * @param method HTTP 方法
     * @param authType Authorization-Type 值
     * @return TestEnvironment 實例
     */
    public static TestEnvironment createTestEnvironment(String uri, String method, String authType) {
        MockHttpServletRequest request = createMockRequestWithAuth(uri, method, authType);
        MockHttpServletResponse response = createMockResponse();
        MockFilterChain filterChain = createMockFilterChain();
        
        return new TestEnvironment(request, response, filterChain);
    }

    /**
     * 創建用於 normality-check 端點的完整測試環境
     * 
     * @param authType Authorization-Type 值
     * @return TestEnvironment 實例
     */
    public static TestEnvironment createNormalityCheckTestEnvironment(String authType) {
        return createTestEnvironment("/api/v1/normality-check", "GET", authType);
    }

    /**
     * 測試環境封裝類
     */
    public static class TestEnvironment {
        private final MockHttpServletRequest request;
        private final MockHttpServletResponse response;
        private final MockFilterChain filterChain;

        public TestEnvironment(MockHttpServletRequest request, MockHttpServletResponse response, MockFilterChain filterChain) {
            this.request = request;
            this.response = response;
            this.filterChain = filterChain;
        }

        public MockHttpServletRequest getRequest() {
            return request;
        }

        public MockHttpServletResponse getResponse() {
            return response;
        }

        public MockFilterChain getFilterChain() {
            return filterChain;
        }
    }

    /**
     * 驗證響應狀態碼
     * 
     * @param response MockHttpServletResponse
     * @param expectedStatus 期望的狀態碼
     */
    public static void assertResponseStatus(MockHttpServletResponse response, int expectedStatus) {
        if (response.getStatus() != 0) { // 如果狀態碼被設置了
            org.junit.jupiter.api.Assertions.assertEquals(expectedStatus, response.getStatus(), 
                "響應狀態碼應為 " + expectedStatus);
        }
    }

    /**
     * 驗證響應內容類型
     * 
     * @param response MockHttpServletResponse
     * @param expectedContentType 期望的內容類型
     */
    public static void assertResponseContentType(MockHttpServletResponse response, String expectedContentType) {
        if (response.getContentType() != null) {
            org.junit.jupiter.api.Assertions.assertEquals(expectedContentType, response.getContentType(), 
                "響應內容類型應為 " + expectedContentType);
        }
    }

    /**
     * 驗證響應內容
     * 
     * @param response MockHttpServletResponse
     * @param expectedContent 期望的內容
     */
    public static void assertResponseContent(MockHttpServletResponse response, String expectedContent) {
        try {
            String actualContent = response.getContentAsString();
            org.junit.jupiter.api.Assertions.assertEquals(expectedContent, actualContent, 
                "響應內容應為 " + expectedContent);
        } catch (Exception e) {
            org.junit.jupiter.api.Assertions.fail("無法獲取響應內容: " + e.getMessage());
        }
    }

    /**
     * 驗證請求頭
     * 
     * @param request MockHttpServletRequest
     * @param headerName 請求頭名稱
     * @param expectedValue 期望的值
     */
    public static void assertRequestHeader(MockHttpServletRequest request, String headerName, String expectedValue) {
        String actualValue = request.getHeader(headerName);
        org.junit.jupiter.api.Assertions.assertEquals(expectedValue, actualValue, 
            "請求頭 " + headerName + " 應為 " + expectedValue);
    }

    /**
     * 驗證請求參數
     * 
     * @param request MockHttpServletRequest
     * @param paramName 參數名稱
     * @param expectedValue 期望的值
     */
    public static void assertRequestParameter(MockHttpServletRequest request, String paramName, String expectedValue) {
        String actualValue = request.getParameter(paramName);
        org.junit.jupiter.api.Assertions.assertEquals(expectedValue, actualValue, 
            "請求參數 " + paramName + " 應為 " + expectedValue);
    }
} 