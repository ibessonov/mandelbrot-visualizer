package ibessonov.fractal.events;

import java.util.EventListener;

/**
 *
 * @author Ivan Bessonov
 */
public interface ImagePaintListener extends EventListener {

    void repaint();

    void repaint(int x0, int y0, int x1, int y1);
}
