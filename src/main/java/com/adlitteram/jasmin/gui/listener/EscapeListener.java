package com.adlitteram.jasmin.gui.listener;

import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class EscapeListener extends KeyAdapter {

    private final Window window;

    public EscapeListener(Window window) {
        this.window = window;
    }

    @Override
    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            window.dispose();
            e.consume();
        }
    }
}
