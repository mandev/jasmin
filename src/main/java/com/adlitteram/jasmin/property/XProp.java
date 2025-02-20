package com.adlitteram.jasmin.property;

import com.adlitteram.jasmin.Applicationable;
import com.adlitteram.jasmin.LocaleManager;
import com.adlitteram.jasmin.utils.GuiUtils;
import com.adlitteram.jasmin.utils.StrUtils;
import org.apache.commons.lang3.SystemUtils;
import org.apache.commons.lang3.math.NumberUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.Map;
import java.util.Properties;

public final class XProp {

    private static final Logger LOGGER = LoggerFactory.getLogger(XProp.class);
    private static Applicationable application;
    private static Properties properties;    // the properties

    private XProp() {
    }

    public static void init(Applicationable app) {
        application = app;

        Properties resProps = new Properties();
        properties = new Properties(resProps);

        // Load user's properties
        File file = new File(app.getUserPropFile());
        if (file.exists() && file.canRead()) {
            LOGGER.info("Loading resource {}", file.getPath());
            XPropertiesReader.read(properties, file.getPath());
        }

        // Load program resources
        loadResource(resProps, getResource("text.xml"));
        loadResource(resProps, getResource("tips.xml"));
        loadResource(resProps, getResource("icons.xml"));

        if (SystemUtils.IS_OS_MAC_OSX) {
            loadResource(resProps, getResource("keys-mac.xml"));
        } else {
            loadResource(resProps, getResource("keys.xml"));
        }

        // Load custom resources
        loadResource(resProps, getResource("custom.xml"));

        // Load default resources
        URL url = app.getMainClass().getResource("resource/default.xml");
        loadResource(properties, toURI(url));
    }

    public static void loadResource(Properties resProps, URI uri) {
        if (uri != null) {
            LOGGER.info("Loading resource {}", uri);
            XPropertiesReader.read(resProps, uri);
        }
    }

    public static URI getResource(String filename) {

        // User
        File file = new File(application.getUserConfDir(), filename);
        LOGGER.info("Trying userconf file: {}", file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return file.toURI();
        }

        // Config/Localized
        file = new File(application.getLangDir() + LocaleManager.getUILocale().toString() + File.separator + filename);
        LOGGER.info("Trying local file: {}", file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return file.toURI();
        }

        // Default
        LOGGER.info("Trying default: /{}", filename);
        URL url = application.getMainClass().getResource("/" + filename);
        if (url != null) {
            return toURI(url);
        }

        // Default
        LOGGER.info("Trying resource: resource/{}", filename);
        URL url2 = application.getMainClass().getResource("resource/" + filename);
        if (url2 != null) {
            return toURI(url2);
        }

        LOGGER.info("URL not found : resource/{}", filename);
        return null;
    }

    public static InputStream getResourceAsTream(String filename) throws FileNotFoundException {
        // User
        File file = new File(application.getUserConfDir(), filename);
        LOGGER.info("Trying userconf file: {}", file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return new BufferedInputStream(new FileInputStream(file));
        }

        // Config/Localized
        file = new File(application.getLangDir() + LocaleManager.getUILocale().toString() + File.separator + filename);
        LOGGER.info("Trying local file: {}", file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return new BufferedInputStream(new FileInputStream(file));
        }

        // Default
        LOGGER.info("Trying default: /{}", filename);
        InputStream is = application.getMainClass().getResourceAsStream("/" + filename);
        if (is != null) {
            return is;
        }

        // Default
        LOGGER.info("Trying resource: resource/{}", filename);
        is = application.getMainClass().getResourceAsStream("resource/" + filename);
        if (is != null) {
            return is;
        }

        throw new FileNotFoundException(filename);
    }

    private static URI toURI(URL url) {
        try {
            if (url != null) {
                return url.toURI();
            }
        } catch (URISyntaxException ex) {
            LOGGER.warn("XProps.URLtoURI() : {}", url);
        }
        return null;
    }

    public static void putProperty(String key, String value) {
        if ((key != null) && (value != null)) {
            properties.put(key, value);
        }
    }

    public static void put(String key, String value) {
        putProperty(key, value);
    }

    public static void put(String key, boolean value) {
        putProperty(key, String.valueOf(value));
    }

    public static void put(String key, int value) {
        putProperty(key, String.valueOf(value));
    }

    public static void put(String key, long value) {
        putProperty(key, String.valueOf(value));
    }

    public static void put(String key, float value) {
        putProperty(key, String.valueOf(value));
    }

    public static void put(String key, double value) {
        putProperty(key, String.valueOf(value));
    }

    // Get
    public static String getProperty(String key) {
        return properties.getProperty(key);
    }

    public static String get(String key) {
        return getProperty(key);
    }

    public static String getProperty(String key, String dft) {
        return properties.getProperty(key, dft);
    }

    public static String get(String key, String dft) {
        return getProperty(key, dft);
    }

    public static boolean getBoolean(String key, boolean dft) {
        String b = getProperty(key);
        return (b != null) ? Boolean.parseBoolean(b) : dft;
    }

    public static int getInt(String key, int dft) {
        return NumberUtils.toInt(getProperty(key), dft);
    }

    public static float getFloat(String key, float dft) {
        return NumberUtils.toFloat(getProperty(key), dft);
    }

    public static double getDouble(String key, double dft) {
        return NumberUtils.toDouble(getProperty(key), dft);
    }

    public static long getLong(String key, long dft) {
        return NumberUtils.toLong(getProperty(key), dft);
    }

    // Unset property
    public static void unsetProperty(String key) {
        properties.remove(key);
    }

    public static Icon getIcon(String key) {
        String path = getProperty(key);
        if (path != null) {
            return GuiUtils.loadIcon(path, application.getMainClass());
        } else {
            LOGGER.info("icon {} is undefined", key);
            return null;
        }
    }

    // Save XML Properties
    public static void saveProperties(String fileName, String version, String build) {

        put("properties.release", version);
        put("properties.build", build);

        StringBuilder buffer = new StringBuilder(512);
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buffer.append("<!-- Last save: ").append(new Date()).append(" -->\n");
        buffer.append("<!-- User Properties -->\n");
        buffer.append("<properties>\n");
        for (Map.Entry<Object, Object> entry : properties.entrySet()) {
            buffer.append("  <property key=\"").append(entry.getKey()).append("\" value=\"");
            StrUtils.escapeXml(buffer, (String) entry.getValue());
            buffer.append("\" /> \n");
        }
        buffer.append("</properties>");

        try (FileOutputStream fos = new FileOutputStream(fileName); OutputStreamWriter out = new OutputStreamWriter(fos, StandardCharsets.UTF_8)) {
            out.write(buffer.toString());
        } catch (IOException e) {
            LOGGER.warn("", e);
        }

    }
}
