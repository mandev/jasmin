package com.adlitteram.jasmin;

import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.Font;
import java.awt.Window;
import java.util.Enumeration;
import javax.swing.SwingUtilities;
import javax.swing.UIDefaults;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.plaf.FontUIResource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LookManager {

    private static final Logger logger = LoggerFactory.getLogger(LookManager.class);
    //
    private static Applicationable application;
    //
    public static final String[] looks = {
        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
        "apple.laf.AquaLookAndFeel",
        "javax.swing.plaf.metal.MetalLookAndFeel",
        "com.sun.java.swing.plaf.nimbus.NimbusLookAndFeel", // "ch.randelshofer.quaqua.QuaquaLookAndFeel"
    };

    public static void init(Applicationable app) {
        application = app;
        String lf = UIManager.getSystemLookAndFeelClassName();
        setLookAndFeel(XProp.get("LookAndFeel", lf), false);
    }

    public static void setLookAndFeel(String lf) {
        setLookAndFeel(lf, true);
    }

    public static void setLookAndFeel(final String lf, final boolean alert) {
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                try {
                    UIManager.setLookAndFeel(lf);
                    XProp.put("LookAndFeel", lf);
                }
                catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
//                    if (alert) {
//                        GuiUtils.showMessage(Message.get("LookAndFeel.NotSupported"));
//                    }
                    logger.warn("Message.get(\"LookAndFeel.NotSupported\")", e);
                }
            }
        });
    }

    public static void updateComponentUI() {
        // Update Help Window
        SwingUtilities.invokeLater(new Runnable() {

            @Override
            public void run() {
                HelpManager.updateComponentUI();

                Window window = application.getMainFrame();
                // Update Dialogs, Popup, etc...
                if (window != null) {
                    SwingUtilities.updateComponentTreeUI(window);
                    window.validate();
                    window.repaint();
                }
            }
        });
    }

    public static void setDefaultFont(float scale) {
        UIDefaults defaults = UIManager.getDefaults();
        Enumeration keys = defaults.keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = defaults.get(key);
            if (value instanceof Font) {
                UIManager.put(key, null);
                Font font = UIManager.getFont(key);
                if (font != null) {
                    float size = font.getSize2D();
                    UIManager.put(key, new FontUIResource(font.deriveFont(size * scale)));
                }
            }
        }
    }
}
