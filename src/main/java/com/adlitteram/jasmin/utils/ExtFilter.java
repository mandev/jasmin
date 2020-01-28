package com.adlitteram.jasmin.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.StringTokenizer;
import org.apache.commons.io.FilenameUtils;

public class ExtFilter extends javax.swing.filechooser.FileFilter implements java.io.FileFilter, FilenameFilter {

    public static final ExtFilter EXE = new ExtFilter("exe|bin|app|sh", "Executable files");
    public static final ExtFilter HTM = new ExtFilter("htm|html", "Html Files (*.htm, *.html)");
    public static final ExtFilter IMG = new ExtFilter("gif|jpg|jpeg|tiff|tif|png|ps|eps|pdf", "Images files");
    public static final ExtFilter PDF = new ExtFilter("pdf", "PDF files (*.pdf)");
    public static final ExtFilter TXT = new ExtFilter("txt", "Text Files (*.txt)");
    public static final ExtFilter ZIP = new ExtFilter("zip", "Zip files");
    public static final ExtFilter XML = new ExtFilter("xml", "XML files");
    public static final ExtFilter ICC = new ExtFilter("icm|icc|pf", "ICC Profiles (*.icm, *.icc, *.pf)");
    public static final ExtFilter IPTC = new ExtFilter("iptc", "Iptc files");
    public static final ExtFilter IPTC_ = new ExtFilter("iptc", "Iptc files", false);
    public static final ExtFilter DIC_ = new ExtFilter("dic", "Dictionary", false);
    public static final ExtFilter ICC_ = new ExtFilter("icc|icm|pf", "ICC Profiles", false);
    public static final ExtFilter XML_ = new ExtFilter("xml", "XML files", false);
    public static final ExtFilter ZIP_ = new ExtFilter("zip", "Zip files", false);
    //
    private String[] exts;
    private String desc;
    private boolean acceptDir;

    public ExtFilter(String ext, String desc) {
        this(ext, desc, true);
    }

    public ExtFilter(String ext, String desc, boolean acceptDir) {
        this.desc = desc;
        this.acceptDir = acceptDir;

        StringTokenizer st = new StringTokenizer(ext, "|");
        exts = new String[st.countTokens()];
        for (int i = 0; i < exts.length; i++) {
            exts[i] = (String) st.nextToken();
        }
    }

    @Override
    public boolean accept(File dir, String name) {
        return accept(new File(dir, name));
    }

    @Override
    public boolean accept(File f) {
        if (f.isDirectory()) {
            return acceptDir;
        }

        String fext = FilenameUtils.getExtension(f.getName());
        for (String ext : exts) {
            if (ext.equalsIgnoreCase(fext)) {
                return true;
            }
        }
        return false;
    }

    @Override
    public String getDescription() {
        return desc;
    }

    public String[] getExtensions() {
        return exts;
    }
}
