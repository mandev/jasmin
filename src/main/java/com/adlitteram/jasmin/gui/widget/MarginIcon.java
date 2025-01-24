package com.adlitteram.jasmin.gui.widget;

import javax.swing.*;
import java.awt.*;

public class MarginIcon implements Icon {

    private final Insets margin;
    private final Icon icon;

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
