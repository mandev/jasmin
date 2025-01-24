package com.adlitteram.jasmin;

import com.adlitteram.jasmin.gui.XAbstractMenu;
import com.adlitteram.jasmin.gui.widget.Registrable;

import javax.swing.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class WidgetManager {

    // Map an action to 0-n widgets
    private final HashMap<String, ArrayList<JComponent>> widgetsMap = new HashMap<>();
    private final Object source;

    public WidgetManager(Object source) {
        this.source = source;
    }

    public JComponent[] getWidgets(String action) {
        ArrayList<JComponent> widgets = widgetsMap.get(action);
        if (widgets == null) {
            return new JComponent[0];
        }
        return widgets.toArray(new JComponent[0]);
    }

    // Register a widget-action (n widgets => 1 action )
    public void registerWidget(JComponent widget, String action) {
        List<JComponent> widgetList = widgetsMap.computeIfAbsent(action, k -> new ArrayList<>());
        widget.putClientProperty("SourceObject", source);
        widgetList.add(widget);
    }

    public void registerWidget(AbstractButton widget) {
        registerWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    public void registerWidget(JComboBox<?> widget) {
        registerWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    public void unregisterWidget(JComponent widget, String action) {
        ArrayList<JComponent> widgetList = widgetsMap.get(action);
        if (widgetList != null) {
            widgetList.remove(widget);
        }
    }

    public void unregisterWidget(AbstractButton widget) {
        unregisterWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    public void unregisterWidget(JComboBox<?> widget) {
        unregisterWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    // Tell a widget that it should change
    public void updateWidgets(String action, Object value) {
        ArrayList<JComponent> widgetList = widgetsMap.get(action);
        if (widgetList == null) {
            return;
        }

        for (JComponent obj : widgetList) {
            if (obj instanceof XAbstractMenu menu) {
                menu.setSelectedItem(value);
            } else if (obj instanceof AbstractButton button) {

                if (value instanceof Boolean) {
                    button.setSelected((boolean) value);
                } else {
                    Object o = obj.getClientProperty("REF_OBJECT");
                    if (value == null) {
                        button.setSelected(o == null);
                    } else {
                        button.setSelected(value.equals(o));
                    }
                }
            } else if (obj instanceof JComboBox<?> comboBox) {
                Action a = comboBox.getAction();
                comboBox.removeActionListener(a);
                comboBox.setSelectedItem(value);    // problem : trigger an action event
                comboBox.setAction(a);
            } else if (obj instanceof Registrable register) {
                register.update(value);
            }
        }
    }
}
