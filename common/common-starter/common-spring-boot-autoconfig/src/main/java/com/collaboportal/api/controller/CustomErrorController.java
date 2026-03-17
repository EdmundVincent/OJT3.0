package com.collaboportal.api.controller;

import com.collaboportal.api.model.ErrorResponseBody;
import com.collaboportal.common.utils.Message;
import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class CustomErrorController implements ErrorController {

    private static final Logger log = LoggerFactory.getLogger(CustomErrorController.class);

    public CustomErrorController() {
        log.debug("=== CustomErrorController が初期化されました ===");
    }

    @RequestMapping("/error")
    public ErrorResponseBody handleError(HttpServletRequest request) {
        Integer statusCode = (Integer) request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        Throwable ex = (Throwable) request.getAttribute(RequestDispatcher.ERROR_EXCEPTION);
        String requestUri = (String) request.getAttribute(RequestDispatcher.ERROR_REQUEST_URI);
        HttpStatus status = (statusCode != null)
                ? HttpStatus.resolve(statusCode)
                : HttpStatus.INTERNAL_SERVER_ERROR;
        String path = (requestUri != null) ? requestUri : "不明";
        String cause = (ex != null) ? ex.getMessage() : "不明";
        switch (status) {
            case NOT_FOUND -> {
                log.info("リソースが見つかりません: [{}] {}", status.value(), path);
                return new ErrorResponseBody(
                        "404",
                        "お探しのページは存在しません。",
                        Message.ERROR_LEVEL_WARNING);
            }
            case FORBIDDEN -> {
                log.warn("アクセス拒否: {}", path);
                return new ErrorResponseBody(
                        "403",
                        "アクセス権限がありません。",
                        Message.ERROR_LEVEL_WARNING);
            }
            case INTERNAL_SERVER_ERROR -> {
                log.error("サーバーエラー: {}, 原因: {}", path, cause);
                return new ErrorResponseBody(
                        "500",
                        "システムエラーが発生しました。管理者へ連絡してください。",
                        Message.W500);
            }
            default -> {
                log.error("想定外エラー: [{}] {}, 原因: {}", status.value(), path, cause);
                return new ErrorResponseBody(
                        String.valueOf(status.value()),
                        "予期しないエラーが発生しました。",
                        Message.W500);
            }
        }
    }
}
