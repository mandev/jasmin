package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class LengthComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public LengthComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        long d = o1.getLength() - o2.getLength();
        if (reverse) {
            d = -d;
        }
        return d > 0 ? 1 : -1;
    }
}
