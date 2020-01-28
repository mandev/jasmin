package com.adlitteram.jasmin.gui.widget;

import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;
import java.util.ArrayList;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.UIManager;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class JSpinSlider extends JPanel implements ChangeListener {

    private final ArrayList<ChangeListener> listeners = new ArrayList<>();
    private final JSlider slider;
    private final JSpinner spinner;
    private int value;
    private final int min;
    private final int max;

    public JSpinSlider(int value, int min, int max, int step, int tick, int gap) {

        this.value = value;
        this.min = min;
        this.max = max;

        UIManager.put("Slider.focus", getBackground());
        slider = new JSlider(FloatJSlider.HORIZONTAL, min, max, value);
        slider.addChangeListener(this);

        spinner = new JSpinner(new SpinnerNumberModel(value, min, max, step));
        spinner.addChangeListener(this);

        int col = String.valueOf(Math.round(max)).length() + 2;
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(col);

        HIGLayout layout = new HIGLayout(new int[]{100, gap, 0}, new int[]{0});
        HIGConstraints constraints = new HIGConstraints();
        layout.setColumnWeight(1, 1);

        setLayout(layout);
        add(slider, constraints.xy(1, 1));
        add(spinner, constraints.xy(3, 1, "ltr"));
    }

    public JSlider getSlider() {
        return slider;
    }

    public JSpinner getSpinner() {
        return spinner;
    }

    public void addChangeListener(ChangeListener listener) {
        if (!listeners.contains(listener)) {
            listeners.add(listener);
        }
    }

    public void removeChangeListener(ChangeListener listener) {
        listeners.remove(listener);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
        int val = clamp(min, (e.getSource() instanceof JSlider) ? slider.getValue() : ((Number) spinner.getValue()).intValue(), max);
        if (val != value) {
            value = val;
            slider.removeChangeListener(this);
            spinner.removeChangeListener(this);

            slider.setValue(value);
            spinner.setValue(value);
            ChangeEvent ce = new ChangeEvent(this);
            listeners.forEach(listener -> listener.stateChanged(ce));
            slider.addChangeListener(this);     // Last in First out
            spinner.addChangeListener(this);    // Last in First out
        }
    }

    @Override
    public void setEnabled(boolean b) {
        super.setEnabled(b);
        spinner.setEnabled(b);
        slider.setEnabled(b);
    }

    public int getValue() {
        return slider.getValue();
    }

    public void setValue(int value) {
        slider.setValue(value);
    }

    static private int clamp(int min, int val, int max) {
        return (val < min) ? min : (val > max) ? max : val;
    }
}
