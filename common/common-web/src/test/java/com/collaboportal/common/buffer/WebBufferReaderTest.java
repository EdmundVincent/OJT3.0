package com.collaboportal.common.buffer;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;

import org.junit.jupiter.api.Test;

import common.buffer.allocator.BufferArena;

class WebBufferReaderTest {

    private final WebBufferReader reader = new WebBufferReader();

    @Test
    void readSlice_shouldCopyByteBufferIntoZeroCopyFrameAndReadSlice() {
        String payload = "Authorization: Bearer demo-token";
        String expected = "Bearer demo-token";
        ByteBuffer input = ByteBuffer.wrap(payload.getBytes(StandardCharsets.US_ASCII));

        byte[] result = reader.readSlice(input, payload.indexOf(expected), expected.length());

        assertArrayEquals(expected.getBytes(StandardCharsets.US_ASCII), result);
        assertEquals(0, BufferArena.activeAllocations());
    }

    @Test
    void readSlice_shouldFailWhenSliceExceedsFrameBounds() {
        ByteBuffer input = ByteBuffer.wrap("short".getBytes(StandardCharsets.US_ASCII));

        assertThrows(IndexOutOfBoundsException.class, () -> reader.readSlice(input, 2, 8));
        assertEquals(0, BufferArena.activeAllocations());
    }
}
