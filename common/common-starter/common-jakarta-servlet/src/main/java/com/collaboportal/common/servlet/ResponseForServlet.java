package com.collaboportal.common.servlet;

import com.collaboportal.common.context.web.BaseResponse;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.CommonException;

import jakarta.servlet.http.HttpServletResponse;

public class ResponseForServlet implements BaseResponse {

    protected HttpServletResponse response;

    public ResponseForServlet(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public Object Source() {
        return response;
    }

    @Override
    public BaseResponse setStatus(int sc) {
        response.setStatus(sc);
        return this;
    }

    @Override
    public BaseResponse setHeader(String name, String value) {
        response.setHeader(name, value);
        return this;
    }

    @Override
    public BaseResponse addHeader(String name, String value) {
        response.addHeader(name, value);
        return this;
    }

    @Override
    public Object redirect(String url) {
        try {
            response.setStatus(302);
            response.setHeader("Location", url);
        } catch (Exception e) {
            throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
        }
        return null;
    }

    @Override
    public void flush() {
        try {
            response.flushBuffer();
        } catch (Exception e) {
            throw new CommonException(InternalErrorCode.SYSTEM_ERROR);
        }
    }

}
