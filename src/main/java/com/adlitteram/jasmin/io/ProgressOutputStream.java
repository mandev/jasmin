package com.adlitteram.jasmin.io;

import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;

public class ProgressOutputStream extends FilterOutputStream {

    private ArrayList<ProgressListener> listenerList;
    private long count;
    private long oldCount;
    private long oldTime;
    private long trigger;

    public ProgressOutputStream(OutputStream os) {
        this(os, 0);
    }

    public ProgressOutputStream(OutputStream os, int size) {
        this(os, size, new ArrayList<ProgressListener>());
    }

    public ProgressOutputStream(OutputStream os, int size, ArrayList<ProgressListener> listenerList) {
        super(os);

        this.trigger = size;
        this.listenerList = listenerList;

        count = 0;
        oldCount = 0;
        oldTime = System.currentTimeMillis();
    }

    @Override
    public void close() throws IOException {
        out.close();
        fireListeners();
    }

    @Override
    public void flush() throws IOException {
        out.flush();
        fireListeners();
    }

    @Override
    public void write(byte[] b) throws IOException {
        out.write(b);
        updateSize(b.length);
    }

    @Override
    public void write(byte[] b, int off, int len) throws IOException {
        out.write(b, off, len);
        updateSize(len);
    }

    @Override
    public void write(int b) throws IOException {
        out.write(b);
        updateSize(1);
    }

    public void addProgressListener(ProgressListener listener) {
        if (!listenerList.contains(listener)) {
            listenerList.add(listener);
        }
    }

    public void removeProgressListener(ProgressListener listener) {
        listenerList.remove(listener);
    }

    private void updateSize(int length) {
        count += length;
        if (trigger < 0) {
            long time = System.currentTimeMillis();
            if ((time - oldTime) > -trigger) {
                oldTime = time;
                fireListeners();
            }
        }
        else if (trigger > 0) {
            if ((count - oldCount) >= trigger) {
                fireListeners();
            }
        }
    }

    private void fireListeners() {
        oldCount = count;
        listenerList.forEach(listener -> listener.bytesTransferred(count));
    }
}
