/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller
 */
package com.adlitteram.jasmin.gui;

import java.awt.event.ActionEvent;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class LastFilesItems {

    private final JMenu menu;
    private final int pos;

    public LastFilesItems(JMenu menu) {
        this.menu = menu;
        pos = menu.getItemCount();

        menu.addMenuListener(new MenuListener() {

            @Override
            public void menuCanceled(MenuEvent e) {
            }

            @Override
            public void menuDeselected(MenuEvent e) {
                removeChildItems();
            }

            @Override
            public void menuSelected(MenuEvent e) {
                buildChildItems();
            }
        });
    }

    private void removeChildItems() {
        for (int j = menu.getItemCount() - 1; j >= 0; j--) {
            JMenuItem item = menu.getItem(j);
            if (item instanceof LastFileItem) {
                menu.remove(item);
            }
        }
    }

    private void buildChildItems() {

        ArrayList list = new ArrayList();

        for (int i = 0; i < list.size(); i++) {
            LastFileItem item = new LastFileItem((String) list.get(i), i + 1);
            menu.insert(item, pos + i);

            item.addActionListener((ActionEvent e) -> {
                //OpenDocument.action(((LastFileItem)e.getSource()).getFileName()) ;
            });
        }
    }

    // This JCheckBoxMenuItem descendant is used to track the child frame that corresponds to a give menu.
    class LastFileItem extends JMenuItem {

        private final String fileName;

        public LastFileItem(String fileName, int i) {
            super(i + "  " + fileName);
            this.fileName = fileName;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
