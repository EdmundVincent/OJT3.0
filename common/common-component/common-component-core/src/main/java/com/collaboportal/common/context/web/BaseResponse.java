package com.collaboportal.common.context.web;

public interface BaseResponse {
    String ACCESS_CONTROL_EXPOSE_HEADERS = "Access-Control-Expose-Headers";
    Object Source();
    default void deleteCookie(String name) { addCookie(name, null, null, null, 0); }
    default void deleteCookie(String name, String path, String domain) { addCookie(name, null, path, domain, 0); }
    default void addCookie(String name, String value, String path, String domain, int timeout) {
        this.addCookie(new BaseCookie(name, value).setPath(path).setDomain(domain).setMaxAge(timeout));
    }
    default void addCookie(BaseCookie cookie) { this.addHeader(BaseCookie.HEADER_NAME, cookie.toHeaderValue()); }
    BaseResponse setStatus(int sc);
    BaseResponse setHeader(String name, String value);
    BaseResponse addHeader(String name, String value);
    default BaseResponse setServer(String value) { return this.setHeader("Server", value); }
    Object redirect(String url);
    void flush();
}
