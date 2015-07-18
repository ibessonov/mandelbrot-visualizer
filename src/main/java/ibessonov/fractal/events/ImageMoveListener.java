package ibessonov.fractal.events;

import java.util.EventListener;

/**
 *
 * @author Ivan Bessonov
 */
public interface ImageMoveListener extends EventListener {

    void imageMoved(int dx, int dy);
}
