package common.frame;

import java.nio.ByteBuffer;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicBoolean;

public final class ZeroCopyFrame implements AutoCloseable {

    @FunctionalInterface
    public interface SliceFactory {
        ZeroCopyFrame create(int absoluteOffset, int length);
    }

    private final ByteBuffer view;
    private final int absoluteOffset;
    private final int length;
    private final SliceFactory sliceFactory;
    private final Runnable releaseAction;
    private final AtomicBoolean closed = new AtomicBoolean(false);

    private int readIndex;

    public ZeroCopyFrame(ByteBuffer view, int absoluteOffset, int length, SliceFactory sliceFactory,
            Runnable releaseAction) {
        this.view = Objects.requireNonNull(view, "view");
        this.absoluteOffset = absoluteOffset;
        this.length = length;
        this.sliceFactory = Objects.requireNonNull(sliceFactory, "sliceFactory");
        this.releaseAction = Objects.requireNonNull(releaseAction, "releaseAction");
    }

    public int capacity() {
        ensureOpen();
        return length;
    }

    public int readableBytes() {
        ensureOpen();
        return length - readIndex;
    }

    public int readIndex() {
        ensureOpen();
        return readIndex;
    }

    public void setReadIndex(int readIndex) {
        ensureOpen();
        if (readIndex < 0 || readIndex > length) {
            throw new IndexOutOfBoundsException("readIndex out of bounds: " + readIndex);
        }
        this.readIndex = readIndex;
    }

    public ZeroCopyFrame slice(int offset, int sliceLength) {
        ensureOpen();
        if (offset < 0 || sliceLength < 0 || offset + sliceLength > length) {
            throw new IndexOutOfBoundsException(
                    "Invalid slice offset=" + offset + ", length=" + sliceLength + ", capacity=" + length);
        }
        return sliceFactory.create(absoluteOffset + offset, sliceLength);
    }

    public ByteBuffer nioBuffer() {
        ensureOpen();
        ByteBuffer duplicate = view.duplicate();
        duplicate.position(0);
        duplicate.limit(length);
        return duplicate.slice();
    }

    public byte readByte() {
        ensureReadable(1);
        byte value = view.get(readIndex);
        readIndex += 1;
        return value;
    }

    public int readInt() {
        ensureReadable(Integer.BYTES);
        int value = view.getInt(readIndex);
        readIndex += Integer.BYTES;
        return value;
    }

    public long readLong() {
        ensureReadable(Long.BYTES);
        long value = view.getLong(readIndex);
        readIndex += Long.BYTES;
        return value;
    }

    public byte[] readBytes(int size) {
        ensureReadable(size);
        byte[] bytes = new byte[size];
        ByteBuffer duplicate = view.duplicate();
        duplicate.position(readIndex);
        duplicate.get(bytes);
        readIndex += size;
        return bytes;
    }

    @Override
    public void close() {
        if (closed.compareAndSet(false, true)) {
            releaseAction.run();
        }
    }

    private void ensureOpen() {
        if (closed.get()) {
            throw new IllegalStateException("Frame already closed");
        }
    }

    private void ensureReadable(int size) {
        ensureOpen();
        if (size < 0 || readIndex + size > length) {
            throw new IndexOutOfBoundsException(
                    "Not enough readable bytes: requested=" + size + ", readable=" + readableBytes());
        }
    }
}
