package com.collaboportal.common.security.core.model;

public class AuthResult {

    public enum Status {
        SUCCESS,
        FAILURE,
        SKIPPED
    }

    private final Status status;
    private final Object payload;
    private final Throwable error;

    private AuthResult(Status status, Object payload, Throwable error) {
        this.status = status;
        this.payload = payload;
        this.error = error;
    }

    public static AuthResult success() {
        return new AuthResult(Status.SUCCESS, null, null);
    }

    public static AuthResult success(Object payload) {
        return new AuthResult(Status.SUCCESS, payload, null);
    }

    public static AuthResult failure(Throwable error) {
        return new AuthResult(Status.FAILURE, null, error);
    }

    public static AuthResult failure(Object payload, Throwable error) {
        return new AuthResult(Status.FAILURE, payload, error);
    }

    public static AuthResult skipped() {
        return new AuthResult(Status.SKIPPED, null, null);
    }

    public Status getStatus() {
        return status;
    }

    public Object getPayload() {
        return payload;
    }

    public Throwable getError() {
        return error;
    }

    public boolean isSuccess() {
        return status == Status.SUCCESS;
    }
}
