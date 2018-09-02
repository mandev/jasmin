
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.color;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;

public class ColorComboRenderer extends DefaultListCellRenderer {

    protected NamedColor color = new NamedColor(Color.black, "black");
    private Dimension dim ;
    
    public ColorComboRenderer() {
        super();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        g.drawString(color.getName(), dim.height, (int) (dim.height * 0.8));
        g.setColor(color);
        g.fillRoundRect(1, 1, dim.height - 4, dim.height - 4, 6, 6);
        g.setColor(Color.BLACK);
        g.drawRoundRect(1, 1, dim.height - 4, dim.height - 4, 6, 6);
    }

    @Override
    public Component getListCellRendererComponent(JList list,
       Object obj, int row, boolean isSelected, boolean hasFocus) {

        color = (NamedColor) obj;

        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        }
        else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        setToolTipText(color.getDisplayName());
        setFont(list.getFont());
        FontMetrics fontMetrics = getFontMetrics(getFont());
        int height = fontMetrics.getHeight() + 2;
        int width = height + fontMetrics.stringWidth(color.getName()) + 5 ;
        dim = new Dimension(width, height);
        return this;
    }
    
    @Override
    public Dimension getPreferredSize() {
        return dim ;
    }
}
