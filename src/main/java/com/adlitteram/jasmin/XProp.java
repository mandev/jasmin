/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 2.1
 * @author Emmanuel Deviller
 */
package com.adlitteram.jasmin;

import org.slf4j.LoggerFactory;
import com.adlitteram.jasmin.utils.GuiUtils;
import com.adlitteram.jasmin.property.XPropertiesReader;
import com.adlitteram.jasmin.utils.StrUtils;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.Map;
import java.util.Properties;
import org.slf4j.Logger;
import javax.swing.Icon;
import org.apache.commons.io.IOUtils;

public class XProp {

    private static final Logger LOGGER = LoggerFactory.getLogger(XProp.class);
    private static Applicationable application;
    private static Properties properties;    // the properties

    public static void init(Applicationable app) {
        application = app;

        Properties resProps = new Properties();
        properties = new Properties(resProps);

        // Load user's properties
        File file = new File(app.getUserPropFile());
        if (file.exists() && file.canRead()) {
            LOGGER.info("Loading resource " + file.getPath());
            XPropertiesReader.read(properties, file.getPath());
        }
        
        // Load program resources
        loadResource(resProps, getResource("text.xml"));
        loadResource(resProps, getResource("keys.xml"));
        loadResource(resProps, getResource("tips.xml"));
        loadResource(resProps, getResource("icons.xml"));

        // Load custom resources
        loadResource(resProps, getResource("custom.xml"));

        // Load default resources
        URL url = app.getMainClass().getResource("resource/default.xml");
        loadResource(properties, URLtoURI(url));
    }

    public static void loadResource(Properties resProps, URI uri) {
        if (uri != null) {
            LOGGER.info("Loading resource " + uri.toString());
            XPropertiesReader.read(resProps, uri);
        }
    }

    public static URI getResource(String filename) {

        // User
        File file = new File(application.getUserConfDir(), filename);
        LOGGER.info("Trying userconf file: " + file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return file.toURI();
        }

        // Config/Localized
        file = new File(application.getLangDir() + LocaleManager.getUILocale().toString() + File.separator + filename);
        LOGGER.info("Trying local file: " + file);
        if (file.exists() && file.canRead() && file.isFile()) {
            return file.toURI();
        }

        // Default
        LOGGER.info("Trying default: " + "resource/" + filename);
        URL url = application.getMainClass().getResource("resource/" + filename);
        if (url != null) {
            return URLtoURI(url);
        }

        LOGGER.info("URL not found : resource/" + filename);
        return null;
    }

    private static URI URLtoURI(URL url) {
        try {
            if (url != null) {
                return url.toURI();
            }
        }
        catch (URISyntaxException ex) {
            LOGGER.warn("XProps.URLtoURI() : {}", url);
        }
        return null;
    }

    // Get the current Properties object
    public static Properties getProperties() {
        return properties;
    }

    // Get
    public static void putProperty(String key, String value) {

        // XLog.logger(key + " " + value) ;
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
        return (b != null) ? Boolean.valueOf(b).booleanValue() : dft;
    }

    public static int getInt(String key, int dft) {
        try {
            return Integer.parseInt(getProperty(key));
        }
        catch (Exception e) {
            return dft;
        }
    }

    public static float getFloat(String key, float dft) {
        try {
            return Float.parseFloat(getProperty(key));
        }
        catch (Exception e) {
            return dft;
        }
    }

    public static double getDouble(String key, double dft) {
        try {
            return Double.parseDouble(getProperty(key));
        }
        catch (Exception e) {
            return dft;
        }
    }

    public static long getLong(String key, long dft) {
        try {
            return Long.parseLong(getProperty(key));
        }
        catch (Exception e) {
            return dft;
        }
    }

    // Unset property
    public static void unsetProperty(String key) {

        // if (defaultProps.get(name) != null) props.put(name, "");
        // else  props.remove(name);
        properties.remove(key);
    }

    public static Icon getIcon(String key) {
        String path = getProperty(key);
        if (path != null) {
            return GuiUtils.loadIcon(path, application.getMainClass());
        }
        else {
            LOGGER.info("icon {} is undefined", key);
            return null;
        }
    }

    // Save XML Properties
    public static void saveProperties(String fileName, String version, String build) {

        //unsetProperty("ExportPDF.Password");

        put("properties.release", version);
        put("properties.build", build);

        StringBuffer buffer = new StringBuffer(512);
        buffer.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        buffer.append("<!-- Last save: ").append(new Date()).append(" -->\n");
        buffer.append("<!-- User Properties -->\n");
        buffer.append("<properties>\n");
        for (Map.Entry entry : properties.entrySet()) {
            buffer.append("  <property key=\"").append(entry.getKey()).append("\" value=\"");
            StrUtils.escapeXml(buffer, (String) entry.getValue());
            buffer.append("\" /> \n");
        }
        buffer.append("</properties>");

        OutputStreamWriter out = null;
        try {
            out = new OutputStreamWriter(new FileOutputStream(fileName), "UTF-8");
            out.write(buffer.toString());
        }
        catch (Exception e) {
            LOGGER.warn("", e);
        }
        finally {
            IOUtils.closeQuietly(out);
        }
    }
}
