package com.collaboportal.common.spring.redis;

import org.springframework.boot.autoconfigure.AutoConfiguration;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import com.collaboportal.common.redis.RedisClient;
import com.collaboportal.common.redis.RedisConnectionConfig;
import com.collaboportal.common.redis.SocketRedisClient;

@AutoConfiguration
@ConditionalOnClass(SocketRedisClient.class)
@EnableConfigurationProperties(RedisProperties.class)
public class RedisAutoConfiguration {

    @Configuration(proxyBeanMethods = false)
    @ConditionalOnProperty(prefix = "common.redis", name = "enabled", havingValue = "true", matchIfMissing = true)
    static class EnabledRedisConfiguration {

        @Bean
        @ConditionalOnMissingBean
        @ConditionalOnProperty(prefix = "common.redis", name = "host")
        public RedisConnectionConfig redisConnectionConfig(RedisProperties properties) {
            return new RedisConnectionConfig(
                    properties.getHost(),
                    properties.getPort(),
                    properties.getConnectTimeoutMillis(),
                    properties.getReadTimeoutMillis(),
                    properties.getPassword(),
                    properties.getDatabase());
        }

        @Bean(destroyMethod = "close")
        @ConditionalOnMissingBean({RedisClient.class, SocketRedisClient.class})
        @ConditionalOnProperty(prefix = "common.redis", name = "host")
        public SocketRedisClient redisClient(RedisConnectionConfig config) {
            return new SocketRedisClient(config);
        }
    }
}
