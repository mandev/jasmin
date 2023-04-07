package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class DateComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public DateComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        return reverse
                ? Long.compare(o1.firstCreated(), o2.firstCreated())
                : Long.compare(o2.firstCreated(), o1.firstCreated());
    }
}
