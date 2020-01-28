package com.adlitteram.jasmin;

import com.adlitteram.jasmin.property.XProp;
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

    private static Applicationable application;

    public static final String[] LOOKS = {
        "com.sun.java.swing.plaf.windows.WindowsLookAndFeel",
        "com.sun.java.swing.plaf.gtk.GTKLookAndFeel",
        "apple.laf.AquaLookAndFeel",
        "javax.swing.plaf.metal.MetalLookAndFeel",
        "javax.swing.plaf.nimbus.NimbusLookAndFeel", // "ch.randelshofer.quaqua.QuaquaLookAndFeel"
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
        SwingUtilities.invokeLater(() -> {
            try {
                UIManager.setLookAndFeel(lf);
                XProp.put("LookAndFeel", lf);
            } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
                logger.warn("Message.get(\"LookAndFeel.NotSupported\")", e);
            }
        });
    }

    public static void updateComponentUI() {
        SwingUtilities.invokeLater(() -> {
            HelpManager.updateComponentUI();
            Window window = application.getMainFrame();
            if (window != null) {
                SwingUtilities.updateComponentTreeUI(window);
                window.validate();
                window.repaint();
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
