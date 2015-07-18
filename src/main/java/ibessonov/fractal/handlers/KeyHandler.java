package ibessonov.fractal.handlers;

import ibessonov.fractal.screen.Screen;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

/**
 *
 * @author Ivan Bessonov
 */
public final class KeyHandler extends KeyAdapter {

    private final Screen screen;

    public KeyHandler(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void keyPressed(KeyEvent ke) {
        if (ke.getKeyChar() != 's') {
            return;
        }
        File file = new File(getFileName());
        try {
            ImageIO.write(screen.getRenderedImage(), getExtension(), file);
        } catch (IOException ex) {
        }
    }

    private synchronized String getFileName() {
        return "screenshot." + getExtension();
    }

    private String getExtension() {
        return "png";
    }
}
