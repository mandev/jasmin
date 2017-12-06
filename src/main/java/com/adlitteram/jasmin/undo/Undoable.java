
package com.adlitteram.jasmin.undo;

public interface Undoable {

    public void restore(UndoContext context);
}
