package com.adlitteram.jasmin;

import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.log.XLog;
import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.Window;
import java.io.File;
import static org.apache.commons.lang3.SystemUtils.JAVA_VENDOR;
import static org.apache.commons.lang3.SystemUtils.JAVA_VERSION;
import static org.apache.commons.lang3.SystemUtils.OS_NAME;
import static org.apache.commons.lang3.SystemUtils.OS_VERSION;
import org.slf4j.Logger;
import static org.slf4j.LoggerFactory.getLogger;

public abstract class Application implements Applicationable {

    public static void init(Applicationable app) {
        XLog.init(app);

        Logger logger = getLogger(Application.class);
        logger.info("OS_NAME = " + OS_NAME);
        logger.info("OS_VERSION = " + OS_VERSION);
        logger.info("JAVA_VERSION = " + JAVA_VERSION);
        logger.info("JAVA_VENDOR = " + JAVA_VENDOR);
        logger.info("APPLICATION_NAME = " + app.getApplicationName());
        logger.info("APPLICATION_RELEASE = " + app.getApplicationRelease());
        logger.info("APPLICATION_BUILD = " + app.getApplicationBuild());

        TempManager.init(app);
        LocaleManager.init(app);
        XProp.init(app);
        LookManager.init(app);
        HelpManager.init(app);
        GuiUtils.init(app);
    }

    public void init() {
        File dir = new File(getUserConfDir());
        if (!dir.exists()) {
            dir.mkdir();
        }
        init(this);
    }

    @Override
    public abstract String getApplicationName();

    @Override
    public abstract String getApplicationRelease();

    @Override
    public abstract String getApplicationBuild();

    @Override
    public abstract Class getMainClass();

    @Override
    public abstract Window getMainFrame();

    @Override
    public abstract String getUserConfDir();

    @Override
    public abstract String getUserLogDir();

    @Override
    public abstract String getUserPropFile();

    @Override
    public abstract String getLangDir();

    @Override
    public abstract String getLogName();
}
