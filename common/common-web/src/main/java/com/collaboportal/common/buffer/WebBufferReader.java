package com.collaboportal.common.buffer;

import java.nio.ByteBuffer;
import java.util.Objects;

import common.buffer.allocator.BufferArena;
import common.frame.ZeroCopyFrame;

public final class WebBufferReader {

    public byte[] readSlice(ByteBuffer source, int sliceOffset, int sliceLength) {
        Objects.requireNonNull(source, "source");

        ByteBuffer input = source.duplicate();
        int readable = input.remaining();

        try (ZeroCopyFrame frame = BufferArena.allocate(readable)) {
            ByteBuffer target = frame.nioBuffer();
            target.put(input);

            try (ZeroCopyFrame slice = frame.slice(sliceOffset, sliceLength)) {
                return slice.readBytes(slice.readableBytes());
            }
        }
    }
}
