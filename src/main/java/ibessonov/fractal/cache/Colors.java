package ibessonov.fractal.cache;

import java.awt.Color;

/**
 *
 * @author Ivan Bessonov
 */
public final class Colors {

    private static final int len = 10;
    private static final int SIZE = 1 << len;
    private static final int[][] cachedColors = new int[SIZE][SIZE];
    static {
        for (int h = 0; h < SIZE; h++) {
            for (int b = 0; b < SIZE; b++) {
                Color color = Color.getHSBColor(
                        ((float) h) / (SIZE - 1), 0.75f,
                        ((float) b) / (SIZE - 1));
                cachedColors[h][b] = color.getRGB();
             }
        }
    }

    public static int get(int color) {
        int h = (color >>> 16)   >> (16 - len);
        int b = (color & 0xFFFF) >> (16 - len);
        return cachedColors[h][b];
    }
}
