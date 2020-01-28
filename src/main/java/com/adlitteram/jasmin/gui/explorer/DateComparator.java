package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class DateComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public DateComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        long d = o1.firstCreated() - o2.firstCreated();
        if (reverse) {
            d = -d;
        }
        return d > 0 ? 1 : -1;
    }

}
