
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.gui;

import javax.swing.JMenu;
import javax.swing.JMenuItem;

public abstract class XAbstractMenu extends JMenu {

    public XAbstractMenu(String s) {
        super(s);
    }

    public void setSelectedItem(Object value) {
        for (int i = 0; i < getItemCount(); i++) {
            JMenuItem item = getItem(i);
            Object obj = item.getClientProperty("REF_OBJECT");

            if (value == null)
                item.setSelected(obj == null);
            else
                item.setSelected(value.equals(obj));
        }
    }
}
