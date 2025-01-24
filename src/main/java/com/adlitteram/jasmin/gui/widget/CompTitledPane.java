package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.gui.layout.VerticalLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

public class CompTitledPane extends JPanel {

    protected CompTitledBorder border;
    protected JComponent component;
    protected JPanel panel;

    private void setChildrenEnable(Component cmp, boolean enable) {
        cmp.setEnabled(enable);
        if (cmp instanceof Container container) {
            for (Component c : container.getComponents()) {
                setChildrenEnable(c, enable);
            }
        }
    }

    public CompTitledPane(JComponent component, JPanel panel) {
        setLayout(new VerticalLayout(-2, 0, 1));
        this.component = component;
        this.component.setOpaque(true);
        this.panel = panel;

        // $ed
        component.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        border = new CompTitledBorder(BorderFactory.createEtchedBorder(), component);
        setBorder(border);
        add(component);
        add(panel);
    }

    public CompTitledPane(JCheckBox component, JPanel panel) {
        setLayout(new VerticalLayout(-2, 0, 1));
        this.component = component;
        this.panel = panel;
        this.component.setOpaque(true);

        // $ed
        component.setBorder(BorderFactory.createEmptyBorder(0, 0, 0, 0));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 0, 0, 0));

        border = new CompTitledBorder(BorderFactory.createEtchedBorder(), component);
        setBorder(border);
        add(component);
        add(panel);
        if (!component.isSelected()) {
            setChildrenEnable(panel, false);
        }
        ItemListener childrenListener = e -> setChildrenEnable(panel, (e.getStateChange() == ItemEvent.SELECTED));
        component.addItemListener(childrenListener);
    }

    @Override
    public void doLayout() {
        Insets insets = getInsets();
        Rectangle rect = getBounds();
        rect.x = 0;
        rect.y = 0;

        Rectangle compR = border.getComponentRect(rect, insets);
        component.setBounds(compR);
        rect.x += insets.left;
        rect.y += insets.top;
        rect.width -= insets.left + insets.right;
        rect.height -= insets.top + insets.bottom;
        panel.setBounds(rect);
    }
}
