package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class NameComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public NameComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        int d = o1.getName().compareToIgnoreCase(o2.getName());
        return reverse ? -d : d;
    }
}
