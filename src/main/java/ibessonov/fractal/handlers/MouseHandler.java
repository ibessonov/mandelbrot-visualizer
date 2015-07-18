package ibessonov.fractal.handlers;

import ibessonov.fractal.events.ImageMoveListener;
import ibessonov.fractal.events.ImageZoomListener;
import java.awt.event.*;
import javax.swing.event.EventListenerList;

/**
 *
 * @author Ivan Bessonov
 */
public class MouseHandler implements MouseListener, MouseWheelListener,
        MouseMotionListener {

    //<editor-fold defaultstate="collapsed" desc="Empty mouse events handlers">
    @Override
    public void mouseClicked(MouseEvent e) {
    }
    
    @Override
    public void mouseEntered(MouseEvent e) {
    }
    
    @Override
    public void mouseExited(MouseEvent e) {
    }
    //</editor-fold>

    private int mouseX;
    private int mouseY;
    private boolean mouseCapture = false;
    private final EventListenerList listenerList = new EventListenerList();

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
        notifyImageZoom(-e.getWheelRotation());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (mouseCapture) {
            notifyImageMove(mouseX - e.getX(), mouseY - e.getY());
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        if (mouseCapture) {
            notifyImageMove(e.getX() - mouseX, e.getY() - mouseY);
        }
        mouseX = e.getX();
        mouseY = e.getY();
    }

    public synchronized void addImageMoveListener(ImageMoveListener listener) {
        listenerList.add(ImageMoveListener.class, listener);
    }

    public synchronized void removeImageMoveListener(ImageMoveListener listener) {
        listenerList.remove(ImageMoveListener.class, listener);
    }

    private void notifyImageMove(int dx, int dy) {
        for (ImageMoveListener listener
                : listenerList.getListeners(ImageMoveListener.class)) {
            listener.imageMoved(dx, dy);
        }
    }

    public synchronized void addImageZoomListener(ImageZoomListener listener) {
        listenerList.add(ImageZoomListener.class, listener);
    }

    public synchronized void removeImageZoomListener(ImageZoomListener listener) {
        listenerList.remove(ImageZoomListener.class, listener);
    }

    private void notifyImageZoom(int zoom) {
        for (ImageZoomListener listener
                : listenerList.getListeners(ImageZoomListener.class)) {
            listener.imageZoomed(zoom);
        }
    }
}
