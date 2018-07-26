package ibessonov.fractal.application;

import ibessonov.fractal.gui.MainFrame;

import static java.awt.EventQueue.invokeLater;

public class Main {

    public static void main(String args[]) {
        invokeLater(() -> new MainFrame().setVisible(true));
    }
}
