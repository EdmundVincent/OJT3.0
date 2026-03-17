package com.collaboportal.common.jwt.repository;

import org.apache.ibatis.annotations.Mapper;
import org.springframework.data.repository.query.Param;

@Mapper
public interface LogMapper {
    
    /**
     * DB登録
     * 
     * @param level エラーレベル
     * @param api API名
     * @param logMessage ログ
     */
    void addLog(@Param("level")String level, @Param("api")String api, @Param("logMessage")String logMessage);
}
