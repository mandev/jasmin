package com.adlitteram.jasmin.gui.widget;

import java.awt.Component;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Action;
import javax.swing.Icon;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;

public class PopupButton extends JToggleButton {

    protected JPopupMenu popup;
    protected Icon dropDownIcon;
    protected boolean popupVisible;
    protected boolean arrowPressed;
    protected ActionListener defaultActionListener;
    protected ActionListener buttonActionListener;

    public PopupButton() {
        super();
        initialize();
    }

    public PopupButton(Icon icon) {
        super(icon);
        initialize();
    }

    public PopupButton(String text) {
        super(text);
        initialize();
    }

    public PopupButton(Action a) {
        super(a);
        initialize();
    }

    public PopupButton(String text, Icon icon) {
        super(text, icon);
        initialize();
    }

    private void initialize() {
        popup = new JPopupMenu();
        popupVisible = false;

        popup.addPopupMenuListener(new PopupMenuListener() {

            @Override
            public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
                setPopupVisible(true);
            }

            @Override
            public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                setPopupVisible(false);
            }

            @Override
            public void popupMenuCanceled(PopupMenuEvent e) {
                setPopupVisible(false);
            }
        });

        addMouseListener(new MouseHandler());

        addActionListener((ActionEvent e) -> {
            if (isArrowPressed()) {
                togglePopupVisible();
                ActionListener arrowActionListener = getArrowActionListener();
                if (arrowActionListener != null) {
                    arrowActionListener.actionPerformed(e);
                }
            } else {
                ActionListener buttonActionListener1 = getButtonActionListener();
                if (buttonActionListener1 != null) {
                    buttonActionListener1.actionPerformed(e);
                }
            }
        });

        // Workaround for Win XP
        setMargin(new Insets(0, 0, 0, 0));
    }

    public boolean isPopupVisible() {
        return popupVisible;
    }

    public void setPopupVisible(boolean popupVisible) {
        this.popupVisible = popupVisible;
        //setSelected(popupVisible);
    }

    public JPopupMenu getPopup() {
        return popup;
    }

    public void setPopup(JPopupMenu popup) {
        this.popup = popup;
    }

    public Icon getDropDownIcon() {
        if (dropDownIcon == null) {
            setDropDownIcon(new MarginIcon(new Insets(0, 2, 0, 2), new DropDownIcon()));
        }
        return dropDownIcon;
    }

    public void setDropDownIcon(Icon dropDownIcon) {
        this.dropDownIcon = dropDownIcon;
    }

    public boolean isArrowPressed() {
        return arrowPressed;
    }

    public void setArrowPressed(boolean arrowPressed) {
        this.arrowPressed = arrowPressed;
    }

    public ActionListener getArrowActionListener() {
        return defaultActionListener;
    }

    public void setArrowActionListener(ActionListener defaultActionListener) {
        this.defaultActionListener = defaultActionListener;
    }

    public ActionListener getButtonActionListener() {
        return buttonActionListener;
    }

    public void setButtonActionListener(ActionListener buttonActionListener) {
        this.buttonActionListener = buttonActionListener;
    }

    @Override
    public Component add(Component c) {
        return popup.add(c);
    }

    public void togglePopupVisible() {
        if (popup.isShowing()) {
            popup.setVisible(false);
        } else {
            popup.show(this, 0, getHeight());
        }
    }

    @Override
    public void setMargin(Insets m) {
        Icon ico = getDropDownIcon();
        m = new Insets(m.top, m.left, m.bottom, m.right + ico.getIconWidth());
        super.setMargin(m);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);

        // lets paint that icon...
        int width = getWidth();
        int height = getHeight();
        Icon ico = getDropDownIcon();
        ico.paintIcon(this, g, width - ico.getIconWidth(), (height / 2) - (ico.getIconHeight() / 2));
    }

    public boolean isAboveArrow(Point p) {
        return (getWidth() - getDropDownIcon().getIconWidth()) < p.x;
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mousePressed(MouseEvent e) {
            setArrowPressed(isAboveArrow(e.getPoint()));
        }
    }
}
