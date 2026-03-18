package com.collaboportal.common.redis;

import java.time.Duration;
import java.util.Optional;

public interface RedisClient extends AutoCloseable {

    void set(String key, byte[] value);

    void set(String key, byte[] value, Duration ttl);

    Optional<byte[]> get(String key);

    boolean delete(String key);

    boolean exists(String key);

    @Override
    void close();
}
