package com.adlitteram.jasmin;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.apache.commons.io.FileUtils;

public class TempManager {

    private static final Logger logger = LoggerFactory.getLogger(TempManager.class);

    private static Applicationable application;
    private static final ArrayList<File> DIRS = new ArrayList<>();

    public static void init(Applicationable app) {
        application = app;
    }

    public static synchronized void deleteTmpDir() {
        for (int i = DIRS.size() - 1; i >= 0; i--) {
            File dir = DIRS.get(i);
            FileUtils.deleteQuietly(dir);
            DIRS.remove(i);
        }
    }

    public static synchronized File createTmpDir() {
        try {
            Path path = Paths.get(application.getUserConfDir());
            File dir = Files.createTempDirectory(path, "tmp_").toFile();
            DIRS.add(dir);
            return dir;
        }
        catch (IOException ex) {
            logger.warn("Unable to create temporary directory", ex);
            System.exit(1);
            return null;
        }
    }
}
