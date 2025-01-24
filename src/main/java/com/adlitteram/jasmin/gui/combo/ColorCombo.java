package com.adlitteram.jasmin.gui.combo;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.color.ColorPalette;
import com.adlitteram.jasmin.color.NamedColor;
import com.adlitteram.jasmin.gui.ColorChooserPanel;
import com.adlitteram.jasmin.utils.GuiUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class ColorCombo extends JExtComboBox<NamedColor> implements HierarchyListener {

    private final ColorPalette colorPalette;

    public ColorCombo(ColorPalette colorPalette) {
        this(colorPalette, null);
    }

    public ColorCombo(ColorPalette colorPalette, String actionName) {
        super(colorPalette.getComboBoxModel());

        this.colorPalette = colorPalette;
        setColumnCount(3);
        setMaximumRowCount(12);

        JButton paletteButton = new JButton(Message.get("ColorCombo.Palette"), GuiUtils.loadIcon("Palette.ico"));
        paletteButton.addMouseListener(new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                ColorChooserPanel panel = new ColorChooserPanel(null, ColorCombo.this.colorPalette, (NamedColor) ColorCombo.this.getSelectedItem());
                NamedColor color = panel.getSelectedColor();
                if (color != null) {
                    setSelectedItem(color);
                }
            }
        });

        setLastComponent(paletteButton);
        setRenderer(new ColorComboRenderer());
        addHierarchyListener(this);

    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }

    @Override
    public void setSelectedItem(Object obj) {

        if (obj instanceof NamedColor nc1) {
            for (int i = 0; i < getItemCount(); i++) {
                NamedColor nc2 = getItemAt(i);
                if (nc1.getRGB() == nc2.getRGB() && nc1.getCMYK() == nc2.getCMYK()) {
                    super.setSelectedItem(nc2);
                    setToolTipText(nc2.getDisplayName());
                    return;
                }
            }

            if (nc1.getName().isEmpty()) {
                String name = "r" + nc1.getRed() + "g" + nc1.getGreen() + "b" + nc1.getBlue();
                nc1 = new NamedColor(nc1, name);
            }

            addItem(nc1);
            super.setSelectedItem(nc1);
            setToolTipText(nc1.getDisplayName());

        } else if (obj instanceof Color color) {
            int rgb = color.getRGB();

            for (int i = 0; i < getItemCount(); i++) {
                NamedColor nc = getItemAt(i);
                if (rgb == nc.getRGB()) {
                    super.setSelectedItem(nc);
                    setToolTipText(nc.getDisplayName());
                    return;
                }
            }

            String name = "r" + color.getRed() + "g" + color.getGreen() + "b" + color.getBlue();
            NamedColor nc = new NamedColor(color, name);
            addItem(nc);
            super.setSelectedItem(nc);
            setToolTipText(nc.getDisplayName());
        }

    }

    // Does not work with the METAL L&F ??? (it's a referenced Swing bug)
    // Don't forget to dispose model
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED && !isDisplayable()) {
            colorPalette.removeModel(getModel());
        }

    }
}
