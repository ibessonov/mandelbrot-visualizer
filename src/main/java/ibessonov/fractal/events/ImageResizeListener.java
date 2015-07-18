package ibessonov.fractal.events;

import java.util.EventListener;

/**
 *
 * @author Ivan Bessonov
 */
public interface ImageResizeListener extends EventListener {

    void imageResized(int width, int height);
}
