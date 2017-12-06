/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adlitteram.jasmin.gui.explorer;

import java.util.Comparator;

public class DimensionComparator implements Comparator<ImageFile> {
    //
    private boolean reverse;

    public DimensionComparator(boolean reverse) {
        this.reverse = reverse;
    }

    @Override
    public int compare(ImageFile o1, ImageFile o2) {
        int d = o1.getWidth() - o2.getWidth();
        return reverse ? -d : d;
    }
}
