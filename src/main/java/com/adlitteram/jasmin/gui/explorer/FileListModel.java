package com.adlitteram.jasmin.gui.explorer;

import javax.swing.*;
import java.io.File;
import java.util.List;

public class FileListModel extends AbstractListModel<ImageFile> {

    private final ExplorerModel explorerModel;
    private final List<ImageFile> imageFileList;
    private ImageFile previousImageFile;

    public FileListModel(ExplorerModel explorerModel, List<ImageFile> list) {
        this.explorerModel = explorerModel;
        this.imageFileList = list;
    }

    public void fireContentsChanged(int index0, int index1) {
        super.fireContentsChanged(this, index0, index1);
    }

    public void fireIntervalAdded(int index0, int index1) {
        super.fireIntervalAdded(this, index0, index1);
    }

    public void fireIntervalRemoved(int index0, int index1) {
        super.fireIntervalRemoved(this, index0, index1);
    }

    public boolean contains(Object elem) {
        return (indexOf(elem) >= 0);
    }

    private ImageFile getImageFile(int index) {
        ImageFile imageFile = imageFileList.get(index);
        if (imageFile != previousImageFile || imageFile.getIconSize() != explorerModel.getIconSize()) {
            previousImageFile = imageFile;
            if (!imageFile.isCompleted() || imageFile.getIconSize() != explorerModel.getIconSize()) {
                imageFile.update(explorerModel, index);
            }
        }
        return imageFile;
    }

    public ImageFile get(int index) {
        return getImageFile(index);
    }

    @Override
    public ImageFile getElementAt(int index) {
        return get(index);
    }

    @Override
    public int getSize() {
        return size();
    }

    public boolean isEmpty() {
        return imageFileList.isEmpty();
    }

    public int indexOf(Object element) {
        return imageFileList.indexOf(element);
    }

    public ImageFile remove(int index) {
        ImageFile elem = imageFileList.remove(index);
        fireIntervalRemoved(this, index, index);
        return elem;
    }

    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        imageFileList.subList(fromIndex, toIndex + 1).clear();
        fireIntervalRemoved(this, fromIndex, toIndex);
    }

    public ImageFile set(int index, ImageFile element) {
        ImageFile file = imageFileList.set(index, element);
        fireContentsChanged(this, index, index);
        return file;
    }

    public int size() {
        return imageFileList.size();
    }

    public ImageFile[] toArray() {
        return imageFileList.toArray(new ImageFile[0]);
    }

    @Override
    public String toString() {
        return imageFileList.toString();
    }
}
