package com.adlitteram.jasmin.gui;

import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.event.MenuEvent;
import javax.swing.event.MenuListener;

public class RecentFilesItems implements MenuListener {

    private final JMenu menu;
    private final int pos;
    //
    private ActionListener actionListener;
    private ArrayList<String> filenameList;

    public RecentFilesItems(JMenu menu) {
        this.menu = menu;
        this.pos = menu.getItemCount();
        menu.addMenuListener(this);
    }

    public void setActionListener(ActionListener actionListener) {
        this.actionListener = actionListener;
    }

    public void setFilenameList(ArrayList<String> filenameList) {
        this.filenameList = filenameList;
    }

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

    private void removeChildItems() {
        for (int i = menu.getItemCount() - 1; i >= 0; i--) {
            JMenuItem item = menu.getItem(i);
            if (item instanceof RecentFileItem) {
                menu.remove(item);
            }
        }
    }

    private void buildChildItems() {
        if (filenameList != null) {
            for (int i = 0; i < filenameList.size(); i++) {
                RecentFileItem item = new RecentFileItem(filenameList.get(i), i + 1);
                menu.insert(item, pos + i);
                if (actionListener != null) {
                    item.addActionListener(actionListener);
                }
            }
        }
    }

    // This JCheckBoxMenuItem descendant is used to track the
    // child frame that corresponds to a give menu.
    public class RecentFileItem extends JMenuItem {

        private final String filename;

        public RecentFileItem(String filename, int i) {
            super(i + "  " + filename);
            this.filename = filename;
        }

        public String getFileName() {
            return filename;
        }
    }
}
