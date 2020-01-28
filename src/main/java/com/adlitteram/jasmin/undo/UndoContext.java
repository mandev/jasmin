package com.adlitteram.jasmin.undo;

import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UndoContext {

    private static final Logger logger = LoggerFactory.getLogger(UndoContext.class);

    private final UndoManager undoManager;
    private boolean isCommitted = false;
    private final ArrayList<Undoable> undoTransList = new ArrayList<>();

    protected UndoContext(UndoManager undoManager) {
        this.undoManager = undoManager;
    }

    public void add(Undoable undoable) {
        if (isCommitted) {
            logger.info("Context is already committed");
        }
        else {
            undoTransList.add(undoable);
        }
    }

    public boolean isEmpty() {
        return undoTransList.isEmpty();
    }

    protected void restore() {
        if (!isCommitted) {
            logger.info("Context is not committed");
        }
        else {
            UndoContext context = undoManager.createContext();
            for (int i = undoTransList.size() - 1; i >= 0; i--) {
                undoTransList.get(i).restore(context);
            }
            undoManager.commit(context);
        }
    }

    public void commit() {
        undoManager.commit(this);
    }

    public boolean isCommitted() {
        return isCommitted;
    }

    protected void setCommitted(boolean isCommitted) {
        this.isCommitted = isCommitted;
    }
}
