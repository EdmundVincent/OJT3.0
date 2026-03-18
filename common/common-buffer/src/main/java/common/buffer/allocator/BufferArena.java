package common.buffer.allocator;

import java.nio.ByteBuffer;
import java.util.Queue;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.concurrent.atomic.AtomicLong;

import common.frame.ZeroCopyFrame;

public final class BufferArena {

    private static final AtomicLong NEXT_CHUNK_ID = new AtomicLong(1);
    private static final Map<Integer, Queue<MemoryChunk>> POOL = new ConcurrentHashMap<>();
    private static final Map<Long, MemoryChunk> ACTIVE_CHUNKS = new ConcurrentHashMap<>();

    private BufferArena() {
    }

    public static ZeroCopyFrame allocate(int length) {
        if (length <= 0) {
            throw new IllegalArgumentException("length must be greater than zero");
        }

        MemoryChunk chunk = borrowChunk(length);
        chunk.acquire();
        ACTIVE_CHUNKS.put(chunk.id(), chunk);
        return newFrame(chunk, 0, length);
    }

    public static int activeAllocations() {
        return ACTIVE_CHUNKS.size();
    }

    public static int pooledChunks(int length) {
        Queue<MemoryChunk> bucket = POOL.get(length);
        return bucket == null ? 0 : bucket.size();
    }

    private static ZeroCopyFrame newFrame(MemoryChunk chunk, int offset, int length) {
        return new ZeroCopyFrame(
                chunk.region(offset, length),
                offset,
                length,
                (sliceOffset, sliceLength) -> {
                    chunk.retain();
                    return newFrame(chunk, sliceOffset, sliceLength);
                },
                () -> {
                    if (chunk.release()) {
                        ACTIVE_CHUNKS.remove(chunk.id());
                        recycle(chunk);
                    }
                });
    }

    private static MemoryChunk borrowChunk(int length) {
        Queue<MemoryChunk> bucket = POOL.computeIfAbsent(length, ignored -> new ConcurrentLinkedQueue<>());
        MemoryChunk chunk = bucket.poll();
        if (chunk != null) {
            return chunk;
        }

        long chunkId = NEXT_CHUNK_ID.getAndIncrement();
        ByteBuffer directBuffer = NativeMemoryAccess.allocateDirect(length);
        return new MemoryChunk(chunkId, directBuffer);
    }

    private static void recycle(MemoryChunk chunk) {
        POOL.computeIfAbsent(chunk.capacity(), ignored -> new ConcurrentLinkedQueue<>()).offer(chunk);
    }
}
