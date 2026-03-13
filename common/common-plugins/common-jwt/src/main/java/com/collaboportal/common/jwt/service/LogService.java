package com.collaboportal.common.jwt.service;

public interface LogService {
    
    /**
     * DB登録
     * 
     * @param level エラーレベル
     * @param api API名
     * @param logMessage ログ
     */
    void addLog(String level, String api, String logMessage);
}
