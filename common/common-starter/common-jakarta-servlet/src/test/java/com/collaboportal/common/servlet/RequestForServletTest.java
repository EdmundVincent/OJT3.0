package com.collaboportal.common.servlet;

import static org.junit.jupiter.api.Assertions.*;

import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockHttpServletRequest;

class RequestForServletTest {

    @Test
    void getHeader_shouldReadHeaderThroughZeroCopyBridge() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer demo-token");

        RequestForServlet baseRequest = new RequestForServlet(request);
        assertNotNull(baseRequest.getHeader("Authorization"));
        assertEquals("Bearer demo-token", baseRequest.getHeader("Authorization"));
        assertArrayEquals("Bearer demo-token".getBytes(StandardCharsets.UTF_8),
                baseRequest.getHeaderBytes("Authorization"));
    }

    @Test
    void getBody_shouldReadAndCacheBodyThroughZeroCopyBridge() {
        MockHttpServletRequest request = new MockHttpServletRequest();
        byte[] payload = "{\"username\":\"alice\"}".getBytes(StandardCharsets.UTF_8);
        request.setContent(payload);

        RequestForServlet baseRequest = new RequestForServlet(request);

        assertEquals("{\"username\":\"alice\"}", baseRequest.getBody());
        assertArrayEquals(payload, baseRequest.getBodyBytes());
        assertEquals("{\"username\":\"alice\"}", baseRequest.getBody());
    }
}
