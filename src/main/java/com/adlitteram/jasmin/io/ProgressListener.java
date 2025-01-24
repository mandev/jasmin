package com.adlitteram.jasmin.io;

public interface ProgressListener {

    void bytesTransferred(long count);

    void finished(Object object);

    void init(Object object);
}
