package com.collaboportal.common.context.web;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.LinkedHashMap;
import java.util.Map;

public class BaseCookie {
    public static final String HEADER_NAME = "Set-Cookie";

    private String name;
    private String value;
    private int maxAge = -1;
    private String domain;
    private String path;
    private Boolean secure = false;
    private Boolean httpOnly = false;
    private String sameSite;
    private Map<String, String> extraAttrs = new LinkedHashMap<>();

    public BaseCookie() {}
    public BaseCookie(String name, String value) { this.name = name; this.value = value; }

    public String getName() { return name; }
    public BaseCookie setName(String name) { this.name = name; return this; }
    public String getValue() { return value; }
    public BaseCookie setValue(String value) { this.value = value; return this; }
    public int getMaxAge() { return maxAge; }
    public BaseCookie setMaxAge(int maxAge) { this.maxAge = maxAge; return this; }
    public String getDomain() { return domain; }
    public BaseCookie setDomain(String domain) { this.domain = domain; return this; }
    public String getPath() { return path; }
    public BaseCookie setPath(String path) { this.path = path; return this; }
    public Boolean getSecure() { return secure; }
    public BaseCookie setSecure(Boolean secure) { this.secure = secure; return this; }
    public Boolean getHttpOnly() { return httpOnly; }
    public BaseCookie setHttpOnly(Boolean httpOnly) { this.httpOnly = httpOnly; return this; }
    public String getSameSite() { return sameSite; }
    public BaseCookie setSameSite(String sameSite) { this.sameSite = sameSite; return this; }
    public Map<String, String> getExtraAttrs() { return extraAttrs; }
    public BaseCookie setExtraAttrs(Map<String, String> extraAttrs) { this.extraAttrs = extraAttrs; return this; }
    public BaseCookie addExtraAttr(String name, String value) {
        if (extraAttrs == null) { extraAttrs = new LinkedHashMap<>(); }
        this.extraAttrs.put(name, value);
        return this;
    }
    public BaseCookie addExtraAttr(String name) { return this.addExtraAttr(name, null); }
    public BaseCookie removeExtraAttr(String name) { if (extraAttrs != null) { this.extraAttrs.remove(name); } return this; }

    private boolean isEmpty(String s) { return s == null || s.isEmpty(); }
    public void builder() { if (path == null) { path = "/"; } }

    public String toHeaderValue() {
        this.builder();
        if (isEmpty(name)) { throw new RuntimeException("nameは空にできません"); }
        if (value != null && value.contains(";")) { throw new RuntimeException("無効なValue：" + value); }

        StringBuilder sb = new StringBuilder();
        sb.append(name).append("=").append(value);

        if (maxAge >= 0) {
            sb.append("; Max-Age=").append(maxAge);
            String expires;
            if (maxAge == 0) {
                expires = Instant.EPOCH.atOffset(ZoneOffset.UTC).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            } else {
                expires = OffsetDateTime.now().plusSeconds(maxAge).format(DateTimeFormatter.RFC_1123_DATE_TIME);
            }
            sb.append("; Expires=").append(expires);
        }
        if (!isEmpty(domain)) { sb.append("; Domain=").append(domain); }
        if (!isEmpty(path)) { sb.append("; Path=").append(path); }
        if (Boolean.TRUE.equals(secure)) { sb.append("; Secure"); }
        if (Boolean.TRUE.equals(httpOnly)) { sb.append("; HttpOnly"); }
        if (!isEmpty(sameSite)) { sb.append("; SameSite=").append(sameSite); }

        if (extraAttrs != null) {
            extraAttrs.forEach((k, v) -> {
                if (isEmpty(v)) { sb.append("; ").append(k); }
                else { sb.append("; ").append(k).append("=").append(v); }
            });
        }
        return sb.toString();
    }
}
