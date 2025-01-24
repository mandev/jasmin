package com.adlitteram.jasmin.action;

import javax.swing.*;

public class ActionManager {

    private final InputMap inputMap;
    private final ActionMap actionMap;

    public ActionManager() {
        this(new InputMap(), new ActionMap());
    }

    public ActionManager(InputMap inputMap, ActionMap actionMap) {
        this.inputMap = inputMap;
        this.actionMap = actionMap;
    }

    public void enableActions() {
        Object[] keys = actionMap.keys();
        if (keys == null) {
            return;
        }

        for (Object key : keys) {
            Action action = actionMap.get(key);
            if (action instanceof XAction act) {
                act.enable();
            }
        }
    }

    public void enableAction(Action action) {
        if (action instanceof XAction act) {
            act.enable();
        }
    }

    public void enableAction(String actionName) {
        enableAction(getAction(actionName));
    }

    public void putAction(String actionName, Action action, KeyStroke key) {
        actionMap.put(actionName, action);
        if (key != null) {
            inputMap.put(key, actionName);
        }
    }

    public void putAction(String actionName, Action action) {
        KeyStroke key = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        putAction(actionName, action, key);
    }

    public void putAction(Action action, String key) {
        putAction(action, KeyStroke.getKeyStroke(key));
    }

    public void putAction(Action action, KeyStroke key) {
        String actionName = (String) action.getValue(Action.NAME);
        putAction(actionName, action, key);
    }

    public void putAction(Action action) {
        String actionName = (String) action.getValue(Action.NAME);
        putAction(actionName, action);
    }

    public void putActions(Action[] actions) {
        for (Action action : actions) {
            putAction(action);
        }
    }

    public void removeAction(Action action) {
        String actionName = (String) action.getValue(Action.NAME);
        actionMap.remove(actionName);
        KeyStroke key = (KeyStroke) action.getValue(Action.ACCELERATOR_KEY);
        if (key != null) {
            inputMap.remove(key);
        }
    }

    public Action getAction(String actionName) {
        return actionMap.get(actionName);
    }

    public InputMap getInputMap() {
        return inputMap;
    }

    public ActionMap getActionMap() {
        return actionMap;
    }
}
