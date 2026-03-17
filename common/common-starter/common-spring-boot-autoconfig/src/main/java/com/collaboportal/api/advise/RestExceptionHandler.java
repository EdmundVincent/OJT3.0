package com.collaboportal.api.advise;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.collaboportal.api.model.ErrorResponseBody;
import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.common.exception.ForbiddenException;
import com.collaboportal.common.exception.PageNotFoundException;
import com.collaboportal.common.utils.Message;

import java.nio.file.AccessDeniedException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@RestControllerAdvice
public class RestExceptionHandler {

    Logger logger = LoggerFactory.getLogger(getClass());

    @ExceptionHandler(CommonException.class)
    public ErrorResponseBody handleCommonException(CommonException e) {
        InternalErrorCode errorCode = e.getInternalErrorCode();
        String nbErrCod = e.getNbErrCod();
        String errMsg = e.getErrMsg();
        String errLevel = e.getErrLevel();
        if (errorCode == null && nbErrCod == null) {
            logger.warn("=====業務エラー(InternalErrorCode未設定)=====", e);
            errorCode = InternalErrorCode.UNDEINED_ERROR;
        }
        if (errorCode != null) {
            logger.warn("=====業務エラー:{}({})=====", errorCode.getErrorMessage(), errorCode.getErrorId(), e);
        }
        return buildErrorResponse(errorCode, nbErrCod, errMsg, errLevel);
    }

    @ExceptionHandler(ForbiddenException.class)
    public ErrorResponseBody handleForbiddenException(ForbiddenException e) {
        InternalErrorCode errorCode = e.getErrorCode();
        if (errorCode == null) {
            logger.warn("=====Forbidden Error: InternalErrorCode未設定=====", e);
            errorCode = InternalErrorCode.AUTHORIZATION_ERROR;
        } else {
            logger.warn("=====Forbidden Error:{}({})=====", errorCode.getErrorMessage(), errorCode.getErrorId(), e);
        }
        return buildErrorResponse(errorCode, null, Message.W403, null);
    }

    @ExceptionHandler(BindException.class)
    public ErrorResponseBody handleBindException(BindException ex) {
        logger.info("===== Bind Exception =====: {}: {}", ex.getClass().getName(), ex.getMessage(), ex);
        return buildErrorResponse(InternalErrorCode.VALIDATION_ERROR, null, Message.W400, null);
    }

    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ErrorResponseBody handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        logger.error("===== System Error =====: {}: {}", e.getClass().getName(), e.getMessage(), e);
        return buildErrorResponse(InternalErrorCode.OPTIMISTIC_LOCKING_FAILURE_ERROR, null, Message.W409, null);
    }

    @ExceptionHandler(AuthenticationException.class)
    public ErrorResponseBody handleAuthorizationErrorException(AuthenticationException e) {
        logger.warn("===== Authentication Error =====: {}", e.getMessage());
        return buildErrorResponse(InternalErrorCode.AUTHORIZATION_ERROR, null, Message.W401, null);
    }

    @ExceptionHandler(Exception.class)
    public ErrorResponseBody handleGenericException(Exception e) {
        if (e instanceof AuthenticationException) {
            return buildErrorResponse(InternalErrorCode.AUTHORIZATION_ERROR, null, Message.W401, null);
        } else if (e instanceof DataAccessException) {
            logger.error("===== Database Error =====: {}", e.getMessage());
            return buildErrorResponse(InternalErrorCode.SYSTEM_ERROR, null, Message.W500, null);
        } else if (e instanceof AccessDeniedException) {
            logger.warn("===== Access Denied =====: {}", e.getMessage());
            return buildErrorResponse(InternalErrorCode.AUTHORIZATION_ERROR, null, Message.W403, null);
        } else if (e.getCause() instanceof IllegalArgumentException) {
            logger.warn("===== Bad Request Error =====: {}", e.getMessage());
            return buildErrorResponse(InternalErrorCode.VALIDATION_ERROR, null, Message.W400, null);
        } else if (e instanceof PageNotFoundException) {
            logger.warn("===== Page Not Found =====: {}", e.getMessage());
            return buildErrorResponse(InternalErrorCode.RECORD_NOT_FOUND_ERROR, null, Message.E404, null);
        } else {
            logger.error("===== System Error =====: {}: {}", e.getClass().getName(), e.getMessage(), e);
        }
        return buildErrorResponse(InternalErrorCode.SYSTEM_ERROR, null, Message.W500, null);
    }

    private ErrorResponseBody buildErrorResponse(InternalErrorCode errorCode, String nbErrCod, String errMsg, String errLevel) {
        String code = nbErrCod;
        if (code == null && errorCode != null) {
            code = String.valueOf(errorCode.getHttpStatus());
            if (errMsg == null) {
                errMsg = errorCode.getErrorMessage();
            }
        }
        if (code == null) {
            code = String.valueOf(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }
        return new ErrorResponseBody(code, errMsg, errLevel != null ? errLevel : Message.ERROR_LEVEL_ERROR);
    }
}
