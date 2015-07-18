package ibessonov.fractal.screen;

import ibessonov.fractal.cache.Colors;
import ibessonov.fractal.cache.PiecesCache;
import ibessonov.fractal.conf.Configuration;
import ibessonov.fractal.util.ThreadLocalIntArray;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.awt.image.WritableRaster;

/**
 *
 * @author Ivan Bessonov
 */
public class CalculationTask implements Runnable {

    private static final int SIZE = (int) Configuration.PIXELS_IN_UNIT;
    private static final ThreadLocalIntArray buffer = new ThreadLocalIntArray(SIZE * SIZE);
    private final long x, y;
    private final int zoom;
    private final Screen screen;

    public CalculationTask(long x, long y, int zoom, Screen screen) {
        this.x = x;
        this.y = y;
        this.zoom = zoom;
        this.screen = screen;
    }

    @Override
    @SuppressWarnings("CallToThreadDumpStack")
    public void run() {
        Image result = PiecesCache.get(zoom, x, y);
        if (null == result) {
            result = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
            WritableRaster raster = ((BufferedImage) result).getRaster();
            int[] colors = buffer.get();
            Calculator.getPiece(x, y, zoom, colors);
            for (int i = 0; i < SIZE; ++i) {
                for (int j = 0; j < SIZE; ++j) {
                    int[] color = Colors.get(colors[i * SIZE + j]);
                    raster.setPixel(i, j, color);
                }
            }
            PiecesCache.store(zoom, x, y, result);
        }
        screen.paint(zoom, x, y, result);
    }
}
