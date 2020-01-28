package com.adlitteram.jasmin.utils;

import org.apache.commons.exec.ExecuteException;
import org.apache.commons.exec.ExecuteResultHandler;

public class DefaultExecuteResultHandler implements ExecuteResultHandler {

    public static final int RUNNING = 0;
    public static final int SUCCEED = 1;
    public static final int FAILED = 2;

    private int status = RUNNING;
    private int exitValue;
    private ExecuteException exception;

    @Override
    public void onProcessComplete(int exitValue) {
        this.exitValue = exitValue;
        this.status = SUCCEED;
    }

    @Override
    public void onProcessFailed(ExecuteException e) {
        this.exception = e;
        this.status = FAILED;
    }

    public ExecuteException getException() {
        return exception;
    }

    public int getStatus() {
        return status;
    }

    public int getExitValue() {
        return exitValue;
    }
}
