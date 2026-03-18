package com.collaboportal.common.servlet;

import java.nio.ByteBuffer;
import java.util.Objects;

import common.buffer.allocator.BufferArena;
import common.frame.ZeroCopyFrame;

final class ServletZeroCopyReader {

    byte[] read(ByteBuffer source) {
        Objects.requireNonNull(source, "source");

        ByteBuffer input = source.duplicate();
        int readable = input.remaining();

        try (ZeroCopyFrame frame = BufferArena.allocate(readable)) {
            frame.nioBuffer().put(input);
            try (ZeroCopyFrame slice = frame.slice(0, readable)) {
                return slice.readBytes(readable);
            }
        }
    }
}
