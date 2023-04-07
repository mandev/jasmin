package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class LengthComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public LengthComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        return reverse
                ? Long.compare(o1.getLength(), o2.getLength())
                : Long.compare(o2.getLength(), o1.getLength());
    }
}
