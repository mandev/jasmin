package com.adlitteram.jasmin.gui.widget;

import javax.swing.*;
import javax.swing.plaf.UIResource;
import java.awt.*;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.Enumeration;
import java.util.Hashtable;

public class FloatJSlider extends JSlider {

    static final float FLOAT_MINIMUM = 0.0f;
    static final float FLOAT_MAXIMUM = 100.0f;
    static final float FLOAT_MIDDLE = 50.0f;
    static final int PRECISION_MULTIPLIER = 100;

    public FloatJSlider() {
        super();
        setFloatMinimum(FLOAT_MINIMUM);
        setFloatMaximum(FLOAT_MAXIMUM);
        setFloatValue(FLOAT_MIDDLE);
    }

    public FloatJSlider(int orientation, float min, float max, float val) {
        super(orientation);
        setFloatMinimum(min);
        setFloatMaximum(max);
        setFloatValue(val);
    }

    public float getFloatMaximum() {
        return ((float) getMaximum() / (float) PRECISION_MULTIPLIER);
    }

    public float getFloatMinimum() {
        return ((float) getMinimum() / (float) PRECISION_MULTIPLIER);
    }

    public float getFloatValue() {
        return ((float) getValue() / (float) PRECISION_MULTIPLIER);
    }

    public final void setFloatMaximum(float max) {
        setMaximum(Math.round(max * PRECISION_MULTIPLIER));
    }

    public final void setFloatMinimum(float min) {
        setMinimum(Math.round(min * PRECISION_MULTIPLIER));
    }

    public final void setFloatValue(float val) {
        setValue(Math.round(val * PRECISION_MULTIPLIER));
        setToolTipText(Float.toString(val));
    }

    public void setFloatMajorTickSpacing(float val) {
        setMajorTickSpacing(Math.round(val * PRECISION_MULTIPLIER));
    }

    public void setFloatMinorTickSpacing(float val) {
        setMinorTickSpacing(Math.round(val * PRECISION_MULTIPLIER));
    }

    @Override
    public Hashtable createStandardLabels(int increment, int start) {
        if (start > getMaximum() || start < getMinimum()) {
            throw new IllegalArgumentException("Slider label start point out of range.");
        }

        if (increment <= 0) {
            throw new IllegalArgumentException("Label incremement must be > 0");
        }

        class SmartHashtable extends Hashtable implements PropertyChangeListener {

            int increment = 0;
            int start = 0;
            boolean startAtMin = false;

            class LabelUIResource extends JLabel implements UIResource {

                public LabelUIResource(String text, int alignment) {
                    super(text, alignment);
                    setName("Slider.label");
                }

                @Override
                public Font getFont() {
                    Font font = super.getFont();
                    if (font != null && !(font instanceof UIResource)) {
                        return font;
                    }
                    return FloatJSlider.this.getFont();
                }

                @Override
                public Color getForeground() {
                    Color fg = super.getForeground();
                    if (fg != null && !(fg instanceof UIResource)) {
                        return fg;
                    }
                    if (!(FloatJSlider.this.getForeground() instanceof UIResource)) {
                        return FloatJSlider.this.getForeground();
                    }
                    return fg;
                }
            }

            public SmartHashtable(int increment, int start) {
                super();
                this.increment = increment;
                this.start = start;
                startAtMin = start == getMinimum();
                createLabels();
            }

            @Override
            public void propertyChange(PropertyChangeEvent e) {
                if (e.getPropertyName().equals("minimum") && startAtMin) {
                    start = getMinimum();
                }

                if (e.getPropertyName().equals("minimum")
                        || e.getPropertyName().equals("maximum")) {

                    Enumeration keys = getLabelTable().keys();
                    Object key = null;
                    Hashtable hashtable = new Hashtable();

                    // Save the labels that were added by the developer
                    while (keys.hasMoreElements()) {
                        key = keys.nextElement();
                        Object value = getLabelTable().get(key);
                        if (!(value instanceof LabelUIResource)) {
                            hashtable.put(key, value);
                        }
                    }

                    clear();
                    createLabels();

                    // Add the saved labels
                    keys = hashtable.keys();
                    while (keys.hasMoreElements()) {
                        key = keys.nextElement();
                        put(key, hashtable.get(key));
                    }

                    ((JSlider) e.getSource()).setLabelTable(this);
                }
            }

            void createLabels() {
                for (int labelIndex = start; labelIndex <= getMaximum(); labelIndex += increment) {
                    put(labelIndex, new LabelUIResource(String.valueOf((float) labelIndex / (float) PRECISION_MULTIPLIER), JLabel.CENTER));
                }
            }
        }

        SmartHashtable table = new SmartHashtable(increment, start);

        if (getLabelTable() != null && (getLabelTable() instanceof PropertyChangeListener)) {
            removePropertyChangeListener((PropertyChangeListener) getLabelTable());
        }

        addPropertyChangeListener(table);

        return table;
    }
}
