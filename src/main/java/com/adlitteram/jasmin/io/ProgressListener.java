package com.adlitteram.jasmin.io;

public interface ProgressListener {

    public void bytesTransferred(long count);

    public void finished(Object object);

    public void init(Object object);
}
