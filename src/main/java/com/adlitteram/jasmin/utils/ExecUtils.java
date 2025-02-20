package com.adlitteram.jasmin.utils;

import org.apache.commons.exec.*;
import org.apache.commons.lang3.SystemUtils;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;

public class ExecUtils {

    public static int exec(String executable) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        return new DefaultExecutor().execute(commandLine);
    }

    public static int exec(String executable, File dir) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        return executor.execute(commandLine);
    }

    public static int exec(String executable, String[] args) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        return executor.execute(commandLine);
    }

    public static int exec(String executable, String[] args, File dir) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        return executor.execute(commandLine);
    }

    public static int exec(String executable, String[] args, File dir, OutputStream os) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (os != null) {
            executor.setStreamHandler(new PumpStreamHandler(os));
        }
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        return executor.execute(commandLine);
    }

    public static int exec(String executable, String[] args, File dir, OutputStream os, long timeout) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (timeout > 0) {
            executor.setWatchdog(new ExecuteWatchdog(timeout));
        }
        if (os != null) {
            executor.setStreamHandler(new PumpStreamHandler(os));
        }
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        return executor.execute(commandLine);
    }

    public static DefaultExecuteResultHandler execAsync(String executable) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(commandLine, handler);
        return handler;
    }

    public static DefaultExecuteResultHandler execAsync(String executable, File dir) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(commandLine, handler);
        return handler;
    }

    public static DefaultExecuteResultHandler execAsync(String executable, String[] args) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(commandLine, handler);
        return handler;
    }

    public static DefaultExecuteResultHandler execAsync(String executable, String[] args, File dir) throws IOException {
        CommandLine commandLine = new CommandLine(executable);
        commandLine.addArguments(args);
        DefaultExecutor executor = new DefaultExecutor();
        executor.setProcessDestroyer(new ShutdownHookProcessDestroyer());
        if (dir != null) {
            executor.setWorkingDirectory(dir);
        }
        DefaultExecuteResultHandler handler = new DefaultExecuteResultHandler();
        executor.execute(commandLine, handler);
        return handler;
    }

    // Open File Explorer
    public static void execExplorer(String path) throws Exception {
        if (SystemUtils.IS_OS_WINDOWS) {
            execAsync("explorer", new String[]{"/select", path});
        } else {
            throw new Exception("Cannot launch Explorer on this plateform");
        }
    }

    // Open Browser
    public static void execBrowser(String url) throws Exception {
        // new BrowserLauncher().openURLinBrowser(url);
    }
}
