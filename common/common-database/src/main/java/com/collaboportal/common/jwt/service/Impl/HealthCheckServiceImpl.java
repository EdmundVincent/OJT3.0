package com.collaboportal.common.jwt.service.Impl;
import org.springframework.stereotype.Service;

import com.collaboportal.common.jwt.repository.HealthCheckMapper;
import com.collaboportal.common.jwt.service.HealthCheckService;
import com.collaboportal.common.utils.Message;



@Service
public class HealthCheckServiceImpl implements HealthCheckService {

    /** ヘルスチェック マッパー */
    private final HealthCheckMapper healthCheckMapper;

    /** コンストラクタ */
    public HealthCheckServiceImpl(HealthCheckMapper healthCheckMapper) {
        this.healthCheckMapper = healthCheckMapper;
    }

    @Override
    public Integer getHealthCheck(String keyWord){

        // クエリを呼び出す
        Integer result = healthCheckMapper.getHealthCheck(keyWord);

        // クエリの取得結果が0件の場合、500を返す
        if(result == null){
            return Message.SYSTEM_ERROR;
        }

        return result;
    }
}