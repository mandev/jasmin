/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.imagetool.EXImage;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import javax.swing.AbstractListModel;

//public class DefaultImageListModel<T extends EXImage> extends AbstractListModel {
public class DefaultImageListModel<T extends EXImage> extends AbstractListModel {

    private final ArrayList<T> list = new ArrayList<>();

    public void add(int index, T obj) {
        list.add(index, obj);
        fireIntervalAdded(this, index, index);
    }

    public void add(T obj) {
        int index = list.size();
        list.add(obj);
        fireIntervalAdded(this, index, index);
    }

    public void add(T[] objects) {
        int index = list.size();
        list.addAll(Arrays.asList(objects));

        if (list.size() > index) {
            fireIntervalAdded(this, index, list.size() - 1);
        }
    }

    // public void addAll(Collection c) {
    // }
    public void clear() {
        int index = list.size() - 1;
        list.clear();
        if (index >= 0) {
            fireIntervalRemoved(this, 0, index);
        }
    }

    public boolean contains(T elem) {
        return (indexOf(elem) >= 0);
    }

    public void ensureCapacity(int minCapacity) {
        list.ensureCapacity(minCapacity);
    }

    public T get(int index) {
        return list.get(index);
    }

    @Override
    public T getElementAt(int index) {
        return get(index);
    }

    @Override
    public int getSize() {
        return size();
    }

    public boolean isEmpty() {
        return list.isEmpty();
    }

    public int indexOf(T elem) {
        return list.indexOf(elem);
    }

//    public int indexOf(T elem) {
//        long checksum;
//        try {
//            checksum = elem.getChecksum();
//            for (int i = 0; i < size(); i++) {
//                T eximg = get(i);
//                try {
//                    if (elem == eximg || checksum == eximg.getChecksum()) return i;
//                }
//                catch (IOException ex) {
//                    logger.log(Level.SEVERE, "", ex);
//                }
//            }
//        }
//        catch (IOException ex) {
//            logger.log(Level.SEVERE, "", ex);
//        }
//        return -1;
//    }
    public void trimToSize() {
        list.trimToSize();
    }

    public T remove(int index) {
        T elem = list.remove(index);
        fireIntervalRemoved(this, index, index);
        return elem;
    }

    public void removeRange(int fromIndex, int toIndex) {
        if (fromIndex > toIndex) {
            throw new IllegalArgumentException("fromIndex must be <= toIndex");
        }
        for (int i = toIndex; i >= fromIndex; i--) {
            list.remove(i);
        }
        fireIntervalRemoved(this, fromIndex, toIndex);
    }

    public T set(int index, T element) {
        T rv = list.set(index, element);
        fireContentsChanged(this, index, index);
        return rv;
    }

    public int size() {
        return list.size();
    }

    public Object[] toArray() {
        return list.toArray();
    }

    public <T> T[] toArray(T[] element) {
        return list.toArray(element);
    }

    @Override
    public String toString() {
        return list.toString();
    }

    // Must be executed on the EDT
    public void updateImageContent(T elem) {
        for (int i = 0; i < getSize(); i++) {
            if (elem == get(i)) {
                fireContentsChanged(this, i, i);
                break;
            }
        }
    }

    // Must be executed on the EDT
    public void sort(Comparator<T> comparator) {
        Collections.sort(list, comparator);
        fireContentsChanged(this, 0, list.size() - 1);
    }
}
