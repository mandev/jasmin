package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class DimensionComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public DimensionComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        return reverse
                ? Integer.compare(o1.getWidth(), o2.getWidth())
                : Integer.compare(o2.getWidth(), o1.getWidth());
    }
}
