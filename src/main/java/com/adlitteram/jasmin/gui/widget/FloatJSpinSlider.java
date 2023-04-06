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

public class FloatJSpinSlider extends JPanel implements ChangeListener {

    private final ArrayList<ChangeListener> listeners = new ArrayList<>();
    private FloatJSlider slider;
    private JSpinner spinner;
    private float value;
    private float min;
    private float max;

    public FloatJSpinSlider(float value, float min, float max, float step) {
        this(value, min, max, step, (min - max) / 10f, 5);
    }

    public FloatJSpinSlider(float value, float min, float max, float step, float tick, int gap) {

        this.value = value;
        this.min = min;
        this.max = max;

        UIManager.put("Slider.focus", getBackground());
        slider = new FloatJSlider(JSlider.HORIZONTAL, min, max, value);
        slider.addChangeListener(this);
        spinner = new JSpinner(new SpinnerNumberModel(Float.valueOf(value), Float.valueOf(min - step + .0001f), Float.valueOf(max + step - .0001f), Float.valueOf(step)));
        spinner.addChangeListener(this);

        int col = Math.max(String.valueOf(Math.round(max)).length() + 1, 4);
        ((JSpinner.DefaultEditor) spinner.getEditor()).getTextField().setColumns(col);

        HIGLayout l = new HIGLayout(new int[]{100, gap, 0}, new int[]{0});
        HIGConstraints c = new HIGConstraints();
        l.setColumnWeight(1, 1);

        setLayout(l);
        add(slider, c.xy(1, 1));
        add(spinner, c.xy(3, 1, "ltr"));
    }

    public FloatJSlider getSlider() {
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
        float val = clamp(min, (e.getSource() instanceof JSlider) ? slider.getFloatValue() : ((Number) spinner.getValue()).floatValue(), max);
        if (val != value) {
            value = val;
            slider.removeChangeListener(this);
            spinner.removeChangeListener(this);

            slider.setFloatValue(value);
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

    public float getFloatValue() {
        return slider.getFloatValue();
    }

    public void setFloatValue(float value) {
        slider.setFloatValue(value);
    }

    static private float clamp(float min, float val, float max) {
        return (val < min) ? min : (val > max) ? max : val;
    }
}
