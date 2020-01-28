package com.adlitteram.jasmin.action;

import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.utils.GuiUtils;
import javax.swing.AbstractAction;
import javax.swing.Icon;
import javax.swing.JComponent;
import javax.swing.KeyStroke;

abstract public class XAction extends AbstractAction {

    public XAction(String actionName) {
        super(actionName);

        // Text (label) & Mnemonic
        String text = XProp.get(actionName + ".text");
        if (text == null) {
            text = XProp.get(actionName + ".txt");
            if (text == null) {
                text = actionName;
            }
        }

        int index = text.indexOf('$');
        if (index != -1 && ((text.length() - index) > 1)) {
            text = text.substring(0, index).concat(text.substring(++index));
            putValue(MNEMONIC_KEY, (int) (text.charAt(index - 1)));
        }
        putValue("TEXT", text);

        // Label
        String labelName = XProp.get(actionName + ".label");
        if (labelName != null) {
            putValue("LABEL", labelName);
        }

        // Icon
        String iconName = XProp.get(actionName + ".ico");
        if (iconName != null) {
            putValue(SMALL_ICON, GuiUtils.loadIcon(iconName));
        }

        // Shortcut
        String keyName = XProp.get(actionName + ".key");
        if (keyName != null) {
            putValue(ACCELERATOR_KEY, KeyStroke.getKeyStroke(keyName));
        }

        // Tooltip
        String tipName = XProp.get(actionName + ".tip");
        if (tipName != null) {
            putValue(SHORT_DESCRIPTION, tipName);
        }
    }

    public Object getSource(Object widget) {
        return (widget instanceof JComponent) ? ((JComponent) widget).getClientProperty("SourceObject") : null;
    }

    public String getName() {
        return (String) getValue(NAME);
    }

    public String getText() {
        return (String) getValue("TEXT");
    }

    public String getLabel() {
        return (String) getValue("LABEL");
    }

    public KeyStroke getAccelerator() {
        return (KeyStroke) getValue(ACCELERATOR_KEY);
    }

    public Icon getIcon() {
        return (Icon) getValue(SMALL_ICON);
    }

    public int getMnemonic() {
        Integer n = (Integer) getValue(MNEMONIC_KEY);
        if (n != null) {
            return n;
        }
        return -1;
    }

    public String getToolTipText() {
        return (String) getValue(SHORT_DESCRIPTION);
    }

    public void enable() {
        setEnabled(true);
    }
}
