package com.adlitteram.jasmin.gui;

import javax.swing.*;

public abstract class XAbstractMenu extends JMenu {

    public XAbstractMenu(String s) {
        super(s);
    }

    public void setSelectedItem(Object value) {
        for (int i = 0; i < getItemCount(); i++) {
            JMenuItem item = getItem(i);
            Object obj = item.getClientProperty("REF_OBJECT");

            if (value == null) {
                item.setSelected(obj == null);
            } else {
                item.setSelected(value.equals(obj));
            }
        }
    }
}
