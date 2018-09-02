package com.adlitteram.jasmin.gui.explorer;

import java.io.File;
import java.util.ArrayList;
import javax.swing.AbstractListModel;

public class FileListModel extends AbstractListModel {
    //

    private final ExplorerModel explorerModel;
    private final ArrayList<ImageFile> imageFileList;
    private ImageFile previousImageFile;

    public FileListModel(ExplorerModel explorerModel, ArrayList<ImageFile> list) {
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

    public boolean contains(File elem) {
        return (indexOf(elem) >= 0);
    }

    public void ensureCapacity(int minCapacity) {
        imageFileList.ensureCapacity(minCapacity);
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

    public int indexOf(File elem) {
        return imageFileList.indexOf(elem);
    }

    public void trimToSize() {
        imageFileList.trimToSize();
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
        for (int i = toIndex; i >= fromIndex; i--) {
            imageFileList.remove(i);
        }
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

    public Object[] toArray() {
        return imageFileList.toArray();
    }

    public <ImageFile> ImageFile[] toArray(ImageFile[] element) {
        return imageFileList.toArray(element);
    }

    @Override
    public String toString() {
        return imageFileList.toString();
    }
}
