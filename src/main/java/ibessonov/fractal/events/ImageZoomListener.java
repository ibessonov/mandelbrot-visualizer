package ibessonov.fractal.events;

import java.util.EventListener;

/**
 *
 * @author Ivan Bessonov
 */
public interface ImageZoomListener extends EventListener {

    void imageZoomed(int zoom);
}
