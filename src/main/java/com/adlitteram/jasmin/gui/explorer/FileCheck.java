
package com.adlitteram.jasmin.gui.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

public class FileCheck implements ImageFileCheckable {
    //
    private Set<File> fileSet = new HashSet<File>();
    private ArrayList<ExplorerPane> explorerPaneList = new ArrayList<ExplorerPane>();

    public FileCheck() {
    }

    public FileCheck(ExplorerPane explorerPane) {
        addListener(explorerPane);
    }

    public void addListener(ExplorerPane explorerPane) {
        if (!explorerPaneList.contains(explorerPane)) {
            explorerPaneList.add(explorerPane);
            fireExplorerPanes();
        }
    }

    public void removeListener(ExplorerPane explorerPane) {
        explorerPaneList.remove(explorerPane);
        fireExplorerPanes();
    }

    protected void fireExplorerPanes() {
        for (ExplorerPane explorerPane : explorerPaneList) {
            explorerPane.repaint();
        }
    }

    public void clear() {
        fileSet.clear();
        fireExplorerPanes();
    }

    public void putAll(Collection<? extends File> c) {
        fileSet.addAll(c);
    }

    public void add(File file) {
        fileSet.add(file);
        fireExplorerPanes();
    }

    public void remove(File file) {
        fileSet.remove(file);
        fireExplorerPanes();
    }

    public boolean check(ImageFile imageFile) {
        return fileSet.contains(imageFile.getFile());
    }
}
