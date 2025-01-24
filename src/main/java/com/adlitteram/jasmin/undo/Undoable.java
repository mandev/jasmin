package com.adlitteram.jasmin.undo;

public interface Undoable {

    void restore(UndoContext context);
}
