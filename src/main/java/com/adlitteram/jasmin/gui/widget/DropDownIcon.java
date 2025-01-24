package com.adlitteram.jasmin.gui.widget;

import javax.swing.*;
import java.awt.*;

public class DropDownIcon implements Icon {

    @Override
    public void paintIcon(Component component, Graphics g, int x, int y) {
        JComponent c = (JComponent) component;
        int iconWidth = getIconWidth();

        g.translate(x, y);

        g.setColor(c.isEnabled() ? UIManager.getColor("controlText") : UIManager.getColor("controlDkShadow"));

        g.drawLine(1, 1, 1 + (iconWidth - 3), 1);
        g.drawLine(2, 2, 2 + (iconWidth - 5), 2);
        g.drawLine(3, 3, 3 + (iconWidth - 7), 3);
        g.drawLine(4, 4, 4 + (iconWidth - 9), 4);

        g.translate(-x, -y);
    }

    @Override
    public int getIconWidth() {
        return 10;
    }

    @Override
    public int getIconHeight() {
        return 4;
    }
}
