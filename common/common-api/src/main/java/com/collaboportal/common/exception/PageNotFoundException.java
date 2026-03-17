package com.collaboportal.common.exception;

public class PageNotFoundException extends RuntimeException {
    public PageNotFoundException() {
        super("ページが見つかりません", null, false, false);
    }

    public PageNotFoundException(String message) {
        super(message, null, false, false);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
