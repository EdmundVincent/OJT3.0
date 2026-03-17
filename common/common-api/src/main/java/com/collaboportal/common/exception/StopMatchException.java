package com.collaboportal.common.exception;

public class StopMatchException extends RuntimeException {

    public StopMatchException() {
        super("マッチングを停止しました。", null, false, false);
    }

    public StopMatchException(String message) {
        super(message, null, false, false);
    }

    @Override
    public synchronized Throwable fillInStackTrace() {
        return this;
    }
}
