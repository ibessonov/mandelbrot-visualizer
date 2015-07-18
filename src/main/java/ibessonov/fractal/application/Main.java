package ibessonov.fractal.application;

import ibessonov.fractal.gui.MainFrame;

public class Main {

    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(() -> {
            new MainFrame().setVisible(true);
        });
    }
}
