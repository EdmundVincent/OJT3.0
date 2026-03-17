package com.collaboportal.common.security.core.context;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

public class AuthContext {

    private final BaseRequest request;
    private final BaseResponse response;
    private final Map<String, Object> attributes = new HashMap<>();

    public AuthContext(BaseRequest request, BaseResponse response) {
        this.request = request;
        this.response = response;
    }

    public BaseRequest getRequest() {
        return request;
    }

    public BaseResponse getResponse() {
        return response;
    }

    public Map<String, Object> getAttributes() {
        return Collections.unmodifiableMap(attributes);
    }

    public void putAttribute(String key, Object value) {
        attributes.put(key, value);
    }

    public Object getAttribute(String key) {
        return attributes.get(key);
    }
}
