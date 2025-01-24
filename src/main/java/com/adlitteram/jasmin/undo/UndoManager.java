package com.adlitteram.jasmin.undo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.LinkedList;

public class UndoManager {

    private static final Logger logger = LoggerFactory.getLogger(UndoManager.class);

    public static final int NORM = 0;
    public static final int UNDO = 1; // Ctrl Z
    public static final int REDO = 2; // Ctrl Y
    public static final String GLOBAL_CONTEXT_IS_NOT_CREATED_OR_ALREADY_COMMITTED = "GlobalContext is not created or already committed";

    private final LinkedList<UndoContext> undoContextList = new LinkedList<>();
    private final LinkedList<UndoContext> redoContextList = new LinkedList<>();
    private UndoContext globalContext;
    private int maxUndoSize = 50;
    private int state = NORM;

    public synchronized int getMaxUndoSize() {
        return maxUndoSize;
    }

    public synchronized void setMaxUndoSize(int maxUndoSize) {
        this.maxUndoSize = maxUndoSize;
    }

    public synchronized void clear() {
        globalContext = null;
        undoContextList.clear();
        redoContextList.clear();
    }

    public synchronized void begin() {
        if (globalContext != null && !globalContext.isCommitted()) {
            logger.info("GlobalContext exists and is not committed");
            commit(globalContext);
        }
        globalContext = new UndoContext(this);
    }

    public synchronized void add(Undoable undoable) {
        if (globalContext == null || globalContext.isCommitted()) {
            logger.info(GLOBAL_CONTEXT_IS_NOT_CREATED_OR_ALREADY_COMMITTED);
            begin();
        }
        globalContext.add(undoable);
    }

    public synchronized void commit() {
        if (globalContext != null && !globalContext.isCommitted()) {
            commit(globalContext);
        } else {
            logger.info(GLOBAL_CONTEXT_IS_NOT_CREATED_OR_ALREADY_COMMITTED);
        }
    }

    public synchronized boolean isRolling() {
        return (globalContext != null && !globalContext.isCommitted());
    }

    public synchronized void rollback() {
        if (globalContext != null && !globalContext.isCommitted()) {
            globalContext = new UndoContext(this);
        } else {
            logger.info(GLOBAL_CONTEXT_IS_NOT_CREATED_OR_ALREADY_COMMITTED);
        }
    }

    public synchronized void addCommit(Undoable undoable) {
        begin();
        add(undoable);
        commit();
    }

    public synchronized void addContextCommit(Undoable undoable) {
        UndoContext context = createContext();
        context.add(undoable);
        commit(context);
    }

    public UndoContext createContext() {
        return new UndoContext(this);
    }

    // Ctrl Z
    public synchronized void undo() {
        if (!undoContextList.isEmpty()) {
            state = UNDO;
            undoContextList.removeLast().restore();
            state = NORM;
        }
    }

    // Ctrl Y
    public synchronized void redo() {
        if (!redoContextList.isEmpty()) {
            state = REDO;
            redoContextList.removeLast().restore();
            state = NORM;
        }
    }

    public synchronized boolean isUndoListEmpty() {
        return undoContextList.isEmpty();
    }

    public synchronized boolean isRedoListEmpty() {
        return redoContextList.isEmpty();
    }

    public synchronized void commit(UndoContext context) {
        switch (state) {
            case NORM:
                if (context.isEmpty()) {
                    logger.info("UndoContext is empty");
                    context.setCommitted(true);
                } else if (context.isCommitted()) {
                    logger.info("UndoContext is already committed");
                } else {
                    context.setCommitted(true);
                    undoContextList.add(context);
                    if (undoContextList.size() > maxUndoSize) {
                        undoContextList.removeFirst();
                    }
                    redoContextList.clear();
                }
                break;
            case UNDO:
                if (context.isCommitted()) {
                    logger.info("RedoContext is already committed");
                } else if (!context.isEmpty()) {
                    context.setCommitted(true);
                    redoContextList.add(context);
                }
                break;
            case REDO:
                if (context.isCommitted()) {
                    logger.info("UnredoContext is already committed");
                } else if (!context.isEmpty()) {
                    context.setCommitted(true);
                    undoContextList.add(context);
                    if (undoContextList.size() > maxUndoSize) {
                        undoContextList.removeFirst();
                    }
                }
                break;
            default:
                break;
        }
    }
}
