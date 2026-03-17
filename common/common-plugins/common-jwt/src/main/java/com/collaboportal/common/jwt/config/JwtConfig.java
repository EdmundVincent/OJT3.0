package com.collaboportal.common.jwt.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import java.util.Map;

@Configuration
@ConfigurationProperties(prefix = "jwt") // "jwt"プレフィックスを持つ設定を読み込む
public class JwtConfig {

    private Map<String, Integer> endpointPermissions;

    // getter, setterを定義
    public Map<String, Integer> getEndpointPermissions() {
        return endpointPermissions;
    }

    public void setEndpointPermissions(Map<String, Integer> endpointPermissions) {
        this.endpointPermissions = endpointPermissions;
    }
}
