/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class FormatComparator implements Comparator<ImageFile> {

    private final boolean reverse;

    public FormatComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        int d = o1.getFormat().compareToIgnoreCase(o2.getFormat());
        return reverse ? -d : d;
    }
}
