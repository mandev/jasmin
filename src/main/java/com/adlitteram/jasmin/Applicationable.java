package com.adlitteram.jasmin;

import java.awt.*;

public interface Applicationable {

    String getApplicationName();

    String getApplicationRelease();

    String getApplicationBuild();

    Class<?> getMainClass();

    Window getMainFrame();

    String getUserConfDir();

    String getUserLogDir();

    String getUserPropFile();

    String getLangDir();

    String getLogName();

}
