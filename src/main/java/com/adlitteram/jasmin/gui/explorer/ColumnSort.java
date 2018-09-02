
package com.adlitteram.jasmin.gui.explorer;

public class ColumnSort {
    //
    public static final int DESCENDING = -1;
    public static final int NOT_SORTED = 0;
    public static final int ASCENDING = 1;
    //
    //public static final ColumnSort EMPTY = new ColumnSort(-1, NOT_SORTED);
    //
    private int column;
    private int direction;

    public ColumnSort(int column, int direction) {
        this.column = column;
        this.direction = direction;
    }

    public int getColumn() {
        return column;
    }

    public int getDirection() {
        return direction;
    }

    public int getInverseDirection() {
        return -direction;
    }

    public boolean isDescending() {
        return direction == ColumnSort.DESCENDING;
    }

    public boolean isAscending() {
        return direction == ColumnSort.ASCENDING;
    }

    public boolean isNotSorted() {
        return direction == ColumnSort.NOT_SORTED;
    }


}
