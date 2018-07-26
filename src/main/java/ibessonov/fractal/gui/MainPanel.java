package ibessonov.fractal.gui;

import ibessonov.fractal.handlers.KeyHandler;
import ibessonov.fractal.handlers.MouseHandler;
import ibessonov.fractal.screen.Screen;

import java.awt.*;
import java.awt.event.KeyListener;

public class MainPanel extends javax.swing.JPanel {

    private final Screen screen;

    public MainPanel() {
        Thread.currentThread().setPriority(Thread.MAX_PRIORITY);

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

        screen = new Screen(this);

//        setDoubleBuffered(true); // ?
        setFocusable(true);
        requestFocusInWindow();

        MouseHandler mh = new MouseHandler(screen);

        addMouseListener(mh);
        addMouseWheelListener(mh);
        addMouseMotionListener(mh);

        KeyListener kl = new KeyHandler(screen);
        addKeyListener(kl);
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

    private void notifyImageResize(int width, int height) {
        screen.imageResized(width, height);
    }
}
