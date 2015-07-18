package ibessonov.fractal.util;

/**
 *
 * @author Ivan Bessonov
 */
public class ThreadLocalIntArray extends ThreadLocal<int[]> {

    private final int size;

    public ThreadLocalIntArray(int size) {
        this.size = size;
    }

    @Override
    protected int[] initialValue() {
        return new int[size];
    }
}
