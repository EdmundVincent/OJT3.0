package com.collaboportal.common.jwt.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.collaboportal.common.jwt.service.HealthCheckService;
import com.collaboportal.common.model.MaintenanceBody;
import com.collaboportal.common.utils.Message;

import jakarta.servlet.http.HttpServletResponse;

@RestController
@RequestMapping("/health_check")
public class HealthCheckController {

    /** ロガー */
    Logger logger = LoggerFactory.getLogger(getClass());

    /** サービス */
    private final HealthCheckService healthCheckService;

    /** コンストラクタ */
    public HealthCheckController(HealthCheckService healthCheckService) {
        this.healthCheckService = healthCheckService;
        logger.info("HealthCheckControllerの初期化が完了しました");
    }

    @GetMapping()
    public void getHealthCheck(HttpServletResponse response) {
        logger.info("ヘルスチェックAPIのリクエストを受信しました");

        try {
            // サービスを呼び出す
            logger.debug("ヘルスチェックサービスの呼び出しを開始します");
            Integer result = healthCheckService.getHealthCheck("02");
            logger.debug("ヘルスチェックサービスの呼び出しが完了しました。結果コード: {}", result);

            // ステータスコードを設定する
            response.setStatus(result);
            MaintenanceBody.setMentFlg(result == 200 ? "0" : "1");
            logger.info("ヘルスチェック結果: {} (200:正常, それ以外:異常)", result);
            
            if (result == 200) {
                logger.info("システムは正常に動作しています");
            } else {
                logger.warn("システムに異常が検出されました。ステータスコード: {}", result);
            }
        } catch (Exception ex) {
            // エラーログ出力
            logger.error("ヘルスチェックAPIの実行中にエラーが発生しました", ex);
            logger.error("エラーの詳細: {}", ex.getMessage());

            // 例外が発生した場合、500のステータスコードを設定する
            response.setStatus(Message.SYSTEM_ERROR);
            logger.error("システムエラーが発生したため、500ステータスコードを返します");
        }
        
        logger.info("ヘルスチェックAPIの処理が完了しました");
    }
}