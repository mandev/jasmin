package com.adlitteram.jasmin.gui.combo;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.color.ColorPalette;
import com.adlitteram.jasmin.color.NamedColor;
import com.adlitteram.jasmin.gui.ColorChooserPanel;
import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.HierarchyEvent;
import java.awt.event.HierarchyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JButton;

public class ColorCombo extends JExtComboBox implements HierarchyListener {

    private ColorPalette colorPalette;

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

        if (obj instanceof NamedColor) {
            NamedColor nc1 = (NamedColor) obj;
            for (int i = 0; i < getItemCount(); i++) {
                NamedColor nc2 = (NamedColor) getItemAt(i);
                if (nc1.getRGB() == nc2.getRGB() && nc1.getCMYK() == nc2.getCMYK()) {
                    super.setSelectedItem(nc2);
                    //setSelectedIndex(i) ;
                    setToolTipText(nc2.getDisplayName());
                    return;
                }
            }

            if (nc1.getName().length() == 0) {
                String name = "r" + nc1.getRed() + "g" + nc1.getGreen() + "b" + nc1.getBlue();
                nc1 = new NamedColor((NamedColor) nc1, name);
            }

            addItem(nc1);
            super.setSelectedItem(nc1);
            setToolTipText(nc1.getDisplayName());

        }
        else if (obj instanceof Color) {
            Color color = (Color) obj;
            int rgb = ((Color) obj).getRGB();

            for (int i = 0; i < getItemCount(); i++) {
                NamedColor nc = (NamedColor) getItemAt(i);
                if (rgb == nc.getRGB()) {
                    //super.setSelectedIndex(i) ;
                    super.setSelectedItem(nc);
                    setToolTipText(nc.getDisplayName());
                    return;
                }
            }

            String name = "r" + color.getRed() + "g" + color.getGreen() + "b" + color.getBlue();
            NamedColor ncolor = new NamedColor(color, name);
            addItem(ncolor);
            super.setSelectedItem(ncolor);
            setToolTipText(ncolor.getDisplayName());
        }

    }

    // Does not work with the METAL L&F ??? (it's a referenced Swing bug)
    // Don't forget to dispose model
    @Override
    public void hierarchyChanged(HierarchyEvent e) {
        if (e.getChangeFlags() == HierarchyEvent.DISPLAYABILITY_CHANGED) {
            if (!isDisplayable()) {
                colorPalette.removeModel(getModel());
            }
        }
    }
}
