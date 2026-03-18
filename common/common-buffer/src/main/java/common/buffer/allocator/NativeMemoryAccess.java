package common.buffer.allocator;

import java.nio.ByteBuffer;

final class NativeMemoryAccess {

    private NativeMemoryAccess() {
    }

    static ByteBuffer allocateDirect(int capacity) {
        return ByteBuffer.allocateDirect(capacity);
    }

    static void release(ByteBuffer buffer) {
        // The first version keeps chunks in the arena pool for reuse, so there is
        // nothing to do here yet.
    }
}
