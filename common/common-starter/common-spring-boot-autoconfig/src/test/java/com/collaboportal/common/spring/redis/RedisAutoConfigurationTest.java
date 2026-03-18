package com.collaboportal.common.spring.redis;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import com.collaboportal.common.redis.RedisClient;
import com.collaboportal.common.redis.SocketRedisClient;
import com.collaboportal.common.redis.RedisConnectionConfig;

class RedisAutoConfigurationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(RedisAutoConfiguration.class));

    @Test
    void shouldRegisterRedisBeansWhenEnabledAndHostIsConfigured() throws Exception {
        try (AcceptOnceServer server = new AcceptOnceServer()) {
            contextRunner
                    .withPropertyValues(
                            "common.redis.host=127.0.0.1",
                            "common.redis.port=" + server.port(),
                            "common.redis.connect-timeout-millis=1500",
                            "common.redis.read-timeout-millis=2500",
                            "common.redis.enabled=true")
                    .run(context -> {
                        assertNotNull(context.getBean(RedisProperties.class));
                        assertThat(context).hasSingleBean(RedisConnectionConfig.class);
                        assertThat(context).hasSingleBean(RedisClient.class);
                        assertThat(context).hasSingleBean(SocketRedisClient.class);
                    });
        }
    }

    @Test
    void shouldBindConnectionPropertiesWithoutCreatingClientWhenDisabled() {
        contextRunner
                .withPropertyValues(
                        "common.redis.host=127.0.0.1",
                        "common.redis.port=6380",
                        "common.redis.connect-timeout-millis=1500",
                        "common.redis.read-timeout-millis=2500",
                        "common.redis.enabled=false")
                .run(context -> {
                    RedisProperties properties = context.getBean(RedisProperties.class);
                    assertEquals("127.0.0.1", properties.getHost());
                    assertEquals(6380, properties.getPort());
                    assertEquals(1500, properties.getConnectTimeoutMillis());
                    assertEquals(2500, properties.getReadTimeoutMillis());
                    assertEquals(false, properties.isEnabled());
                    assertThat(context).doesNotHaveBean(RedisClient.class);
                    assertThat(context).doesNotHaveBean(RedisConnectionConfig.class);
                });
    }

    private static final class AcceptOnceServer implements AutoCloseable {
        private final ServerSocket serverSocket;
        private final Thread serverThread;

        private AcceptOnceServer() throws IOException {
            this.serverSocket = new ServerSocket(0);
            this.serverThread = new Thread(this::acceptOnce, "redis-auto-config-test-server");
            this.serverThread.start();
        }

        private int port() {
            return serverSocket.getLocalPort();
        }

        private void acceptOnce() {
            try (Socket ignored = serverSocket.accept()) {
                // The Redis client only needs a successful TCP connect during bean creation.
            } catch (IOException ignored) {
            }
        }

        @Override
        public void close() throws Exception {
            serverSocket.close();
            serverThread.join(2000);
        }
    }
}
