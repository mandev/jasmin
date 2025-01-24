package com.adlitteram.jasmin.utils;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.io.FileUtils;

import java.io.*;

public class TarUtils {

    public static void untar(File targzFile, File untarDir) throws IOException {
        if (!targzFile.isFile()) {
            throw new IOException("Untar source is not a file!");
        }

        if (!untarDir.exists()) {
            untarDir.mkdirs();
        }

        if (!untarDir.isDirectory()) {
            throw new IOException("Untar destination is not a directory!");
        }

        try (FileInputStream fis = new FileInputStream(targzFile);
             TarArchiveInputStream tais = new TarArchiveInputStream(fis, 16384)) {

            TarArchiveEntry tarEntry;
            while ((tarEntry = tais.getNextTarEntry()) != null) {
                File dstPath = new File(untarDir, tarEntry.getName());
                if (tarEntry.isDirectory()) {
                    dstPath.mkdirs();
                } else {
                    FileUtils.copyToFile(tais, dstPath);
                }
            }
        }
    }

    public static void tar(File file, File tarFile) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(tarFile);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(bos)) {

            addFileToTar(taos, tarFile.getPath(), "");
        }
    }

    public static void untargz(File targzFile, File untarDir) throws IOException {
        if (!targzFile.isFile()) {
            throw new IOException("Untar source is not a file!");
        }

        if (!untarDir.exists()) {
            untarDir.mkdirs();
        }

        if (!untarDir.isDirectory()) {
            throw new IOException("Untar destination is not a directory!");
        }

        try (FileInputStream fis = new FileInputStream(targzFile);
             GzipCompressorInputStream gcis = new GzipCompressorInputStream(fis);
             TarArchiveInputStream tais = new TarArchiveInputStream(gcis)) {

            TarArchiveEntry tarEntry;
            while ((tarEntry = tais.getNextTarEntry()) != null) {
                File dstPath = new File(untarDir, tarEntry.getName());
                if (!tarEntry.isDirectory()) {
                    FileUtils.copyToFile(tais, dstPath);
                } else {
                    dstPath.mkdirs();
                }
            }
        }
    }

    public static void targz(File file, File targz) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(targz);
             BufferedOutputStream bos = new BufferedOutputStream(fos);
             GzipCompressorOutputStream gcos = new GzipCompressorOutputStream(bos);
             TarArchiveOutputStream taos = new TarArchiveOutputStream(gcos)) {

            addFileToTar(taos, targz.getPath(), "");
        }
    }

    private static void addFileToTar(TarArchiveOutputStream taos, String path, String base) throws IOException {
        File file = new File(path);
        String entryName = base + file.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(file, entryName);
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        taos.putArchiveEntry(tarEntry);

        if (file.isFile()) {
            FileUtils.copyFile(file, taos);
            taos.closeArchiveEntry();
        } else {
            taos.closeArchiveEntry();
            File[] children = file.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTar(taos, child.getPath(), entryName + "/");
                }
            }
        }
    }
}
