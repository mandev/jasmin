package com.adlitteram.jasmin.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class ZipUtils {

    private static final Logger logger = LoggerFactory.getLogger(ZipUtils.class);

    // This is an utility class
    private ZipUtils() {
    }

    public static void _putDirectoryEntry(String entryName, ZipOutputStream out) throws IOException {
        ZipEntry zentry = new ZipEntry(entryName + '/');
        out.putNextEntry(zentry);
        out.closeEntry();
    }

    public static void _putFileEntry(File file, String entryName, ZipOutputStream out) throws IOException {
        try (FileInputStream in = new FileInputStream(file)) {
            _zip(entryName, in, out);
        }
    }

    public static void _zip(String entryName, InputStream in, ZipOutputStream out) throws IOException {
        ZipEntry zentry = new ZipEntry(entryName);
        out.putNextEntry(zentry);
        // Transfer bytes from the input stream to the ZIP file
        IOUtils.copy(in, out);
        out.closeEntry();
    }

    public static void _zip(String entryName, File file, ZipOutputStream out) throws IOException {
        logger.info("Compressing: " + entryName + " - file: " + file);

        if (file.isDirectory()) {
            if (!entryName.endsWith("/")) {
                entryName += '/';
            }
            ZipEntry zentry = new ZipEntry(entryName);
            out.putNextEntry(zentry);
            out.closeEntry();
            File[] files = file.listFiles();
            for (int i = 0, len = files.length; i < len; i++) {
                _zip(entryName + files[i].getName(), files[i], out);
            }
        }
        else {
            try (InputStream in = new FileInputStream(file)) {
                _zip(entryName, in, out);
            }
        }
    }

    public static void _zip(File[] files, ZipOutputStream out, String prefix) throws IOException {
        if (prefix != null) {
            int len = prefix.length();
            if (len == 0) {
                prefix = null;
            }
            else if (prefix.charAt(len - 1) != '/') {
                prefix += '/';
            }
        }
        for (int i = 0, len = files.length; i < len; i++) {
            String name = prefix != null ? prefix + files[i].getName() : files[i].getName();
            _zip(name, files[i], out);
        }
    }

    public static void zip(File file, OutputStream out, String prefix) throws IOException {
        if (prefix != null) {
            int len = prefix.length();
            if (len == 0) {
                prefix = null;
            }
            else if (prefix.charAt(len - 1) != '/') {
                prefix += '/';
            }
        }
        String name = prefix != null ? prefix + file.getName() : file.getName();
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(out);
            _zip(name, file, zout);
        }
        finally {
            if (zout != null) {
                zout.finish();
            }
        }
    }

    public static void zip(File[] files, OutputStream out, String prefix) throws IOException {
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(out);
            _zip(files, zout, prefix);
        }
        finally {
            if (zout != null) {
                zout.finish();
            }
        }
    }

    public static void zipDirectory(File file, File zip) throws IOException {
        if (!file.isDirectory()) {
            throw new IOException(file + " is not a directory");
        }
        zip(file.listFiles(), zip);
    }

    public static void zip(File file, File zip) throws IOException {
        try (OutputStream out = new FileOutputStream(zip)) {
            zip(file, out, null);
        }
    }

    public static void zip(File[] files, File zip) throws IOException {
        try (OutputStream out = new FileOutputStream(zip)) {
            zip(files, out, null);
        }
    }

    public static void zip(File file, File zip, String prefix) throws IOException {
        try (OutputStream out = new FileOutputStream(zip)) {
            zip(file, out, prefix);
        }
    }

    public static void zip(File[] files, File zip, String prefix) throws IOException {
        try (OutputStream out = new FileOutputStream(zip)) {
            zip(files, out, prefix);
        }
    }

    public static void zipFilesUsingPrefix(String prefix, File[] files, OutputStream out) throws IOException {
        try (ZipOutputStream zout = new ZipOutputStream(out)) {
            if (prefix != null && prefix.length() > 0) {
                int p = prefix.indexOf('/');
                while (p > -1) {
                    _putDirectoryEntry(prefix.substring(0, p), zout);
                    p = prefix.indexOf(p + 1, '/');
                }
                _putDirectoryEntry(prefix, zout);
                prefix += '/';
            }
            else {
                prefix = "";
            }
            for (File file : files) {
                _putFileEntry(file, prefix + file.getName(), zout);
            }
        }
    }

    public static void unzip(String prefix, InputStream zipStream, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(zipStream)) {
            unzip(prefix, in, dir);
        }
    }

    public static void unzip(InputStream zipStream, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(zipStream)) {
            unzip(in, dir);
        }
    }

    public static void unzip(String prefix, URL zip, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(zip.openStream())) {
            unzip(prefix, in, dir);
        }
    }

    public static void unzip(URL zip, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(zip.openStream())) {
            unzip(in, dir);
        }
    }

    public static void unzip(String prefix, File zip, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(zip))) {
            unzip(prefix, in, dir);
        }
    }

    public static void unzip(File zip, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(new FileInputStream(zip))) {
            unzip(in, dir);
        }
    }

    public static void unzip(String prefix, ZipInputStream in, File dir) throws IOException {
        dir.mkdirs();
        ZipEntry entry = in.getNextEntry();
        while (entry != null) {
            if (!entry.getName().startsWith(prefix)) {
                entry = in.getNextEntry();
                continue;
            }
            File file = new File(dir, entry.getName().substring(prefix.length()));
            if (entry.isDirectory()) {
                file.mkdirs();
            }
            else {
                file.getParentFile().mkdirs();
                FileUtils.copyToFile(in, file);
            }
            entry = in.getNextEntry();
        }
    }

    public static void unzip(ZipInputStream in, File dir) throws IOException {
        dir.mkdirs();
        ZipEntry entry = in.getNextEntry();
        while (entry != null) {
            File file = new File(dir, entry.getName());
            if (entry.isDirectory()) {
                file.mkdirs();
            }
            else {
                file.getParentFile().mkdirs();
                FileUtils.copyToFile(in, file);
            }
            entry = in.getNextEntry();
        }
    }

    public static void unzipIgnoreDirs(ZipInputStream in, File dir) throws IOException {
        dir.mkdirs();
        ZipEntry entry = in.getNextEntry();
        while (entry != null) {
            String entryName = entry.getName();
            if (entry.isDirectory()) {
            }
            else {
                int p = entryName.lastIndexOf('/');
                if (p > -1) {
                    entryName = entryName.substring(p + 1);
                }
                FileUtils.copyToFile(in, new File(dir, entryName));
            }
            entry = in.getNextEntry();
        }
    }

    public static void unzipIgnoreDirs(InputStream zipStream, File dir) throws IOException {
        try (ZipInputStream in = new ZipInputStream(zipStream)) {
            unzipIgnoreDirs(in, dir);
        }
    }

}
