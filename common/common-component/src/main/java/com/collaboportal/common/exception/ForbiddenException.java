package com.collaboportal.common.exception;

import com.collaboportal.common.error.InternalErrorCode;

import lombok.Getter;

@Getter
public class ForbiddenException extends RuntimeException {
    
    private InternalErrorCode errorCode;
    public ForbiddenException(InternalErrorCode errorCode) {
        super(errorCode.getErrorMessage());
        this.errorCode = errorCode;
    }
}
