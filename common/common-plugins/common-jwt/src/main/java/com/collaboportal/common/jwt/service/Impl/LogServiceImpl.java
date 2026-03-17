package com.collaboportal.common.jwt.service.Impl;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.collaboportal.common.jwt.repository.LogMapper;
import com.collaboportal.common.jwt.service.LogService;




@Service
public class LogServiceImpl implements LogService{

    /** ロガー */
    Logger logger = LoggerFactory.getLogger(getClass());

    // マッパー
    private final LogMapper logMapper;

    public LogServiceImpl(LogMapper logMapper) {
        this.logMapper = logMapper;
    }

    /**
     * DB登録
     * 
     * @param level エラーレベル
     * @param api API名
     * @param logMessage ログ
     */
    @Override
    public void addLog(String level, String api, String logMessage){
        try {
            // ログ登録
            logMapper.addLog(level, api, logMessage);
        } catch (Exception ex) {
            logger.error("操作ログ出力処理でエラーが発生しました。API名:{}，内容:{}", api, logMessage, ex);
        }
    }
}
