package com.adlitteram.jasmin.gui;

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

class CMYChooserPanel extends AbstractColorChooserPanel implements ChangeListener {

    private static final int MAX_VALUE = 255;
    private static final int MIN_VALUE = 0;
    private boolean isAdjusting = false;    // indicates the fields are being set internally
    protected JSpinner cyanField;
    protected JLabel cyanLabel;
    protected JSlider cyanSlider;
    protected JSpinner magentaField;
    protected JLabel magentaLabel;
    protected JSlider magentaSlider;
    protected JSpinner yellowField;
    protected JLabel yellowLabel;
    protected JSlider yellowSlider;

    public CMYChooserPanel() {
        super();
    }

    private void setColor(Color newColor) {
        int c = 255 - newColor.getRed();
        int m = 255 - newColor.getGreen();
        int y = 255 - newColor.getBlue();

        cyanLabel.setText(String.valueOf(Math.round(1000 * c / 255f) / 10f + " %"));
        magentaLabel.setText(String.valueOf(Math.round(1000 * m / 255f) / 10f + " %"));
        yellowLabel.setText(String.valueOf(Math.round(1000 * y / 255f) / 10f + " %"));

        if (cyanSlider.getValue() != c) {
            cyanSlider.setValue(c);
        }

        if (magentaSlider.getValue() != m) {
            magentaSlider.setValue(m);
        }

        if (yellowSlider.getValue() != y) {
            yellowSlider.setValue(y);
        }

        if (((Integer) cyanField.getValue()) != c) {
            cyanField.setValue(c);
        }

        if (((Integer) magentaField.getValue()) != m) {
            magentaField.setValue(m);
        }

        if (((Integer) yellowField.getValue()) != y) {
            yellowField.setValue(y);
        }
    }

    @Override
    public String getDisplayName() {
        return "CMY";
    }

    @Override
    public int getMnemonic() {
        return -1;
    }

    @Override
    public int getDisplayedMnemonicIndex() {
        return -1;
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
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

    @Override
    protected void buildChooser() {

        Color color = getColorFromModel();
        int c = 255 - color.getRed();
        int m = 255 - color.getGreen();
        int y = 255 - color.getBlue();

        int w0[] = {10, 0, 5, 0, 10, 0, 10, 0, 10};
        int h0[] = {10, 0, 0, 0, 0, 0, 10};
        HIGLayout l0 = new HIGLayout(w0, h0);
        HIGConstraints c0 = new HIGConstraints();
        l0.setColumnWeight(4, 1);
        l0.setRowWeight(1, 1);
        l0.setRowWeight(7, 1);

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
        cyanLabel = new JLabel(String.valueOf(Math.round(c / 255 * 100) + " %"));
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
        magentaLabel = new JLabel(String.valueOf(Math.round(m / 255 * 100) + " %"));
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
        yellowLabel = new JLabel(String.valueOf(Math.round(y / 255 * 100) + " %"));
        enclosure.add(yellowLabel, c0.xy(8, 6, "l"));

        cyanSlider.addChangeListener(this);
        magentaSlider.addChangeListener(this);
        yellowSlider.addChangeListener(this);

        cyanSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        magentaSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);
        yellowSlider.putClientProperty("JSlider.isFilled", Boolean.TRUE);

        setLayout(new BorderLayout());
        add(enclosure, BorderLayout.CENTER);
    }

    @Override
    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
    }

    @Override
    public void updateChooser() {
        if (!isAdjusting) {
            isAdjusting = true;
            setColor(getColorFromModel());
            isAdjusting = false;
        }
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        if ((e.getSource() instanceof JSlider) && !isAdjusting) {
            int red = 255 - cyanSlider.getValue();
            int green = 255 - magentaSlider.getValue();
            int blue = 255 - yellowSlider.getValue();
            getColorSelectionModel().setSelectedColor(new Color(red, green, blue));
        }
        else if ((e.getSource() instanceof JSpinner) && !isAdjusting) {
            int c = ((Integer) cyanField.getValue());
            int m = ((Integer) magentaField.getValue());
            int y = ((Integer) yellowField.getValue());
            int red = 255 - c;
            int green = 255 - m;
            int blue = 255 - y;
            getColorSelectionModel().setSelectedColor(new Color(red, green, blue));
        }
    }
}
