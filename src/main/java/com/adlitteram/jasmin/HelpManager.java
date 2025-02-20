package com.adlitteram.jasmin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.help.*;
import javax.swing.*;
import java.awt.*;
import java.net.URL;

public final class HelpManager {

    private static final Logger logger = LoggerFactory.getLogger(HelpManager.class);

    private static final String NAME = "help_";

    private static Applicationable application;
    private static DefaultHelpBroker helpBroker;
    private static String language;

    private HelpManager() {
    }

    public static void init(Applicationable app) {
        application = app;
    }

    public static HelpBroker getHelpBroker() {

        String lang = LocaleManager.getUILocale().getLanguage();

        if (helpBroker == null || language == null || !language.equals(lang)) {

            try {
                ClassLoader cl = application.getMainClass().getClassLoader();
                String helpsetName = lang + "/" + NAME + lang + ".hs";

                URL url = HelpSet.findHelpSet(cl, helpsetName);
                if (url == null) {
                    lang = "en";
                    helpsetName = lang + "/" + NAME + lang + ".hs";
                    url = HelpSet.findHelpSet(cl, helpsetName);
                }

                logger.info("helpset name = {}", helpsetName);
                logger.info("helpset url = {}", url);

                language = lang;
                HelpSet helpSet = new HelpSet(cl, url);
                helpBroker = (DefaultHelpBroker) helpSet.createHelpBroker();
            } catch (HelpSetException e) {
                logger.warn("Unable to create help window", e.getMessage());
            }
        }

        return helpBroker;
    }

    public static void updateComponentUI() {
        if (helpBroker != null) {
            WindowPresentation wp = helpBroker.getWindowPresentation();
            if (wp != null) {
                Window hw = wp.getHelpWindow();
                if (hw != null) {
                    SwingUtilities.updateComponentTreeUI(hw);
                }
            }
        }
    }
}
