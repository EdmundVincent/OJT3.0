package com.collaboportal.common.redis;

public record RedisConnectionConfig(
        String host,
        int port,
        int connectTimeoutMillis,
        int readTimeoutMillis,
        String password,
        int database) {

    public RedisConnectionConfig {
        if (host == null || host.isBlank()) {
            throw new IllegalArgumentException("host must not be blank");
        }
        if (port <= 0) {
            throw new IllegalArgumentException("port must be greater than zero");
        }
        if (connectTimeoutMillis < 0 || readTimeoutMillis < 0) {
            throw new IllegalArgumentException("timeouts must be zero or greater");
        }
        if (database < 0) {
            throw new IllegalArgumentException("database must be zero or greater");
        }
    }

    public static RedisConnectionConfig of(String host, int port) {
        return new RedisConnectionConfig(host, port, 3000, 3000, null, 0);
    }
}
