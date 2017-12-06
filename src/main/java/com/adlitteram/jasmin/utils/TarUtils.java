/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.utils;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.compress.archivers.tar.TarArchiveOutputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorInputStream;
import org.apache.commons.compress.compressors.gzip.GzipCompressorOutputStream;
import org.apache.commons.compress.utils.IOUtils;

public class TarUtils {

    public static void untar(File targzFile, File untarDir) throws IOException {
        FileInputStream fis = null;
        TarArchiveInputStream tais = null;

        try {
            if (!targzFile.isFile()) {
                throw new IOException("Untar source is not a file!");
            }

            if (!untarDir.exists()) {
                untarDir.mkdirs();
            }

            if (!untarDir.isDirectory()) {
                throw new IOException("Untar destination is not a directory!");
            }

            //fis = new FileInputStream(targzFile);
            int bufsize = 32 * 512;
            fis = new FileInputStream(targzFile);
            tais = new TarArchiveInputStream(fis, bufsize);

            TarArchiveEntry tarEntry;
            while ((tarEntry = tais.getNextTarEntry()) != null) {
                File dstPath = new File(untarDir, tarEntry.getName());
                if (!tarEntry.isDirectory()) {
                    try (FileOutputStream fos = new FileOutputStream(dstPath)) {
                        IOUtils.copy(tais, fos, bufsize);
                    }
                }
                else {
                    dstPath.mkdirs();
                }
            }
        }
        finally {
            if (tais != null) {
                tais.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static void tar(File file, File tarFile) throws IOException {

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        TarArchiveOutputStream taos = null;

        try {
            fos = new FileOutputStream(tarFile);
            bos = new BufferedOutputStream(fos);
            taos = new TarArchiveOutputStream(bos);
            addFileToTar(taos, tarFile.getPath(), "");
        }
        finally {
            if (taos != null) {
                taos.finish();
            }
            if (taos != null) {
                taos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    public static void untargz(File targzFile, File untarDir) throws IOException {
        FileInputStream fis = null;
        TarArchiveInputStream tais = null;
        GzipCompressorInputStream gcis = null;

        try {
            if (!targzFile.isFile()) {
                throw new IOException("Untar source is not a file!");
            }

            if (!untarDir.exists()) {
                untarDir.mkdirs();
            }

            if (!untarDir.isDirectory()) {
                throw new IOException("Untar destination is not a directory!");
            }

            fis = new FileInputStream(targzFile);
            gcis = new GzipCompressorInputStream(fis);
            tais = new TarArchiveInputStream(gcis);

            TarArchiveEntry tarEntry;
            while ((tarEntry = tais.getNextTarEntry()) != null) {
                File dstPath = new File(untarDir, tarEntry.getName());
                if (!tarEntry.isDirectory()) {
                    try (FileOutputStream fos = new FileOutputStream(dstPath)) {
                        IOUtils.copy(tais, fos);
                    }
                }
                else {
                    dstPath.mkdirs();
                }
            }
        }
        finally {
            if (tais != null) {
                tais.close();
            }
            if (gcis != null) {
                gcis.close();
            }
            if (fis != null) {
                fis.close();
            }
        }
    }

    public static void targz(File file, File targz) throws IOException {

        FileOutputStream fos = null;
        BufferedOutputStream bos = null;
        GzipCompressorOutputStream gcos = null;
        TarArchiveOutputStream taos = null;

        try {
            fos = new FileOutputStream(targz);
            bos = new BufferedOutputStream(fos);
            gcos = new GzipCompressorOutputStream(bos);
            taos = new TarArchiveOutputStream(gcos);
            addFileToTar(taos, targz.getPath(), "");
        }
        finally {
            if (taos != null) {
                taos.finish();
            }
            if (taos != null) {
                taos.close();
            }
            if (gcos != null) {
                gcos.close();
            }
            if (bos != null) {
                bos.close();
            }
            if (fos != null) {
                fos.close();
            }
        }
    }

    private static void addFileToTar(TarArchiveOutputStream taos, String path, String base) throws IOException {
        File f = new File(path);
        String entryName = base + f.getName();
        TarArchiveEntry tarEntry = new TarArchiveEntry(f, entryName);
        taos.setLongFileMode(TarArchiveOutputStream.LONGFILE_GNU);
        taos.putArchiveEntry(tarEntry);

        if (f.isFile()) {
            IOUtils.copy(new FileInputStream(f), taos);
            taos.closeArchiveEntry();
        }
        else {
            taos.closeArchiveEntry();

            File[] children = f.listFiles();
            if (children != null) {
                for (File child : children) {
                    addFileToTar(taos, child.getPath(), entryName + "/");
                }
            }
        }
    }
}
