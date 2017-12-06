/*
 * CMYKChooserPanel.java
 */

package com.adlitteram.jasmin.gui;

import com.adlitteram.jasmin.color.NamedColor;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;
import java.awt.BorderLayout;
import java.awt.Color;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class CMYKChooserPanel extends AbstractColorChooserPanel implements ChangeListener {

    private static final int MAX_VALUE = 255;
    private static final int MIN_VALUE = 0;
    private boolean isAdjusting = false;    // indicates the fields are being set internally
    protected JSpinner blackField;
    protected JLabel blackLabel;
    protected JSlider blackSlider;
    protected JSpinner cyanField;
    protected JLabel cyanLabel;
    protected JSlider cyanSlider;
    protected JSpinner magentaField;
    protected JLabel magentaLabel;
    protected JSlider magentaSlider;
    protected JSpinner yellowField;
    protected JLabel yellowLabel;
    protected JSlider yellowSlider;

    public CMYKChooserPanel() {
        super();
    }

    private void setColor(Color color) {

        NamedColor nColor = (color instanceof NamedColor) ? (NamedColor) color : new NamedColor(color);

        int c = nColor.getCyan();
        int m = nColor.getMagenta();
        int y = nColor.getYellow();
        int k = nColor.getBlack();

        cyanLabel.setText(String.valueOf(Math.round(1000 * c / 255f) / 10f + " %"));
        magentaLabel.setText(String.valueOf(Math.round(1000 * m / 255f) / 10f + " %"));
        yellowLabel.setText(String.valueOf(Math.round(1000 * y / 255f) / 10f + " %"));
        blackLabel.setText(String.valueOf(Math.round(1000 * k / 255f) / 10f + " %"));

        if (cyanSlider.getValue() != c) cyanSlider.setValue(c);
        if (magentaSlider.getValue() != m) magentaSlider.setValue(m);
        if (yellowSlider.getValue() != y) yellowSlider.setValue(y);
        if (blackSlider.getValue() != k) blackSlider.setValue(k);

        if (((Integer) cyanField.getValue()).intValue() != c)
            cyanField.setValue(Integer.valueOf(c));
        if (((Integer) magentaField.getValue()).intValue() != m)
            magentaField.setValue(Integer.valueOf(m));
        if (((Integer) yellowField.getValue()).intValue() != y)
            yellowField.setValue(Integer.valueOf(y));
        if (((Integer) blackField.getValue()).intValue() != k)
            blackField.setValue(Integer.valueOf(k));
    }

    public String getDisplayName() {
        return "CMYK";
    }

    @Override
    public int getMnemonic() {
        return -1;
    }

    @Override
    public int getDisplayedMnemonicIndex() {
        return -1;
    }

    public Icon getSmallDisplayIcon() {
        return null;
    }

    public Icon getLargeDisplayIcon() {
        return null;
    }

    /**
     * The background color, foreground color, and font are already set to the
     * defaults from the defaults table before this method is called.
     */
    @Override
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    protected void buildChooser() {

        Color color = getColorFromModel();
        NamedColor nColor = (color instanceof NamedColor) ? (NamedColor) color : new NamedColor(color);
        int c = nColor.getCyan();
        int m = nColor.getMagenta();
        int y = nColor.getYellow();
        int k = nColor.getBlack();

        int w0[] = {10, 0, 5, 0, 10, 0, 10, 0, 10};
        int h0[] = {10, 0, 0, 0, 0, 0, 0, 0, 10};

        HIGLayout l0 = new HIGLayout(w0, h0);
        HIGConstraints c0 = new HIGConstraints();
        l0.setColumnWeight(4, 1);
        l0.setRowWeight(1, 1);
        l0.setRowWeight(9, 1);

        JPanel enclosure = new JPanel(l0);

        // The row for the red value
        JLabel cl = new JLabel("Cyan");
        cl.setDisplayedMnemonic(1);
        enclosure.add(cl, c0.xy(2, 2, "l"));
        cyanSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, c);
        cyanSlider.setMajorTickSpacing(85);
        cyanSlider.setMinorTickSpacing(17);
        cyanSlider.setPaintTicks(true);
        cyanSlider.setPaintLabels(true);
        enclosure.add(cyanSlider, c0.xy(4, 2, "lr"));
        cyanField = new JSpinner(new SpinnerNumberModel(c, MIN_VALUE, MAX_VALUE, 1));
        cyanField.addChangeListener(this);
        enclosure.add(cyanField, c0.xy(6, 2, "l"));
        cyanLabel = new JLabel(String.valueOf(Math.round(c / 255f * 100f) + " %"));
        enclosure.add(cyanLabel, c0.xy(8, 2, "l"));

        // The row for the green value
        JLabel ml = new JLabel("Magenta");
        ml.setDisplayedMnemonic(1);
        enclosure.add(ml, c0.xy(2, 4, "l"));
        magentaSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, m);
        magentaSlider.setMajorTickSpacing(85);
        magentaSlider.setMinorTickSpacing(17);
        magentaSlider.setPaintTicks(true);
        magentaSlider.setPaintLabels(true);
        enclosure.add(magentaSlider, c0.xy(4, 4, "lr"));
        magentaField = new JSpinner(new SpinnerNumberModel(m, MIN_VALUE, MAX_VALUE, 1));
        magentaField.addChangeListener(this);
        enclosure.add(magentaField, c0.xy(6, 4, "l"));
        magentaLabel = new JLabel(String.valueOf(Math.round(m / 255f * 100f) + " %"));
        enclosure.add(magentaLabel, c0.xy(8, 4, "l"));

        // The slider for the blue value
        JLabel yl = new JLabel("Yellow");
        yl.setDisplayedMnemonic(1);
        enclosure.add(yl, c0.xy(2, 6, "l"));
        yellowSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, y);
        yellowSlider.setMajorTickSpacing(85);
        yellowSlider.setMinorTickSpacing(17);
        yellowSlider.setPaintTicks(true);
        yellowSlider.setPaintLabels(true);
        enclosure.add(yellowSlider, c0.xy(4, 6, "lr"));
        yellowField = new JSpinner(new SpinnerNumberModel(y, MIN_VALUE, MAX_VALUE, 1));
        yellowField.addChangeListener(this);
        enclosure.add(yellowField, c0.xy(6, 6, "l"));
        yellowLabel = new JLabel(String.valueOf(Math.round(y / 255f * 100f) + " %"));
        enclosure.add(yellowLabel, c0.xy(8, 6, "l"));

        // The slider for the blue value
        JLabel kl = new JLabel("Black");
        kl.setDisplayedMnemonic(1);
        enclosure.add(kl, c0.xy(2, 8, "l"));
        blackSlider = new JSlider(JSlider.HORIZONTAL, 0, 255, k);
        blackSlider.setMajorTickSpacing(85);
        blackSlider.setMinorTickSpacing(17);
        blackSlider.setPaintTicks(true);
        blackSlider.setPaintLabels(true);
        enclosure.add(blackSlider, c0.xy(4, 8, "lr"));
        blackField = new JSpinner(new SpinnerNumberModel(k, MIN_VALUE, MAX_VALUE, 1));
        blackField.addChangeListener(this);
        enclosure.add(blackField, c0.xy(6, 8, "l"));
        blackLabel = new JLabel(String.valueOf(Math.round(k / 255f * 100f) + " %"));
        enclosure.add(blackLabel, c0.xy(8, 8, "l"));

        cyanSlider.addChangeListener(this);
        magentaSlider.addChangeListener(this);
        yellowSlider.addChangeListener(this);
        blackSlider.addChangeListener(this);

        cyanSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        magentaSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        yellowSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        blackSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);

        setLayout(new BorderLayout());
        add(enclosure, BorderLayout.CENTER);
    }

    @Override
    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
    }

    public void updateChooser() {
        if (!isAdjusting) {
            isAdjusting = true;
            setColor(getColorFromModel());
            isAdjusting = false;
        }
    }

    public void stateChanged(ChangeEvent e) {
        if ((e.getSource() instanceof JSlider) && !isAdjusting) {

            int c = cyanSlider.getValue();
            int m = magentaSlider.getValue();
            int y = yellowSlider.getValue();
            int k = blackSlider.getValue();

            NamedColor nc = NamedColor.buildCmykColor(null, c, m, y, k);
            getColorSelectionModel().setSelectedColor(nc);
        }
        else if ((e.getSource() instanceof JSpinner) && !isAdjusting) {

            int c = ((Integer) cyanField.getValue()).intValue();
            int m = ((Integer) magentaField.getValue()).intValue();
            int y = ((Integer) yellowField.getValue()).intValue();
            int k = ((Integer) blackField.getValue()).intValue();

            NamedColor nc = NamedColor.buildCmykColor(null, c, m, y, k);
            getColorSelectionModel().setSelectedColor(nc);
        }
    }
}
