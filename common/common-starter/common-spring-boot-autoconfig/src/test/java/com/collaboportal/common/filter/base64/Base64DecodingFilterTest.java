package com.collaboportal.common.filter.base64;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;

/**
 * {@link Base64DecodingFilter} の単体テストクラス。
 */
class Base64DecodingFilterTest {

    private static final String ENCODED_HEADER = "X-Encoded-With";
    private static final String ENCODED_HEADER_VALUE = "base64url";

    private Base64DecodingFilter filter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;
    private ObjectMapper objectMapper;

    @BeforeEach
    void setUp() {
        filter = new Base64DecodingFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        objectMapper = new ObjectMapper();
    }

    /**
     * テスト用のBase64URLエンコードを行うヘルパーメソッド。
     * @param value エンコードする文字列
     * @return エンコードされた文字列
     */
    private String encodeBase64Url(String value) {
        return Base64.getUrlEncoder().withoutPadding().encodeToString(value.getBytes(StandardCharsets.UTF_8));
    }

    private void markRequestAsEncoded() {
        request.addHeader(ENCODED_HEADER, ENCODED_HEADER_VALUE);
    }

    /**
     * Base64URLでエンコードされたパラメータが正しくデコードされることをテストします。
     */
    @Test
    void shouldDecodeBase64UrlParameters() throws ServletException, IOException {
        // 準備
        String originalValue = "test@example.com";
        String encodedValue = encodeBase64Url(originalValue);
        request.addParameter("requestparameter", encodedValue);
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                assertEquals(originalValue, wrappedRequest.getParameter("requestparameter"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * Base64URL形式ではない通常の文字列パラメータが、デコードされずにそのまま渡されることをテストします。
     */
    @Test
    void shouldNotDecodeNonBase64Parameters() throws ServletException, IOException {
        // 準備
        String plainValue = "this_is_not_base64";
        request.addParameter("token", plainValue);

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                assertEquals(plainValue, wrappedRequest.getParameter("token"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * エンコードされたパラメータと、されていないパラメータが混在しているリクエストを正しく処理できることをテストします。
     */
    @Test
    void shouldHandleMixedParameters() throws ServletException, IOException {
        // 準備
        String originalValue = "user/123";
        String encodedValue = encodeBase64Url(originalValue);
        String plainValue = "search_term";
        request.addParameter("requestparameter", encodedValue);
        request.addParameter("query", plainValue);
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                assertEquals(originalValue, wrappedRequest.getParameter("requestparameter"));
                assertEquals(plainValue, wrappedRequest.getParameter("query"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * 1つのパラメータ名に対して複数の値が設定されている場合に、すべての値が正しくデコードされることをテストします。
     */
    @Test
    void shouldHandleMultipleValuesForOneParameter() throws ServletException, IOException {
        // 準備
        String originalValue1 = "value1";
        String encodedValue1 = encodeBase64Url(originalValue1);
        String originalValue2 = "value2";
        String encodedValue2 = encodeBase64Url(originalValue2);
        request.addParameter("requestparameter", encodedValue1, encodedValue2);
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                String[] expectedValues = { originalValue1, originalValue2 };
                assertArrayEquals(expectedValues, wrappedRequest.getParameterValues("requestparameter"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * パラメータ名に関係なくBase64文字列がデコードされることを確認します。
     */
    @Test
    void shouldDecodeBase64ParameterRegardlessOfName() throws ServletException, IOException {
        String originalValue = "hidden";
        String encodedValue = encodeBase64Url(originalValue);
        request.addParameter("email", encodedValue);
        markRequestAsEncoded();

        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                assertEquals(originalValue, wrappedRequest.getParameter("email"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * リクエストにパラメータが全く含まれていない場合でも、エラーなく正常に処理が完了することをテストします。
     */
    @Test
    void shouldHandleRequestWithNoParameters() throws ServletException, IOException {
        // 準備 (パラメータは追加しない)

        // 実行
        final boolean[] filterChainCalled = {false};
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                // 検証
                filterChainCalled[0] = true;
                assertNotNull(req);
                assertEquals(0, req.getParameterMap().size());
            }
        };
        filter.doFilterInternal(request, response, filterChain);
        
        // フィルターチェーンが呼び出されたことを確認
        assertTrue(filterChainCalled[0]);
    }

    /**
     * application/json 形式のリクエストボディ内のBase64文字列がデコードされることをテストします。
     */
    @Test
    void shouldDecodeBase64InJsonBody() throws ServletException, IOException {
        // 準備
        String originalValue = "test-body-value";
        String encodedValue = encodeBase64Url(originalValue);
        String jsonBody = String.format("{\"requestparameter\":\"%s\"}", encodedValue);
        
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) throws IOException {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                String resultBody = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JsonNode resultNode = objectMapper.readTree(resultBody);
                assertEquals(originalValue, resultNode.get("requestparameter").asText());
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * ネストしたJSONオブジェクト内のBase64文字列がデコードされることをテストします。
     */
    @Test
    void shouldDecodeBase64InNestedJsonBody() throws ServletException, IOException {
        // 準備
        String originalValue = "nested-value";
        String encodedValue = encodeBase64Url(originalValue);
        String jsonBody = String.format("{\"requestparameter\":{\"level2\":\"%s\"}}", encodedValue);
        
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) throws IOException {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                String resultBody = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JsonNode resultNode = objectMapper.readTree(resultBody);
                assertEquals(originalValue, resultNode.get("requestparameter").get("level2").asText());
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * JSONボディとURLパラメータの両方が同時にデコードされることをテストします。
     */
    @Test
    void shouldDecodeBothJsonBodyAndUrlParameters() throws ServletException, IOException {
        // 準備
        // URLパラメータ
        String paramOriginal = "param-value";
        String paramEncoded = encodeBase64Url(paramOriginal);
        request.addParameter("requestparameter", paramEncoded);

        // JSONボディ
        String bodyOriginal = "body-value";
        String bodyEncoded = encodeBase64Url(bodyOriginal);
        String jsonBody = String.format("{\"requestparameter\":\"%s\"}", bodyEncoded);
        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) throws IOException {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                // URLパラメータの検証
                assertEquals(paramOriginal, wrappedRequest.getParameter("requestparameter"));
                // ボディの検証
                String resultBody = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JsonNode resultNode = objectMapper.readTree(resultBody);
                assertEquals(bodyOriginal, resultNode.get("requestparameter").asText());
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * JSONボディのフィールド名に依存せずBase64文字列がデコードされることを確認します。
     */
    @Test
    void shouldDecodeJsonFieldRegardlessOfName() throws ServletException, IOException {
        String originalValue = "note-content";
        String encodedValue = encodeBase64Url(originalValue);
        String jsonBody = String.format("{\"note\":\"%s\"}", encodedValue);

        request.setContentType("application/json");
        request.setContent(jsonBody.getBytes(StandardCharsets.UTF_8));
        markRequestAsEncoded();

        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) throws IOException {
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                String resultBody = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                JsonNode resultNode = objectMapper.readTree(resultBody);
                assertEquals(originalValue, resultNode.get("note").asText());
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * Content-TypeがJSONでない場合、リクエストボディが変更されないことをテストします。
     */
    @Test
    void shouldNotDecodeBodyIfNotJson() throws ServletException, IOException {
        // 準備
        String originalBody = "this is plain text";
        request.setContentType("text/plain");
        request.setContent(originalBody.getBytes(StandardCharsets.UTF_8));
        markRequestAsEncoded();

        // 実行
        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) throws IOException {
                // 検証
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                String resultBody = new String(wrappedRequest.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
                assertEquals(originalBody, resultBody);
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }

    /**
     * Base64ヘッダーが付与されていない場合は、デコード処理をスキップすることを確認します。
     */
    @Test
    void shouldSkipDecodingWithoutHeader() throws ServletException, IOException {
        String valueThatLooksEncoded = "MTIzNDU2";
        request.addParameter("requestparameter", valueThatLooksEncoded);

        filterChain = new MockFilterChain() {
            @Override
            public void doFilter(jakarta.servlet.ServletRequest req, jakarta.servlet.ServletResponse res) {
                HttpServletRequest wrappedRequest = (HttpServletRequest) req;
                assertEquals(valueThatLooksEncoded, wrappedRequest.getParameter("requestparameter"));
            }
        };
        filter.doFilterInternal(request, response, filterChain);
    }
}
