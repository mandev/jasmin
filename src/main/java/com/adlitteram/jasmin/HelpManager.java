
package com.adlitteram.jasmin;

import java.awt.Window;
import java.net.URL;
import javax.help.DefaultHelpBroker;
import javax.help.HelpBroker;
import javax.help.HelpSet;
import javax.help.WindowPresentation;
import javax.swing.SwingUtilities;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HelpManager {

    private static final Logger logger = LoggerFactory.getLogger(HelpManager.class);
    //
    private static Applicationable application ;
    private static final String name = "help_";
    private static String language;
    private static DefaultHelpBroker helpBroker;

    public static void init(Applicationable app) {
        application = app ;
    }

    public static HelpBroker getHelpBroker() {

        String lang = LocaleManager.getUILocale().getLanguage();

        if (helpBroker == null || language == null || !language.equals(lang)) {

            try {
                ClassLoader cl = application.getMainClass().getClassLoader();
                String helpsetName = lang + "/" + name + lang + ".hs";

                URL url = HelpSet.findHelpSet(cl, helpsetName);
                if (url == null) {
                    lang = "en";
                    helpsetName = lang + "/" + name + lang + ".hs";
                    url = HelpSet.findHelpSet(cl, helpsetName);
                }

                logger.info("helpset name = " + helpsetName);
                logger.info("helpset url = " + url);

                language = lang;
                HelpSet helpSet = new HelpSet(cl, url);
                helpBroker = (DefaultHelpBroker) helpSet.createHelpBroker();
            }
            catch (Exception e) {
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
                if (hw != null) SwingUtilities.updateComponentTreeUI(hw);
            }
        }
    }
}
