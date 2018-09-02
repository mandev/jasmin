package com.adlitteram.jasmin.gui.widget;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import javax.swing.Icon;

public class MarginIcon implements Icon {

    private Insets margin;
    private Icon icon;

    public MarginIcon(Insets margin, Icon icon) {
        this.margin = margin;
        this.icon = icon;
    }

    @Override
    public void paintIcon(Component c, Graphics g, int x, int y) {
        icon.paintIcon(c, g, x + margin.left, y + margin.top);
    }

    @Override
    public int getIconWidth() {
        return margin.left + icon.getIconWidth() + margin.right;
    }

    @Override
    public int getIconHeight() {
        return margin.top + icon.getIconHeight() + margin.bottom;
    }
}
