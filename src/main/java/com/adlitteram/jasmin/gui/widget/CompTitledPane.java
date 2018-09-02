/* (swing1.1.1) */
//package jp.gr.java_conf.tame.swing.panel;
package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.gui.layout.VerticalLayout;
import java.awt.Component;
import java.awt.Container;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import javax.swing.BorderFactory;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class CompTitledPane extends JPanel {

    protected CompTitledBorder border;
    protected JComponent component;
    protected JPanel panel;
    private ItemListener childrenListener = new ItemListener() {

        @Override
        public void itemStateChanged(ItemEvent e) {
            setChildrenEnable(panel, (e.getStateChange() == ItemEvent.SELECTED));
        }
    };

    private void setChildrenEnable(Component cmp, boolean enable) {
        cmp.setEnabled(enable);
        if (cmp instanceof Container) {
            Component[] c = ((Container) cmp).getComponents();
            for (int i = 0; i < c.length; i++) {
                setChildrenEnable(c[i], enable);
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
        if (!component.isSelected()) setChildrenEnable(panel, false);
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
