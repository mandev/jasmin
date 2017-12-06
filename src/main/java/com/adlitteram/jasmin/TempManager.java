/*
 * TempManager.java
 *
 * Created on 30 ao�t 2005, 23:32
 *
 * To change this template, choose Tools | Options and locate the template under
 * the Source Creation and Management node. Right-click the template and choose
 * Open. You can then make changes to the template in the Source Editor.
 */

package com.adlitteram.jasmin;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.nio.channels.FileLock;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.apache.commons.io.FileUtils;

public class TempManager {

    private static final Logger logger = LoggerFactory.getLogger(TempManager.class);
    //
    private static Applicationable application;
    private static ArrayList dirList;
    private static ArrayList rafList;
    private static ArrayList lockList;    // IMPORTANT sinon le lock est perdu !!!!!!!!!!!!

    public static void init(Applicationable app) {
        application = app;
    }

    public static synchronized void releaseTmpDir() {
        try {
            for (int i = 0; i < rafList.size(); i++) {
                RandomAccessFile rdFile = (RandomAccessFile) rafList.get(i);
                rdFile.close(); // Automatically releases the lock
            }

            for (int i = 0; i < dirList.size(); i++) {
                File dir = (File) dirList.get(i);
                FileUtils.deleteDirectory(dir);
            }
        }
        catch (Exception e) {
            logger.warn("", e);
        }
    }

    public static synchronized File createTmpDir() {

        if (lockList == null) lockList = new ArrayList();
        if (rafList == null) rafList = new ArrayList();
        if (dirList == null) dirList = new ArrayList();

        int count = 1;
        while (count < 9999) {
            File tmpDir = new File(application.getUserConfDir() + "tmp." + String.valueOf(count++));
            if (!tmpDir.exists()) tmpDir.mkdirs();

            File lockFile = new File(tmpDir, "lock");
            RandomAccessFile raFile = null;
            try {
                raFile = new RandomAccessFile(lockFile, lockFile.exists() ? "r" : "rw");
                FileChannel channel = raFile.getChannel();
                FileLock lock = channel.tryLock();

                if (lock != null) {
                    rafList.add(raFile);
                    dirList.add(tmpDir);

                    File[] files = tmpDir.listFiles();
                    for (int j = 0; j < files.length; j++) {
                        if (!files[j].equals(lockFile)) FileUtils.forceDelete(files[j]);
                    }
                    return tmpDir;
                }
                else {
                    channel.close();
                    raFile.close();
                }
            }
            catch (Exception e) {
                logger.info("createTmpDir", e.toString());
                //logger.info(lockFile + " is already locked");
                try {
                    if (raFile != null) raFile.close();
                }
                catch (IOException ex) {
                    logger.warn("", ex);
                }

            }
        }

        logger.error("Unable to create temporary directory - too many tries (10000)");
        System.exit(1);
        return null;
    }

    public static boolean isLocked(File file) {
        if (!file.exists()) return false;

        RandomAccessFile raf = null;
        try {
            raf = new RandomAccessFile(file, "r");
            FileChannel channel = raf.getChannel();
            FileLock lock = channel.tryLock();
            channel.close();
            raf.close();
            if (lock == null) return true;
        }
        catch (Exception e) {
            try {
                if (raf != null) raf.close();
            }
            catch (IOException ex) {
                logger.warn("", ex);
            }
            return true;
        }
        return false;
    }
}
