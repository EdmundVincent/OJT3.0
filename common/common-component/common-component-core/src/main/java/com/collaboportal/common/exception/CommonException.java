package com.collaboportal.common.exception;

import com.collaboportal.common.error.InternalErrorCode;

public class CommonException extends RuntimeException {
    private final InternalErrorCode internalErrorCode;
    private final String nbErrCod;
    private final String errMsg;
    private final String errLevel;
    public CommonException(InternalErrorCode internalErrorCode) { this(internalErrorCode, null, null); }
    public CommonException(InternalErrorCode internalErrorCode, String errMsg) { this(internalErrorCode, errMsg, null); }
    public CommonException(InternalErrorCode internalErrorCode, String errMsg, String errLevel) {
        super(errMsg != null ? errMsg : internalErrorCode.getErrorMessage());
        this.internalErrorCode = internalErrorCode;
        this.nbErrCod = String.valueOf(internalErrorCode.getHttpStatus());
        this.errMsg = super.getMessage();
        this.errLevel = errLevel == null ? "E" : errLevel;
    }
    public InternalErrorCode getInternalErrorCode() { return internalErrorCode; }
    public String getNbErrCod() { return nbErrCod; }
    public String getErrMsg() { return errMsg; }
    public String getErrLevel() { return errLevel; }
}
