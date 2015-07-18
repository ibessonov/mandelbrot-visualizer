package ibessonov.fractal.gui;

import ibessonov.fractal.events.ImagePaintListener;
import ibessonov.fractal.events.ImageResizeListener;
import ibessonov.fractal.handlers.KeyHandler;
import ibessonov.fractal.handlers.MouseHandler;
import ibessonov.fractal.screen.Screen;
import java.awt.Graphics;
import java.awt.event.KeyListener;

public class MainPanel extends javax.swing.JPanel implements ImagePaintListener {

    private final Screen screen;

    @SuppressWarnings("LeakingThisInConstructor")
    public MainPanel() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);
        initComponents();

        setDoubleBuffered(true); // ?
        setFocusable(true);
        requestFocusInWindow();

        screen = new Screen();
        screen.addImagePaintListener(this);

        MouseHandler mh = new MouseHandler();
        mh.addImageMoveListener(screen);
        mh.addImageZoomListener(screen);

        addMouseListener(mh);
        addMouseWheelListener(mh);
        addMouseMotionListener(mh);
        addImageResizeListener(screen);

        KeyListener kl = new KeyHandler(screen);
        addKeyListener(kl);
    }

    @SuppressWarnings("unchecked")
    private void initComponents() {

        setBackground(new java.awt.Color(255, 255, 255));

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        g.drawImage(screen.getImage(), 0, 0, null);
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        notifyImageResize(width, height);
    }

    public final synchronized void addImageResizeListener(ImageResizeListener listener) {
        listenerList.add(ImageResizeListener.class, listener);
    }

    public final synchronized void removeImageResizeListener(ImageResizeListener listener) {
        listenerList.remove(ImageResizeListener.class, listener);
    }

    private void notifyImageResize(int width, int height) {
        for (ImageResizeListener listener : listenerList.getListeners(ImageResizeListener.class)) {
            listener.imageResized(width, height);
        }
    }
}
