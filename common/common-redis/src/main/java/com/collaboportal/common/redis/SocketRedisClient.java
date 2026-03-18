package com.collaboportal.common.redis;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

public final class SocketRedisClient implements RedisClient {

    private final Socket socket;
    private final InputStream input;
    private final OutputStream output;

    public SocketRedisClient(RedisConnectionConfig config) {
        Objects.requireNonNull(config, "config");
        try {
            this.socket = new Socket();
            this.socket.connect(new InetSocketAddress(config.host(), config.port()), config.connectTimeoutMillis());
            this.socket.setSoTimeout(config.readTimeoutMillis());
            this.input = new BufferedInputStream(socket.getInputStream());
            this.output = new BufferedOutputStream(socket.getOutputStream());

            if (config.password() != null && !config.password().isBlank()) {
                Reply authReply = send(List.of(bytes("AUTH"), bytes(config.password())));
                requireSimpleString(authReply, "OK");
            }
            if (config.database() > 0) {
                Reply selectReply = send(List.of(bytes("SELECT"), bytes(Integer.toString(config.database()))));
                requireSimpleString(selectReply, "OK");
            }
        } catch (IOException ex) {
            throw new RedisException("Failed to connect to Redis", ex);
        }
    }

    @Override
    public synchronized void set(String key, byte[] value) {
        set(key, value, null);
    }

    @Override
    public synchronized void set(String key, byte[] value, Duration ttl) {
        validateKey(key);
        Objects.requireNonNull(value, "value");

        List<byte[]> command = new ArrayList<>();
        command.add(bytes("SET"));
        command.add(bytes(key));
        command.add(value);
        if (ttl != null) {
            long millis = ttl.toMillis();
            if (millis <= 0) {
                throw new IllegalArgumentException("ttl must be greater than zero");
            }
            command.add(bytes("PX"));
            command.add(bytes(Long.toString(millis)));
        }

        Reply reply = send(command);
        requireSimpleString(reply, "OK");
    }

    @Override
    public synchronized Optional<byte[]> get(String key) {
        validateKey(key);
        Reply reply = send(List.of(bytes("GET"), bytes(key)));
        if (reply.type == ReplyType.NULL) {
            return Optional.empty();
        }
        if (reply.type != ReplyType.BULK_STRING) {
            throw new RedisException("Unexpected GET reply type: " + reply.type);
        }
        return Optional.of(reply.bulkValue);
    }

    @Override
    public synchronized boolean delete(String key) {
        validateKey(key);
        Reply reply = send(List.of(bytes("DEL"), bytes(key)));
        return requireInteger(reply) > 0;
    }

    @Override
    public synchronized boolean exists(String key) {
        validateKey(key);
        Reply reply = send(List.of(bytes("EXISTS"), bytes(key)));
        return requireInteger(reply) > 0;
    }

    @Override
    public synchronized void close() {
        try {
            socket.close();
        } catch (IOException ex) {
            throw new RedisException("Failed to close Redis socket", ex);
        }
    }

    private Reply send(List<byte[]> args) {
        try {
            writeCommand(args);
            output.flush();
            return readReply();
        } catch (IOException ex) {
            throw new RedisException("Redis command failed", ex);
        }
    }

    private void writeCommand(List<byte[]> args) throws IOException {
        output.write(bytes("*" + args.size() + "\r\n"));
        for (byte[] arg : args) {
            output.write(bytes("$" + arg.length + "\r\n"));
            output.write(arg);
            output.write(bytes("\r\n"));
        }
    }

    private Reply readReply() throws IOException {
        int prefix = input.read();
        if (prefix == -1) {
            throw new RedisException("Redis connection closed");
        }

        return switch ((char) prefix) {
            case '+' -> Reply.simpleString(readLine());
            case '-' -> throw new RedisException(readLine());
            case ':' -> Reply.integer(Long.parseLong(readLine()));
            case '$' -> readBulkStringReply();
            default -> throw new RedisException("Unsupported RESP prefix: " + (char) prefix);
        };
    }

    private Reply readBulkStringReply() throws IOException {
        int length = Integer.parseInt(readLine());
        if (length < 0) {
            return Reply.nil();
        }
        byte[] payload = input.readNBytes(length);
        discardCrlf();
        return Reply.bulkString(payload);
    }

    private String readLine() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        while (true) {
            int next = input.read();
            if (next == -1) {
                throw new RedisException("Unexpected end of RESP stream");
            }
            if (next == '\r') {
                int lf = input.read();
                if (lf != '\n') {
                    throw new RedisException("Malformed RESP line ending");
                }
                return buffer.toString(StandardCharsets.UTF_8);
            }
            buffer.write(next);
        }
    }

    private void discardCrlf() throws IOException {
        int cr = input.read();
        int lf = input.read();
        if (cr != '\r' || lf != '\n') {
            throw new RedisException("Malformed RESP payload terminator");
        }
    }

    private void validateKey(String key) {
        if (key == null || key.isBlank()) {
            throw new IllegalArgumentException("key must not be blank");
        }
    }

    private void requireSimpleString(Reply reply, String expected) {
        if (reply.type != ReplyType.SIMPLE_STRING || !expected.equals(reply.textValue)) {
            throw new RedisException("Unexpected Redis reply: " + reply);
        }
    }

    private long requireInteger(Reply reply) {
        if (reply.type != ReplyType.INTEGER) {
            throw new RedisException("Unexpected Redis integer reply: " + reply);
        }
        return reply.integerValue;
    }

    private static byte[] bytes(String value) {
        return value.getBytes(StandardCharsets.UTF_8);
    }

    private enum ReplyType {
        SIMPLE_STRING,
        BULK_STRING,
        INTEGER,
        NULL
    }

    private static final class Reply {
        private final ReplyType type;
        private final String textValue;
        private final byte[] bulkValue;
        private final long integerValue;

        private Reply(ReplyType type, String textValue, byte[] bulkValue, long integerValue) {
            this.type = type;
            this.textValue = textValue;
            this.bulkValue = bulkValue;
            this.integerValue = integerValue;
        }

        static Reply simpleString(String value) {
            return new Reply(ReplyType.SIMPLE_STRING, value, null, 0);
        }

        static Reply bulkString(byte[] value) {
            return new Reply(ReplyType.BULK_STRING, null, value, 0);
        }

        static Reply integer(long value) {
            return new Reply(ReplyType.INTEGER, null, null, value);
        }

        static Reply nil() {
            return new Reply(ReplyType.NULL, null, null, 0);
        }

        @Override
        public String toString() {
            return "Reply{type=" + type + ", textValue='" + textValue + "', integerValue=" + integerValue + "}";
        }
    }
}
