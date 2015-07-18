package ibessonov.fractal.util;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 *
 * @author Ivan Bessonov
 */
public final class FastLock {

    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock read = lock.readLock();
    private final Lock write = lock.writeLock();

    @FunctionalInterface
    public static interface Raii extends AutoCloseable {

        @Override
        void close();
    }

    public Raii read() {
        read.lock();
        return () -> { read.unlock(); };
    }

    public Raii write() {
        write.lock();
        return () -> { write.unlock(); };
    }

}
