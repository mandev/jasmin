package com.adlitteram.jasmin.utils;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;

public class PlatformUtils {

    private static final Logger logger = LoggerFactory.getLogger(PlatformUtils.class);

    public static String getHomeDir() {
        if (SystemUtils.getUserHome().isDirectory()) {
            return appendSeparator(SystemUtils.USER_HOME);
        }
        return SystemUtils.IS_OS_WINDOWS ? "C:\\" : "/";
    }

    // Typically :
    // Windows : c:\\program files\\pictbook
    // Linux : /usr/local/pictbook
    // MacOSX : /Application/PictBook.app/Contents/Resources/
    public static String getProgDir(Class klass) {
        return SystemUtils.IS_OS_MAC_OSX ? getMacOSXProgDir(klass) : appendSeparator(SystemUtils.USER_DIR);
        //return appendSeparator(SystemUtils.USER_DIR);
    }

    // jar:file:/D:/distrib/windows/SmileBook/lib/edoc.jar!/com/AdLitteram/eDoc/Resources/default.xml      
    private static String getMacOSXProgDir(Class klass) {
        File file = null;                                     // return file

        try {
            ClassLoader cl = klass.getClassLoader();
            if (cl == null) {
                cl = ClassLoader.getSystemClassLoader();
            }

            URL url = cl.getResource(klass.getName().replace('.', '/') + ".class");

            if (url != null) {
                String ef = url.toExternalForm();
                String lc = ef.toLowerCase();
                while (lc.startsWith("jar:") || lc.startsWith("file:/")) {
                    if (lc.startsWith("jar:")) {
                        if (lc.contains("!/")) {
                            ef = ef.substring("jar:".length(), (ef.indexOf("!/")));
                        } // strip encapsulating "jar:" and "!/..." from JAR url
                        else {
                            ef = ef.substring("jar:".length());
                        } // strip encapsulating "jar:"
                    }
                    if (lc.startsWith("file:/")) {
                        ef = ef.substring("file:/".length());                                 // strip encapsulating "file:/"
                        if (!ef.startsWith("/")) {
                            ef = ("/" + ef);
                        }
                        while (ef.length() > 1 && ef.charAt(1) == '/') {
                            ef = ef.substring(1);
                        }
                    }
                    lc = ef.toLowerCase();
                }
                ef = URLDecoder.decode(ef, "UTF-8");
                file = new File(ef);
                file = file.getParentFile().getParentFile();
                file = new File(file, "Resources");
                if (file.exists()) {
                    file = file.getAbsoluteFile();
                }
            }
        } catch (UnsupportedEncodingException ex) {
            logger.warn("", ex);
        }
        return file == null ? null : appendSlash(file.getPath());
    }

    private static String appendSeparator(String str) {
        String s = FilenameUtils.separatorsToUnix(str);
        return s.endsWith("/") ? str : str + File.separator;
    }

    private static String appendSlash(String str) {
        str = FilenameUtils.separatorsToUnix(str);
        return str.endsWith("/") ? str : str + "/";
    }

    public static String getPlatform() {
        if (SystemUtils.IS_OS_WINDOWS) {
            return "windows";
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            return "macosx";
        } else if (SystemUtils.IS_OS_LINUX) {
            return "linux";
        } else if (SystemUtils.IS_OS_SUN_OS) {
            return "sunos";
        }
        return SystemUtils.OS_NAME;
    }

    public static String getPlatformName(String name) {
        if (SystemUtils.IS_OS_WINDOWS) {
            return name + "_windows";
        } else if (SystemUtils.IS_OS_MAC_OSX) {
            return name + "_macosx";
        } else if (SystemUtils.IS_OS_LINUX) {
            return name + "_linux";
        } else if (SystemUtils.IS_OS_SUN_OS) {
            return name + "_sunos";
        }
        return name + SystemUtils.OS_NAME;
    }
}
