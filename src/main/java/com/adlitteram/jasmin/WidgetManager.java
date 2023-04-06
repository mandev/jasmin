package com.adlitteram.jasmin;

import com.adlitteram.jasmin.gui.XAbstractMenu;
import com.adlitteram.jasmin.gui.widget.Registrable;
import java.util.ArrayList;
import java.util.HashMap;
import javax.swing.AbstractButton;
import javax.swing.Action;
import javax.swing.JComboBox;
import javax.swing.JComponent;

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
        ArrayList<JComponent> widgetList = widgetsMap.get(action);
        if (widgetList == null) {
            widgetList = new ArrayList<>();
            widgetsMap.put(action, widgetList);
        }
        widget.putClientProperty("SourceObject", source);
        widgetList.add(widget);
    }

    public void registerWidget(AbstractButton widget) {
        registerWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    public void registerWidget(JComboBox widget) {
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

    public void unregisterWidget(JComboBox widget) {
        unregisterWidget(widget, (String) (widget.getAction()).getValue(Action.NAME));
    }

    // Tell a widget that it should change
    public void updateWidgets(String action, Object value) {
        ArrayList<JComponent>  widgetList = widgetsMap.get(action);
        if (widgetList == null) {
            return;
        }

        for (JComponent obj : widgetList) {
            if (obj instanceof XAbstractMenu) {
                ((XAbstractMenu) obj).setSelectedItem(value);
            }
            else if (obj instanceof AbstractButton) {

                if (value instanceof Boolean) {
                    ((AbstractButton) obj).setSelected((boolean) value);
                }
                else {
                    Object o = ((AbstractButton) obj).getClientProperty("REF_OBJECT");
                    if (value == null) {
                        ((AbstractButton) obj).setSelected(o == null);
                    }
                    else {
                        ((AbstractButton) obj).setSelected(value.equals(o));
                    }
                }
            }
            else if (obj instanceof JComboBox) {
                Action a = ((JComboBox) obj).getAction();
                ((JComboBox) obj).removeActionListener(a);
                ((JComboBox) obj).setSelectedItem(value);	// problem : trigger an action event
                ((JComboBox) obj).setAction(a);
            }
            else if (obj instanceof Registrable) {
                ((Registrable) obj).update(value);
            }
        }
    }
}
