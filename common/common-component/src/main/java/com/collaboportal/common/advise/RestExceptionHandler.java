package com.collaboportal.common.advise;

import org.springframework.dao.DataAccessException;
import org.springframework.dao.OptimisticLockingFailureException;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.collaboportal.common.error.InternalErrorCode;
import com.collaboportal.common.exception.AuthenticationException;
import com.collaboportal.common.exception.CommonException;
import com.collaboportal.common.exception.ForbiddenException;
import com.collaboportal.common.exception.PageNotFoundException;
import com.collaboportal.common.model.ErrorResponseBody;
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

    // カスタムエラーのハンドリング
    // 認可エラー
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

    // クエリパラメータのバリデーションエラーのハンドリング
    @ExceptionHandler(BindException.class)
    public ErrorResponseBody handleBindException(BindException ex) {
        logger.info("===== Bind Exception =====: {}: {}", ex.getClass().getName(), ex.getMessage(), ex);
        return buildErrorResponse(InternalErrorCode.VALIDATION_ERROR, null, Message.W400, null);
    }

    // DB排他エラーのハンドリング
    @ExceptionHandler(OptimisticLockingFailureException.class)
    public ErrorResponseBody handleOptimisticLockingFailureException(OptimisticLockingFailureException e) {
        logger.error("===== System Error =====: {}: {}", e.getClass().getName(), e.getMessage(), e);
        return buildErrorResponse(InternalErrorCode.OPTIMISTIC_LOCKING_FAILURE_ERROR, null, Message.W409, null);
    }

    // 認証エラーのハンドリング
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

    private static String getStackTrace(Throwable ex) {
        StackTraceElement[] list = ex.getStackTrace();
        StringBuilder b = new StringBuilder();
        b.append(ex.getClass()).append(":").append(ex.getMessage()).append("\n");
        for (StackTraceElement s : list) {
            b.append(s.toString()).append("\n");
        }
        return b.toString();
    }

    private ErrorResponseBody buildErrorResponse(InternalErrorCode errorCode, String nbErrCod, String errorMessage, String errorLevel) {
        String responseCode = nbErrCod != null ? nbErrCod : resolveCode(errorCode);
        if (responseCode == null) {
            responseCode = Integer.toString(HttpStatus.INTERNAL_SERVER_ERROR.value());
        }

        String responseMessage = errorMessage != null ? errorMessage : resolveMessage(errorCode);
        if (responseMessage == null) {
            responseMessage = Message.W500;
        }

        HttpStatus status = mapHttpStatus(errorCode, responseCode);
        String level = errorLevel != null ? errorLevel
                : (status.is4xxClientError() ? Message.ERROR_LEVEL_WARNING : Message.ERROR_LEVEL_ERROR);

        return new ErrorResponseBody(responseCode, responseMessage, level);
    }

    private HttpStatus mapHttpStatus(InternalErrorCode errorCode, String nbErrCod) {
        if (errorCode != null) {
            HttpStatus status = resolveFromInteger(errorCode.getInternalErrorCode());
            if (status != null) {
                return status;
            }
        }

        if (nbErrCod != null) {
            try {
                HttpStatus status = resolveFromInteger(Integer.parseInt(nbErrCod));
                if (status != null) {
                    return status;
                }
            } catch (NumberFormatException ignored) {
                // ignore and try prefix resolution below
            }

            if (nbErrCod.length() > 3) {
                try {
                    HttpStatus status = resolveFromInteger(Integer.parseInt(nbErrCod.substring(0, 3)));
                    if (status != null) {
                        return status;
                    }
                } catch (NumberFormatException ignored) {
                    // fall through
                }
            }
        }

        return HttpStatus.INTERNAL_SERVER_ERROR;
    }

    private HttpStatus resolveFromInteger(Integer code) {
        if (code == null) {
            return null;
        }
        HttpStatus status = HttpStatus.resolve(code);
        if (status != null) {
            return status;
        }

        String codeStr = Integer.toString(code);
        if (codeStr.length() > 3) {
            try {
                return HttpStatus.resolve(Integer.parseInt(codeStr.substring(0, 3)));
            } catch (NumberFormatException ignored) {
                return null;
            }
        }
        return null;
    }

    private String resolveCode(InternalErrorCode errorCode) {
        if (errorCode == null) {
            return null;
        }
        Integer code = errorCode.getInternalErrorCode();
        return code != null ? code.toString() : null;
    }

    private String resolveMessage(InternalErrorCode errorCode) {
        return errorCode != null ? errorCode.getErrorMessage() : null;
    }

}
