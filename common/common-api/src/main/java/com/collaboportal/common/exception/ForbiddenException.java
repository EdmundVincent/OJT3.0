package com.collaboportal.common.exception;

import com.collaboportal.common.error.InternalErrorCode;

public class ForbiddenException extends RuntimeException {
    
    private InternalErrorCode errorCode;
    public ForbiddenException(InternalErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }

    public InternalErrorCode getErrorCode() {
        return errorCode;
    }
}
