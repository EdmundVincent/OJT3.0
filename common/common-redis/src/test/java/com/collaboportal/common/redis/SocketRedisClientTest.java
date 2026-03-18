package com.collaboportal.common.redis;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Arrays;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

import org.junit.jupiter.api.Test;

class SocketRedisClientTest {

    @Test
    void socketRedisClient_shouldSupportBasicKvOperations() throws Exception {
        try (FakeRedisServer server = new FakeRedisServer();
                SocketRedisClient client = new SocketRedisClient(RedisConnectionConfig.of("127.0.0.1", server.port()))) {
            byte[] payload = "hello-redis".getBytes(StandardCharsets.UTF_8);

            client.set("sample:key", payload, Duration.ofSeconds(5));

            Optional<byte[]> actual = client.get("sample:key");
            assertTrue(actual.isPresent());
            assertArrayEquals(payload, actual.orElseThrow());
            assertTrue(client.exists("sample:key"));
            assertTrue(client.delete("sample:key"));
            assertFalse(client.exists("sample:key"));
            assertFalse(client.get("sample:key").isPresent());
        }
    }

    private static final class FakeRedisServer implements AutoCloseable {
        private final ServerSocket serverSocket;
        private final Thread serverThread;
        private final Map<String, ValueEntry> store = new ConcurrentHashMap<>();

        FakeRedisServer() throws IOException {
            this.serverSocket = new ServerSocket(0);
            this.serverThread = new Thread(this::serve, "fake-redis-server");
            this.serverThread.start();
        }

        int port() {
            return serverSocket.getLocalPort();
        }

        private void serve() {
            try (Socket socket = serverSocket.accept();
                    BufferedInputStream input = new BufferedInputStream(socket.getInputStream());
                    BufferedOutputStream output = new BufferedOutputStream(socket.getOutputStream())) {
                while (!socket.isClosed()) {
                    String[] command = readCommand(input);
                    if (command == null) {
                        return;
                    }
                    writeReply(output, handle(command));
                    output.flush();
                }
            } catch (IOException ignored) {
            }
        }

        private byte[] handle(String[] command) {
            String op = command[0].toUpperCase();
            return switch (op) {
                case "SET" -> handleSet(command);
                case "GET" -> handleGet(command[1]);
                case "DEL" -> integerReply(store.remove(command[1]) == null ? 0 : 1);
                case "EXISTS" -> integerReply(resolve(command[1]).isPresent() ? 1 : 0);
                case "AUTH", "SELECT" -> simpleString("OK");
                default -> errorReply("unsupported command " + op);
            };
        }

        private byte[] handleSet(String[] command) {
            long expireAt = 0L;
            if (command.length == 5 && "PX".equalsIgnoreCase(command[3])) {
                expireAt = System.currentTimeMillis() + Long.parseLong(command[4]);
            }
            store.put(command[1], new ValueEntry(command[2].getBytes(StandardCharsets.UTF_8), expireAt));
            return simpleString("OK");
        }

        private byte[] handleGet(String key) {
            Optional<ValueEntry> entry = resolve(key);
            if (entry.isEmpty()) {
                return "$-1\r\n".getBytes(StandardCharsets.UTF_8);
            }
            byte[] value = entry.orElseThrow().value;
            byte[] header = ("$" + value.length + "\r\n").getBytes(StandardCharsets.UTF_8);
            byte[] suffix = "\r\n".getBytes(StandardCharsets.UTF_8);
            byte[] reply = Arrays.copyOf(header, header.length + value.length + suffix.length);
            System.arraycopy(value, 0, reply, header.length, value.length);
            System.arraycopy(suffix, 0, reply, header.length + value.length, suffix.length);
            return reply;
        }

        private Optional<ValueEntry> resolve(String key) {
            ValueEntry entry = store.get(key);
            if (entry == null) {
                return Optional.empty();
            }
            if (entry.expireAt > 0 && entry.expireAt < System.currentTimeMillis()) {
                store.remove(key);
                return Optional.empty();
            }
            return Optional.of(entry);
        }

        private String[] readCommand(BufferedInputStream input) throws IOException {
            int prefix = input.read();
            if (prefix == -1) {
                return null;
            }
            if (prefix != '*') {
                throw new IOException("Unexpected prefix: " + (char) prefix);
            }
            int parts = Integer.parseInt(readLine(input));
            String[] command = new String[parts];
            for (int i = 0; i < parts; i++) {
                if (input.read() != '$') {
                    throw new IOException("Expected bulk string");
                }
                int length = Integer.parseInt(readLine(input));
                byte[] value = input.readNBytes(length);
                discardCrlf(input);
                command[i] = new String(value, StandardCharsets.UTF_8);
            }
            return command;
        }

        private String readLine(BufferedInputStream input) throws IOException {
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            while (true) {
                int next = input.read();
                if (next == -1) {
                    throw new IOException("Unexpected EOF");
                }
                if (next == '\r') {
                    if (input.read() != '\n') {
                        throw new IOException("Malformed CRLF");
                    }
                    return buffer.toString(StandardCharsets.UTF_8);
                }
                buffer.write(next);
            }
        }

        private void discardCrlf(BufferedInputStream input) throws IOException {
            if (input.read() != '\r' || input.read() != '\n') {
                throw new IOException("Malformed payload terminator");
            }
        }

        private void writeReply(BufferedOutputStream output, byte[] reply) throws IOException {
            output.write(reply);
        }

        private byte[] simpleString(String value) {
            return ("+" + value + "\r\n").getBytes(StandardCharsets.UTF_8);
        }

        private byte[] integerReply(long value) {
            return (":" + value + "\r\n").getBytes(StandardCharsets.UTF_8);
        }

        private byte[] errorReply(String message) {
            return ("-" + message + "\r\n").getBytes(StandardCharsets.UTF_8);
        }

        @Override
        public void close() throws Exception {
            serverSocket.close();
            serverThread.join(2000);
        }
    }

    private record ValueEntry(byte[] value, long expireAt) {
    }
}
