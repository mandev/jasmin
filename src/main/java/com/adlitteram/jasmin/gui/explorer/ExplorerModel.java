package com.adlitteram.jasmin.gui.explorer;

import javax.swing.event.ListDataListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ExplorerModel {

    private final List<ImageFile> imageFileList;
    private final FileTableModel tableModel;
    private final FileListModel listModel;
    private int iconSize;

    public ExplorerModel() {
        this(ExplorerPane.MEDIUM_ICON);
    }

    public ExplorerModel(int iconSize) {
        this(new ArrayList<>(), iconSize);
    }

    public ExplorerModel(List<File> fileList) {
        this(fileList, ExplorerPane.MEDIUM_ICON);
    }

    public ExplorerModel(List<File> fileList, int iconSize) {
        List<ImageFile> imagefileList = new ArrayList<>(fileList.size());
        fileList.forEach(file -> imagefileList.add(new ImageFile(file)));

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
        int index = imageFileList.size();
        imageFileList.add(imageFile);
        fireFileAdded(index, index);
    }

    public void remove(int index) {
        imageFileList.remove(index);
        fireFileRemoved(index, index);
    }

    public void remove(int index1, int index2) {
        if (index2 >= index1) {
            imageFileList.subList(index1, index2 + 1).clear();
        }
        fireFileRemoved(index1, index2);
    }

    public void sort(Comparator<ImageFile> comparator) {
        imageFileList.sort(comparator);
        fireFileUpdated(0, Math.max(0, imageFileList.size() - 1));
    }

    public void sort(List<Comparator<ImageFile>> comparators) {
        comparators.forEach(imageFileList::sort);
        fireFileUpdated(0, Math.max(0, imageFileList.size() - 1));
    }

    public void addListDataListener(ListDataListener l) {
        listModel.addListDataListener(l);
    }

    public void removeListDataListener(ListDataListener l) {
        listModel.removeListDataListener(l);
    }

    public ListDataListener[] getListDataListeners() {
        return listModel.getListDataListeners();
    }

}
