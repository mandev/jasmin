package com.adlitteram.jasmin;

import com.adlitteram.jasmin.property.XProp;
import java.text.MessageFormat;

public class Message {

    private Message() {
    }

    public static String get(String key, Object arg1) {
        return get(key, new Object[]{arg1});
    }

    public static String get(String key, Object arg1, Object arg2) {
        return get(key, new Object[]{arg1, arg2});
    }

    public static String get(String key, Object arg1, Object arg2, Object arg3) {
        return get(key, new Object[]{arg1, arg2, arg3});
    }

    public static String get(String key) {
        return XProp.getProperty(key, key);
    }

    public static String get(String key, Object[] args) {
        try {
            return (args == null) ? get(key) : MessageFormat.format(get(key), args);
        }
        catch (Exception e) {
            return key;
        }
    }
}
