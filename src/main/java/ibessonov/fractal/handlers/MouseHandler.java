package ibessonov.fractal.handlers;

import ibessonov.fractal.screen.Screen;

import java.awt.event.*;

/**
 *
 * @author Ivan Bessonov
 */
public class MouseHandler extends MouseAdapter {

    private int mouseX;
    private int mouseY;
    private boolean mouseCapture = false;

    private final Screen screen;

    public MouseHandler(Screen screen) {
        this.screen = screen;
    }

    @Override
    public void mousePressed(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseCapture = true;
            mouseX = e.getX();
            mouseY = e.getY();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        if (e.getButton() == MouseEvent.BUTTON1) {
            mouseCapture = false;
        }
    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        screen.imageZoomed(mouseX, mouseY, -e.getWheelRotation());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseCapture) {
            screen.imageMoved(mouseX - e.getX(), mouseY - e.getY());
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseCapture) {
            screen.imageMoved(e.getX() - mouseX, e.getY() - mouseY);
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }
}
