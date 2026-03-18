package common.buffer.allocator;

import java.nio.ByteBuffer;
import java.util.concurrent.atomic.AtomicInteger;

final class MemoryChunk {

    private final long chunkId;
    private final ByteBuffer directBuffer;
    private final int capacity;
    private final AtomicInteger references = new AtomicInteger(0);

    MemoryChunk(long chunkId, ByteBuffer directBuffer) {
        this.chunkId = chunkId;
        this.directBuffer = directBuffer;
        this.capacity = directBuffer.capacity();
    }

    long id() {
        return chunkId;
    }

    int capacity() {
        return capacity;
    }

    void acquire() {
        if (!references.compareAndSet(0, 1)) {
            throw new IllegalStateException("チャンクが使ってます: " + chunkId);
        }
    }

    void retain() {
        int current;
        do {
            current = references.get();
            if (current <= 0) {
                throw new IllegalStateException("チャンクすでに解放されています: " + chunkId);
            }
        } while (!references.compareAndSet(current, current + 1));
    }

    boolean release() {
        int remaining = references.decrementAndGet();
        if (remaining < 0) {
            throw new IllegalStateException("Chunk reference count below zero: " + chunkId);
        }
        return remaining == 0;
    }

    ByteBuffer region(int offset, int length) {
        if (offset < 0 || length < 0 || offset + length > capacity) {
            throw new IndexOutOfBoundsException(
                    "Invalid region offset=" + offset + ", length=" + length + ", capacity=" + capacity);
        }
        ByteBuffer duplicate = directBuffer.duplicate();
        duplicate.position(offset);
        duplicate.limit(offset + length);
        return duplicate.slice();
    }
}
