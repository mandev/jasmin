
package com.adlitteram.jasmin.gui.explorer;

import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.UIManager;

public class ArrowIcon implements Icon {
    private boolean descending;
    private int size;

    public ArrowIcon(boolean descending, int size) {
        this.descending = descending;
        this.size = size;
    }

    @Override
    public void paintIcon(Component component, Graphics g, int x, int y) {
        JComponent c = (JComponent) component;
        g.setColor(c.isEnabled() ? UIManager.getColor("controlText") : UIManager.getColor("controlDkShadow"));

        int s = (size % 2 == 0) ? size + 1 : size;
        int s2 = s / 2;
        int s3 = s / 3;

        g.translate(x, y + s3);
        for (int i = 0; i < s2 + 1; i++) {
            int j = descending ? i : s2 - i;
            g.drawLine(i, j, s - i, j);
        }
        g.translate(-x, -y - s3);
    }

    @Override
    public int getIconWidth() {
        return size;
    }

    @Override
    public int getIconHeight() {
        return size;
    }
}
