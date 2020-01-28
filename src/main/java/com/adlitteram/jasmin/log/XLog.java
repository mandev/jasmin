package com.adlitteram.jasmin.log;

import com.adlitteram.jasmin.Applicationable;
import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.logging.ConsoleHandler;
import java.util.logging.FileHandler;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

public class XLog {

    public static void init(Applicationable app) {
        init(app.getUserLogDir(), app.getLogName());
    }

    public static void init(String logDir, String logName) {
        Level DEFAULT_LEVEL = Level.INFO;

        // Get root logger
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(DEFAULT_LEVEL);

        // Remove all default handlers
        Handler[] handlers = rootLogger.getHandlers();
        for (Handler handler : handlers) {
            rootLogger.removeHandler(handler);
        }

        // Create standard formatter
        StreamFormatter streamFormatter = new StreamFormatter(false, true);

        // Log to Console
        ConsoleHandler consoleHandler = new ConsoleHandler();
        consoleHandler.setLevel(DEFAULT_LEVEL);
        consoleHandler.setFormatter(streamFormatter);
        rootLogger.addHandler(consoleHandler);

        // Log to File
        if (logDir != null) {
            try {
                File dir = new File(logDir);
                if (!dir.exists()) {
                    dir.mkdirs();
                }
                String pattern = logDir + "/" + logName + "_%g.log";
                FileHandler fileHandler = new FileHandler(pattern, 1024 * 1024 * 5, 50);
                fileHandler.setLevel(Level.ALL);
                fileHandler.setFormatter(streamFormatter);
                rootLogger.addHandler(fileHandler);
            }
            catch (IOException | SecurityException e) {
                e.printStackTrace();
            }
        }
    }

    public static Logger getRootLogger() {
        return Logger.getLogger("");
    }

    public static void close() {
        LogManager lm = LogManager.getLogManager();
        for (Enumeration<String> names = lm.getLoggerNames(); names.hasMoreElements();) {
            String name = names.nextElement();
            Logger logger = lm.getLogger(name);
            Handler[] handlers = logger.getHandlers();
            for (Handler handler : handlers) {
                handler.close();
            }
        }
    }
}
