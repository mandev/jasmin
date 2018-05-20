
package com.adlitteram.jasmin.gui.explorer;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.event.ListDataListener;

public class ExplorerModel {
    //
    private ArrayList<ImageFile> imageFileList;
    private FileTableModel tableModel;
    private FileListModel listModel;
    private int iconSize;

    public ExplorerModel() {
        this(ExplorerPane.MEDIUM_ICON);
    }

    public ExplorerModel(int iconSize) {
        this(new ArrayList<File>(), iconSize);
    }

    public ExplorerModel(ArrayList<File> fileList) {
        this(fileList, ExplorerPane.MEDIUM_ICON);
    }

    public ExplorerModel(ArrayList<File> fileList, int iconSize) {
        ArrayList<ImageFile> imagefileList = new ArrayList<>(fileList.size());
        fileList.forEach((file) -> {
            imagefileList.add(new ImageFile(file));
        });

        this.imageFileList = imagefileList;
        this.iconSize = iconSize;
        tableModel = new FileTableModel(this, imagefileList);
        listModel = new FileListModel(this, imagefileList);
    }

    public void fireFileAdded(int index1, int index2) {
        tableModel.fireTableRowsInserted(index1, index2);
        listModel.fireIntervalAdded(index1, index2);
    }

    public void fireFileRemoved(int index1, int index2) {
        tableModel.fireTableRowsDeleted(index1, index2);
        listModel.fireIntervalRemoved(index1, index2);
    }

    public void fireFileUpdated(int index1, int index2) {
        if (index1 >= 0 && index2 < imageFileList.size() && index2 >= index1) {
            tableModel.fireTableRowsUpdated(index1, index2);
            listModel.fireContentsChanged(index1, index2);
        }
    }

    public int getIconSize() {
        return iconSize;
    }

    public void setIconSize(int iconSize) {
        this.iconSize = iconSize;
        fireFileUpdated(0, Math.max(0, imageFileList.size() - 1));
    }

    public FileListModel getListModel() {
        return listModel;
    }

    public FileTableModel getTableModel() {
        return tableModel;
    }

    public int size() {
        return imageFileList.size();
    }

    public ImageFile getImageFile(int i) {
        return imageFileList.get(i);
    }

    public File get(int i) {
        return imageFileList.get(i).getFile();
    }

    public void clear() {
        if (!imageFileList.isEmpty()) {
            int index = imageFileList.size() - 1;
            imageFileList.clear();
            fireFileRemoved(0, index);
        }
    }

    public void add(int index, File file) {
        addImageFile(index, new ImageFile(file));
    }

    public void add(File file) {
        addImageFile(new ImageFile(file));
    }

    public void addImageFile(int index, ImageFile file) {
        imageFileList.add(index, file);
        fireFileAdded(index, index);
    }

    public void addImageFile(ImageFile imageFile) {
        int index = Math.max(0, imageFileList.size());
        imageFileList.add(imageFile);
        fireFileAdded(index, index);
    }

    public void remove(int index) {
        imageFileList.remove(index);
        fireFileRemoved(index, index);
    }

    public void remove(int index1, int index2) {
        for (int i = index2; i >= index1; i--) {
            imageFileList.remove(i);
        }
        fireFileRemoved(index1, index2);
    }

    public void sort(Comparator<ImageFile> comparator) {
        Collections.sort(imageFileList, comparator);
        fireFileUpdated(0, Math.max(0, imageFileList.size() - 1));
    }

    public void sort(ArrayList<Comparator<ImageFile>> comparators) {
        comparators.forEach((comparator) -> {
            Collections.sort(imageFileList, comparator);
        });
        fireFileUpdated(0, Math.max(0, imageFileList.size() - 1));
    }

    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
    }

    public ListDataListener [] getListDataListeners() {
        return  listModel.getListDataListeners();
    }

}
