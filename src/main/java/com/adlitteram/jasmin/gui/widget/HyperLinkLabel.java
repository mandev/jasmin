package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.utils.ExecUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class HyperLinkLabel extends JLabel implements MouseListener {

    private static final Logger logger = LoggerFactory.getLogger(HyperLinkLabel.class);

    private String url;

    public HyperLinkLabel(String label) {
        super(label);
        setForeground(Color.BLUE);
        addMouseListener(this);
    }

    public HyperLinkLabel(String label, String url) {
        this(label);
        this.url = url;
    }

    public HyperLinkLabel(String label, String tip, String url) {
        this(label, url);
        setToolTipText(tip);
    }

    public void setURL(String url) {
        this.url = url;
    }

    public String getURL() {
        return url;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (url != null) {
            try {
                ExecUtils.execBrowser(url);
            } catch (Exception ex) {
                logger.warn("", ex);
            }
        }
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        e.getComponent().setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
    }

    @Override
    public void mouseExited(MouseEvent e) {
        e.getComponent().setCursor(Cursor.getDefaultCursor());
    }

    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        // Do nothing
    }
}
