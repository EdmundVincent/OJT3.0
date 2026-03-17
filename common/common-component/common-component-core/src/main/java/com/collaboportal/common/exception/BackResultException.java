package com.collaboportal.common.exception;

public class BackResultException extends RuntimeException {
    private final Object result;
    public BackResultException(Object result) {
        super();
        this.result = result;
    }
    public Object getResult() { return result; }
}
