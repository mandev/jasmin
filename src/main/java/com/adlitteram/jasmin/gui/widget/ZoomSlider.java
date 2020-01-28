package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.utils.NumUtils;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class ZoomSlider extends JToolBar implements ChangeListener, Registrable {

    private JSlider slider;
    private JLabel label;
    private Action zoomAction;

    public ZoomSlider(Action zoomAction) {
        this(zoomAction, null, null);
    }

    public ZoomSlider(Action zoomAction, Action zoomInAction, Action zoomOutAction) {
        this.zoomAction = zoomAction;

        // Gnome Settings
        UIManager.put("Slider.paintValue", Boolean.FALSE);

        setOpaque(false);
        setFloatable(false);
        setFocusable(false);
        setBorder(null);
        setBorderPainted(false);
        setRollover(true);

        if (zoomOutAction != null) {
            JButton zoomOutButton = new JButton(zoomOutAction);
            zoomOutButton.setFocusPainted(false);
            zoomOutButton.setFocusable(false);
            zoomOutButton.setRolloverEnabled(true);
            zoomOutButton.setText(null);
            zoomOutButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            zoomOutButton.setOpaque(false);
            add(zoomOutButton);
        }

        slider = new JSlider(JSlider.HORIZONTAL, lg(10), lg(1000), lg(100));
        slider.setPaintTicks(false);
        slider.setPaintLabels(false);
        slider.setPaintTrack(true);
        slider.setOpaque(false);
        slider.setPreferredSize(new Dimension(100, 20));
        slider.addChangeListener(this);
        add(slider);

        if (zoomInAction != null) {
            JButton zoomInButton = new JButton(zoomInAction);
            zoomInButton.setFocusPainted(false);
            zoomInButton.setFocusable(false);
            zoomInButton.setRolloverEnabled(true);
            zoomInButton.setText(null);
            zoomInButton.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
            zoomInButton.setOpaque(false);
            add(zoomInButton);
        }

        add(javax.swing.Box.createHorizontalStrut(5));

        label = new JLabel();
        label.setPreferredSize(new Dimension(60, 20));
        add(label);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        JSlider source = (JSlider) e.getSource();
        double value = (double) source.getValue();
        zoomAction.actionPerformed(new ActionEvent(this, (int) value, "stateChanged"));
    }

    @Override
    public Action getAction() {
        return zoomAction;
    }

    public int getValue() {
        return ex(slider.getValue());
    }

    @Override
    public void update(Object object) {
        String val = (String) object;

        int i = val.indexOf("%");
        int scale = (i < 0) ? NumUtils.intValue(val, 100) : NumUtils.intValue(val.substring(0, i), 100);
        scale = NumUtils.clamp(10, scale, 1000);

        slider.removeChangeListener(this);
        slider.setValue(lg(scale));
        label.setText(Math.round(scale) + "%");
        slider.addChangeListener(this);
    }

    private int lg(double d) {
        return Math.round(Math.round(Math.log(d) / Math.log(1000) * 125d));
    }

    private int ex(double d) {
        return Math.round(Math.round(Math.exp(d * Math.log(1000) / 125d)));
    }

    @Override
    public Dimension getMaximumSize() {
        return getPreferredSize();
    }
}
