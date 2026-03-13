package com.collaboportal.common.filter.base64;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

import com.collaboportal.common.utils.Base64Utils;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.node.TextNode;

import jakarta.servlet.ReadListener;
import jakarta.servlet.ServletInputStream;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletRequestWrapper;

/**
 * Base64エンコードされたパラメータとリクエストボディをデコードするためのHttpServletRequestラッパー。
 */
public class Base64DecodedRequestWrapper extends HttpServletRequestWrapper {

    private static final String HEADER_ENCODED_WITH = "X-Encoded-With";
    private static final String ENCODING_SCHEME_BASE64URL = "base64url";
    private static final String JSON_CONTENT_TYPE = "application/json";

    private final Map<String, String[]> decodedParams;
    private byte[] decodedBody;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Base64DecodedRequestWrapper(HttpServletRequest request) {
        super(request);
        boolean shouldDecode = isBase64EncodedRequest(request);
        this.decodedParams = shouldDecode ? decodeParameters(request) : copyParameters(request);
        if (shouldDecode) {
            try {
                decodeBodyIfNecessary(request);
            } catch (IOException e) {
                throw new RuntimeException("Failed to read or decode request body", e);
            }
        }
    }

    private Map<String, String[]> copyParameters(HttpServletRequest request) {
        Map<String, String[]> copied = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values == null) {
                copied.put(key, null);
                return;
            }
            copied.put(key, Arrays.copyOf(values, values.length));
        });
        return Collections.unmodifiableMap(copied);
    }

    private Map<String, String[]> decodeParameters(HttpServletRequest request) {
        Map<String, String[]> decoded = new HashMap<>();
        request.getParameterMap().forEach((key, values) -> {
            if (values == null) {
                decoded.put(key, null);
                return;
            }
            String[] decodedValues = Arrays.stream(values)
                    .map(Base64Utils::decode)
                    .toArray(String[]::new);
            decoded.put(key, decodedValues);
        });
        return Collections.unmodifiableMap(decoded);
    }

    private void decodeBodyIfNecessary(HttpServletRequest request) throws IOException {
        String contentType = request.getContentType();
        if (contentType == null || !isJsonContentType(contentType)) {
            return;
        }
        String body = new String(request.getInputStream().readAllBytes(), StandardCharsets.UTF_8);
        if (body.isBlank()) {
            this.decodedBody = new byte[0];
            return;
        }
        JsonNode rootNode = objectMapper.readTree(body);
        JsonNode decodedNode = decodeJsonNode(rootNode);
        this.decodedBody = objectMapper.writeValueAsBytes(decodedNode);
    }

    private boolean isJsonContentType(String contentType) {
        return contentType.toLowerCase(Locale.ROOT).contains(JSON_CONTENT_TYPE);
    }

    private boolean isBase64EncodedRequest(HttpServletRequest request) {
        String headerValue = request.getHeader(HEADER_ENCODED_WITH);
        if (headerValue == null) {
            return false;
        }
        return ENCODING_SCHEME_BASE64URL.equalsIgnoreCase(headerValue.trim());
    }

    private JsonNode decodeJsonNode(JsonNode node) {
        if (node == null) {
            return null;
        }
        if (node.isObject()) {
            ObjectNode objectNode = (ObjectNode) node;
            Iterator<Map.Entry<String, JsonNode>> fields = objectNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode decodedChild = decodeJsonNode(entry.getValue());
                if (decodedChild != null) {
                    objectNode.set(entry.getKey(), decodedChild);
                }
            }
            return objectNode;
        }
        if (node.isArray()) {
            ArrayNode arrayNode = (ArrayNode) node;
            for (int i = 0; i < arrayNode.size(); i++) {
                JsonNode decodedElement = decodeJsonNode(arrayNode.get(i));
                if (decodedElement != null) {
                    arrayNode.set(i, decodedElement);
                }
            }
            return arrayNode;
        }
        if (node.isTextual()) {
            String decodedText = Base64Utils.decode(node.asText());
            if (decodedText == null || decodedText.equals(node.asText())) {
                return node;
            }
            return new TextNode(decodedText);
        }
        return node;
    }

    @Override
    public ServletInputStream getInputStream() throws IOException {
        if (decodedBody == null) {
            return super.getInputStream();
        }
        final ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(decodedBody);
        return new ServletInputStream() {
            @Override
            public boolean isFinished() {
                return byteArrayInputStream.available() == 0;
            }

            @Override
            public boolean isReady() {
                return true;
            }

            @Override
            public void setReadListener(ReadListener readListener) {
            }

            @Override
            public int read() throws IOException {
                return byteArrayInputStream.read();
            }
        };
    }

    @Override
    public BufferedReader getReader() throws IOException {
        return new BufferedReader(new InputStreamReader(this.getInputStream(), StandardCharsets.UTF_8));
    }

    @Override
    public int getContentLength() {
        if (decodedBody != null) {
            return decodedBody.length;
        }
        return super.getContentLength();
    }

    @Override
    public long getContentLengthLong() {
        if (decodedBody != null) {
            return decodedBody.length;
        }
        return super.getContentLengthLong();
    }

    @Override
    public String getParameter(String name) {
        String[] values = getParameterValues(name);
        if (values != null && values.length > 0) {
            return values[0];
        }
        return null;
    }

    @Override
    public Map<String, String[]> getParameterMap() {
        return this.decodedParams;
    }

    @Override
    public Enumeration<String> getParameterNames() {
        return Collections.enumeration(this.decodedParams.keySet());
    }

    @Override
    public String[] getParameterValues(String name) {
        return this.decodedParams.get(name);
    }
}
