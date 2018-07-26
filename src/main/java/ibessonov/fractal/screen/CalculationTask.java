package ibessonov.fractal.screen;

import ibessonov.fractal.cache.PiecesCache;
import ibessonov.fractal.conf.Configuration;

import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;

/**
 *
 * @author Ivan Bessonov
 */
public class CalculationTask implements Runnable {

    private static final int SIZE = (int) Configuration.PIXELS_IN_UNIT;
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
    public void run() {
        BufferedImage result = (BufferedImage) PiecesCache.get(zoom, x, y);
        if (null == result) {
            result = new BufferedImage(SIZE, SIZE, BufferedImage.TYPE_INT_RGB);
            Calculator.getPiece(x, y, zoom, ((DataBufferInt) result.getRaster().getDataBuffer()).getData());
            PiecesCache.store(zoom, x, y, result);
        }
        screen.paint(zoom, x, y, result);
    }
}
