package com.adlitteram.jasmin;

import com.adlitteram.jasmin.utils.ExtFilter;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Locale;
import org.apache.commons.io.FilenameUtils;

public class LocaleManager {

    private static Applicationable application;
    private static Locale uilocale;

    public static void init(Applicationable app) {
        application = app;
    }

    public static Locale[] getAvailableLocales(String dirname, ExtFilter filter) {
        File dir = new File(dirname);
        Locale[] locales = null;

        if (dir.isDirectory()) {

            File[] files = (filter == null) ? dir.listFiles() : dir.listFiles((java.io.FileFilter) filter);

            ArrayList list = new ArrayList(files.length);
            for (File file : files) {
                String str = FilenameUtils.getBaseName(file.getName());
                if (!list.contains(str)) {
                    list.add(str);
                }
            }

            locales = new Locale[list.size()];
            for (int i = 0; i < list.size(); i++) {
                String str = (String) list.get(i);
                int s = str.indexOf('_');
                if (s < 0) {
                    locales[i] = new Locale(str, "");
                }
                else {
                    locales[i] = new Locale(str.substring(0, s), str.substring(s + 1));
                }
            }

            Arrays.sort(locales, CMP_LOC2);
            return locales;
        }
        return locales;
    }

    // Get best UI Locale
    public static Locale getUILocale() {
        if (uilocale == null) {
            String language = XProp.get("Locale.Language", Locale.getDefault().getLanguage());
            String country = XProp.get("Locale.Country", Locale.getDefault().getCountry());
            setUILocale(matchLocales(new Locale(language, country), getUIAvailableLocales()));
        }
        return uilocale;
    }

    // Set best UI Locale
    public static void setUILocale(Locale loc) {
        uilocale = loc;
        if (uilocale != null) {
            XProp.put("Locale.Language", uilocale.getLanguage());
            XProp.put("Locale.Country", uilocale.getCountry());
        }
    }

    // UI Available Locales
    public static Locale[] getUIAvailableLocales() {
        return getUIAvailableLocales(application.getLangDir());
    }

    // UI Available Locales
    public static Locale[] getUIAvailableLocales(String dirname) {
        File dir = new File(dirname);
        Locale[] locales = null;

        if (dir.isDirectory()) {
            File[] files = dir.listFiles();
            ArrayList list = new ArrayList(files.length);
            for (File file : files) {
                if (!file.isDirectory()) {
                    continue;
                }
                String f = file.getName();
                int s = f.indexOf('_');
                if (s < 0) {
                    list.add(new Locale(f, ""));
                }
                else {
                    list.add(new Locale(f.substring(0, s), f.substring(s + 1)));
                }
            }

            locales = new Locale[list.size()];
            list.toArray(locales);
            Arrays.sort(locales, CMP_LOC2);
        }
        return locales;
    }

    // Match best locale in array
    private static Locale matchLocales(Locale loc, Locale[] locales) {
        if (locales != null) {
            if (Arrays.binarySearch(locales, loc, CMP_LOC2) >= 0) {
                return loc;
            }

            int res = Arrays.binarySearch(locales, loc, CMP_LOC1);
            if (res >= 0) {
                return locales[res];
            }

            res = Arrays.binarySearch(locales, new Locale("en"), CMP_LOC1);
            if (res >= 0) {
                return locales[res];
            }

            if (locales.length > 0) {
                return locales[0];
            }
        }
        return Locale.getDefault();
    }

    private static final Comparator CMP_LOC1 = (o1, o2) -> {
        String s1 = ((Locale) o1).getLanguage();
        String s2 = ((Locale) o2).getLanguage();
        return s1.compareTo(s2);
    };

    private static final Comparator CMP_LOC2 = (o1, o2) -> o1.toString().compareTo(o2.toString());
}
