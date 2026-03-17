package com.collaboportal.common.security.core.model;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import com.collaboportal.common.context.web.BaseRequest;
import com.collaboportal.common.context.web.BaseResponse;

public class AuthRequest {

    private final String type;
    private final String action;
    private final BaseRequest request;
    private final BaseResponse response;
    private final Map<String, Object> attributes;

    public AuthRequest(String type, String action, BaseRequest request, BaseResponse response,
            Map<String, Object> attributes) {
        this.type = type;
        this.action = action;
        this.request = request;
        this.response = response;
        this.attributes = attributes == null ? new HashMap<>() : new HashMap<>(attributes);
    }

    public String getType() {
        return type;
    }

    public String getAction() {
        return action;
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

    public Object getAttribute(String key) {
        return attributes.get(key);
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private String type;
        private String action;
        private BaseRequest request;
        private BaseResponse response;
        private Map<String, Object> attributes = new HashMap<>();

        public Builder type(String type) {
            this.type = type;
            return this;
        }

        public Builder action(String action) {
            this.action = action;
            return this;
        }

        public Builder request(BaseRequest request) {
            this.request = request;
            return this;
        }

        public Builder response(BaseResponse response) {
            this.response = response;
            return this;
        }

        public Builder attribute(String key, Object value) {
            this.attributes.put(key, value);
            return this;
        }

        public Builder attributes(Map<String, Object> attributes) {
            if (attributes != null) {
                this.attributes.putAll(attributes);
            }
            return this;
        }

        public AuthRequest build() {
            return new AuthRequest(type, action, request, response, attributes);
        }
    }
}
