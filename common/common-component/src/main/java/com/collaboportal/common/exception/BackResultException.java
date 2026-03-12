package com.collaboportal.common.exception;

public class BackResultException extends RuntimeException {

    private Object result;

    public BackResultException(Object result) {
        super();
        this.result = result;
    }
}
