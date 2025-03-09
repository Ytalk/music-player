package com.apolo.gui;

import javax.swing.*;
import javax.swing.plaf.basic.BasicProgressBarUI;
import java.awt.*;

public class CustomProgressBarUI extends BasicProgressBarUI {

    @Override
    protected void paintIndeterminate(Graphics g, JComponent c) {
        super.paintIndeterminate(g, c);
    }

    @Override
    protected void paintDeterminate(Graphics g, JComponent c) {
        super.paintDeterminate(g, c);

        int value = progressBar.getValue();
        int x = (int) ((progressBar.getWidth() - 10) * (value / 100.0));
        int y = progressBar.getHeight() / 2 - 2;

        g.setColor(Color.BLACK);
        g.fillRect(x, y, 5, 5);
    }
}