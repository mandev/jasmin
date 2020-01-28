package com.adlitteram.jasmin.gui;

import com.adlitteram.jasmin.HelpManager;
import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.action.ActionManager;
import com.adlitteram.jasmin.action.XAction;
import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import javax.help.HelpBroker;
import javax.swing.*;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GuiBuilder {

    private static final Logger logger = LoggerFactory.getLogger(GuiBuilder.class);

    private final ActionManager actionManager;

    public GuiBuilder(ActionManager actionManager) {
        this.actionManager = actionManager;
    }

    public JButton buildHelpButton(String key) {
        JButton helpButton = new JButton(GuiUtils.HELP_ICON);
        helpButton.setContentAreaFilled(false);
        helpButton.setBorder(null);
        helpButton.setFocusPainted(false);

        HelpBroker hb = HelpManager.getHelpBroker();
        if (hb != null) {
            hb.enableHelpOnButton(helpButton, key, hb.getHelpSet());
        }
        return helpButton;
    }

    // Menu builder	
    public JMenu buildMenu(String label) {
        if (label == null) {
            label = "?????";
        }
        String text = XProp.get(label + ".text", XProp.get(label + ".txt", label));
        int index = text.indexOf('$');
        if (index != -1 && text.length() - index > 1) {
            JMenu menu = new JMenu(text.substring(0, index).concat(text.substring(++index)));
            menu.setMnemonic(Character.toLowerCase(text.charAt(index)));
            return menu;
        }
        return new JMenu(text);
    }

    // Menu Item builder
    public JMenuItem buildMenuItem(String actionName) {
        return buildMenuItem(actionName, null, null, null, null, null);
    }

    public JMenuItem buildMenuItem(String actionName, String icon, String key, String label, String toolTip, Object obj) {
        if (actionName == null) {
            logger.info("Cannot add menu item (actionName = null)");
            return null;
        }

        Action action = actionManager.getAction(actionName);
        if (action == null) {
            logger.info("cannot add action to menu (action = null) - actionName : " + actionName);
            return null;
        }

        JMenuItem item = new JMenuItem(action);
        setItemAtrributes(item, icon, key, label, toolTip, obj);
        return item;
    }

    // Check Menu Item builder
    public JCheckBoxMenuItem buildCheckMenuItem(String actionName, String icon, String key, String label, String toolTip, Object obj) {
        if (actionName == null) {
            return null;
        }

        Action action = actionManager.getAction(actionName);
        if (action == null) {
            return null;
        }

        JCheckBoxMenuItem item = new JCheckBoxMenuItem(action);
        setItemAtrributes(item, icon, key, label, toolTip, obj);
        return item;
    }

    // Button builder
    public JButton buildButton(String actionName) {
        return buildButton(actionName, null, null, null, null, null);
    }

    public JButton buildButton(String actionName, String icon, String key, String label, String tooltip) {
        return buildButton(actionName, icon, key, label, tooltip, null);
    }

    public JButton buildButton(String actionName, String icon, String key, String label, String tooltip, Object obj) {
        if (actionName == null) {
            return null;
        }

        Action action = actionManager.getAction(actionName);
        if (action == null) {
            return null;
        }

        JButton button = new JButton(action);
        //button.setContentAreaFilled(false);
        button.setOpaque(false);
        button.setFocusPainted(false);
        button.setFocusable(false);
        button.setRolloverEnabled(true);
        setItemAtrributes(button, icon, key, label, tooltip, obj);
        return button;
    }

    // Toggle Button builder
    public JToggleButton buildToggleButton(String actionName, String icon, String key, String label, String toolTip, Object obj) {
        if (actionName == null) {
            return null;
        }

        Action action = actionManager.getAction(actionName);
        if (action == null) {
            return null;
        }

        JToggleButton item = new JToggleButton(action);
        item.setOpaque(false);
        //item.setContentAreaFilled(false) ;
        item.setFocusable(false);
        item.setFocusPainted(false);
        item.setRolloverEnabled(true);

        setItemAtrributes(item, icon, key, label, toolTip, obj);
        return item;
    }

    public void setBackgroundIcon(AbstractButton item) {

        if (SystemUtils.IS_OS_MAC_OSX) {
            Icon ic = item.getIcon();
            BufferedImage img = new BufferedImage(ic.getIconWidth(), ic.getIconHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = (Graphics2D) img.getGraphics();
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g2.setColor(Color.LIGHT_GRAY);
            g2.fillRoundRect(0, 0, img.getWidth(), img.getHeight(), 5, 5);
            ic.paintIcon(item, g2, 0, 0);
            item.setSelectedIcon(new ImageIcon(img));
        }

        item.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
        //item.setRolloverEnabled(true) ;
    }

    private void setItemAtrributes(AbstractButton item, String icon, String key, String label, String toolTip, Object obj) {

        XAction action = (XAction) item.getAction();

        if (item instanceof JMenuItem) {
            item.setText(label == null ? action.getText() : label);
        }
        else if ("true".equals(label)) {
            item.setVerticalTextPosition(SwingConstants.BOTTOM);
            item.setHorizontalTextPosition(SwingConstants.CENTER);
            item.setText(action.getLabel());
            Font font = item.getFont();
            item.setFont(font.deriveFont(font.getSize2D() - 2f));
            item.putClientProperty("JComponent.sizeVariant", "small");
        }
        else {
            item.setText(null);
        }

        if (icon != null) {
            item.setIcon(GuiUtils.loadIcon(icon));
        }
        else if (item instanceof JMenuItem) {
            item.setIcon(null);
        }
        else {
            item.setIcon(action.getIcon());
        }

        if (item.getIcon() != null) {
            setBackgroundIcon(item);
        }

        if (toolTip != null) {
            item.setToolTipText(toolTip);
        }
        else {
            item.setToolTipText(action.getToolTipText());
        }

        if (obj != null) {
            item.putClientProperty("REF_OBJECT", obj);
        }

        if (item instanceof JMenuItem) {
            if (key != null) {
                ((JMenuItem) item).setAccelerator(KeyStroke.getKeyStroke(key));
            }
            else {
                ((JMenuItem) item).setAccelerator(action.getAccelerator());
            }
        }
    }
}
