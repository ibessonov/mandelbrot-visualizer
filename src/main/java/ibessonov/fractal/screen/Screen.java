package ibessonov.fractal.screen;

import ibessonov.fractal.cache.PiecesCache;
import ibessonov.fractal.conf.Configuration;
import ibessonov.fractal.events.ImageMoveListener;
import ibessonov.fractal.events.ImagePaintListener;
import ibessonov.fractal.events.ImageResizeListener;
import ibessonov.fractal.events.ImageZoomListener;
import ibessonov.fractal.util.FastLock;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.RenderedImage;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Ivan Bessonov
 */
public class Screen implements ImageMoveListener, ImageResizeListener,
        ImageZoomListener {

    private static final int BUFFER_WIDTH = Toolkit.getDefaultToolkit().getScreenSize().width;
    private static final int BUFFER_HEIGHT =  Toolkit.getDefaultToolkit().getScreenSize().height;


    public Screen() {
        listenerList = new EventListenerList();
        executorService = new ThreadPoolExecutor(
                Runtime.getRuntime().availableProcessors(),
                Runtime.getRuntime().availableProcessors(),
                0, TimeUnit.MICROSECONDS,
                new LinkedBlockingDeque<Runnable>() {

                    @Override
                    public boolean offerFirst(Runnable e) {
                        return super.offerLast(e);
                    }

                    @Override
                    public boolean offerLast(Runnable e) {
                        return super.offerFirst(e);
                    }
                });
        Graphics g = white.getGraphics();
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, BUFFER_WIDTH, BUFFER_HEIGHT);
        back.getGraphics().drawImage(white, 0, 0, null);
    }
    private final EventListenerList listenerList;
    private final ThreadPoolExecutor executorService;
    private Image image = new BufferedImage(BUFFER_WIDTH, BUFFER_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
    private Image back = new BufferedImage(BUFFER_WIDTH, BUFFER_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
    private Image white = new BufferedImage(BUFFER_WIDTH, BUFFER_HEIGHT, BufferedImage.TYPE_3BYTE_BGR);
    private int width = 0, height = 0;
    private int zoom = 0;
    private long originX = (long) (-0.75 * Configuration.PIXELS_IN_UNIT);
    private long originY = 0;

    private FastLock lock = new FastLock();

    public Image getImage() {
        try (FastLock.Raii r = lock.read()) {
            return image;
        }
    }

    public RenderedImage getRenderedImage() {
        try (FastLock.Raii r = lock.read()) {
            BufferedImage buffer = new BufferedImage(width, height, BufferedImage.TYPE_3BYTE_BGR);
            Graphics graphics = buffer.getGraphics();
            graphics.drawImage(image, 0, 0, null);
            graphics.dispose();
            return buffer;
        }
    }

    private void flip() {
        Image temp = image;
        image = back;
        back = temp;
        back.getGraphics().drawImage(white, 0, 0, null);
    }

    @Override
    public void imageMoved(int dx, int dy) {
        int x0, y0, x1, y1;
        try (FastLock.Raii w = lock.write()) {
            originX += dx;
            originY += dy;
            x0 = Math.max(0, -dx);
            y0 = Math.max(0, -dy);
            x1 = Math.min(width, width - dx);
            y1 = Math.min(height, height - dy);
            back.getGraphics().drawImage(image, -dx, -dy, Color.WHITE, null);
            flip();
        }

        notifyImagePaint(x0, y0, x1, y1);
        createTasks(x0, y0, x1, y1);
    }

    @Override
    public void imageResized(int width, int height) {
        try (FastLock.Raii w = lock.write()) {
            this.width = width;
            this.height = height;

            notifyImagePaint();
            createTasks();
        }
    }

    @Override
    public void imageZoomed(int rotation) {
        try (FastLock.Raii w = lock.write()) {
            zoom += rotation;
            if (zoom < 0) {
                rotation -= zoom;
                zoom = 0;
            } else if (zoom > Configuration.ZOOM_DEPTH) {
                rotation -= zoom - Configuration.ZOOM_DEPTH;
                zoom = Configuration.ZOOM_DEPTH;
            }
            if (0 == rotation) {
                return;
            } else if (rotation < 0) {
                originX >>= -rotation;
                originY >>= -rotation;
            } else {
                originX <<= rotation;
                originY <<= rotation;
            }

            if (rotation < 0) {
                int dz = 1 << -rotation;
                int dx1 = width / 2 - width / 2 / dz;
                int dy1 = height / 2 - height / 2 / dz;
                int dx2 = dx1 + width / dz;
                int dy2 = dy1 + height / dz;
                back.getGraphics().drawImage(image, dx1, dy1, dx2, dy2, 0, 0, width, height, null);
            } else {
                int dz = 1 << rotation;
                int dx1 = width / 2 - width / 2 / dz;
                int dy1 = height / 2 - height / 2 / dz;
                int dx2 = dx1 + width / dz;
                int dy2 = dy1 + height / dz;
                back.getGraphics().drawImage(image, 0, 0, width, height, dx1, dy1, dx2, dy2, null);
            }
            flip();
        }

        notifyImagePaint();
        createTasks();
    }

    private void createTasks() {
        createTasks(0, 0, 0, 0);
    }

    private void createTasks(int _x0, int _y0, int _x1, int _y1) {
        long x0 = (originX - width / 2) / Configuration.PIXELS_IN_UNIT - 1;
        long y0 = (originY - height / 2) / Configuration.PIXELS_IN_UNIT - 1;
        long x1 = (originX + width / 2) / Configuration.PIXELS_IN_UNIT;
        long y1 = (originY + height / 2) / Configuration.PIXELS_IN_UNIT;

        long ex0 = (_x0 + originX - width / 2) / Configuration.PIXELS_IN_UNIT;
        long ey0 = (_y0 + originY - height / 2) / Configuration.PIXELS_IN_UNIT;
        long ex1 = (_x1 + originX - width / 2) / Configuration.PIXELS_IN_UNIT - 1;
        long ey1 = (_y1 + originY - height / 2) / Configuration.PIXELS_IN_UNIT - 1;

        List<CalculationTask> cached = new LinkedList<>();
        for (long i = x1; i >= x0; i--) {
            boolean h = (i > ex0 && i < ex1);
            for (long j = y1; j >= y0; j--) {
                if (h && j > ey0 && j < ey1) {
                    continue;
                }
                CalculationTask task = new CalculationTask(i, j, zoom, this);
                if (PiecesCache.get(zoom, i, j) != null) {
                    cached.add(task);
                } else {
                    executorService.submit(task);
                }
            }
        }
        cached.stream().forEach(executorService::submit);
    }

    public void paint(int zoom, long x, long y, Image result) {
        try (FastLock.Raii r = lock.read()) {
            if (this.zoom != zoom) {
                return;
            }
            x = x * Configuration.PIXELS_IN_UNIT - originX + width / 2;
            y = y * Configuration.PIXELS_IN_UNIT - originY + height / 2;
            int x0 = (int) Math.max(0, x);
            int y0 = (int) Math.max(0, y);
            int x1 = (int) Math.min(width, x + Configuration.PIXELS_IN_UNIT);
            int y1 = (int) Math.min(height, y + Configuration.PIXELS_IN_UNIT);
            image.getGraphics().drawImage(result, (int) x, (int) y, null);
            notifyImagePaint(x0, y0, x1, y1);
        }
    }

    public void addImagePaintListener(ImagePaintListener listener) {
        try (FastLock.Raii w = lock.write()) {
            listenerList.add(ImagePaintListener.class, listener);
        }
    }

    public void removeImagePaintListener(ImagePaintListener listener) {
        try (FastLock.Raii r = lock.read()) {
            listenerList.remove(ImagePaintListener.class, listener);
        }
    }

    private void notifyImagePaint() {
        for (ImagePaintListener listener : listenerList.getListeners(ImagePaintListener.class)) {
            listener.repaint();
        }
    }

    private void notifyImagePaint(int x0, int y0, int x1, int y1) {
        for (ImagePaintListener listener : listenerList.getListeners(ImagePaintListener.class)) {
            listener.repaint(x0, y0, x1, y1);
        }
    }
}
