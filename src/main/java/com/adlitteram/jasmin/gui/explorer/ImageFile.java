/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.utils.ThreadUtils;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.concurrent.ThreadPoolExecutor;

public class ImageFile {

    private static final ThreadPoolExecutor TP_EXECUTOR = (ThreadPoolExecutor) ThreadUtils.newFixedLifoThreadPool(2);
    //
    private File file;
    private String name;
    private long length;
    private int width;
    private int height;
    private long time;
    private long lastModified;
    private String format;
    private BufferedImage image;
    private boolean isComplete;
    private int iconSize;
    private Object properties;

    public ImageFile(File file) {
        this(file, file.getName());
    }

    public ImageFile(File file, String name) {
        this.file = file;
        this.length = file.length();
        this.time = file.lastModified();
        this.lastModified = time;
        this.name = name;
        this.iconSize = -1;
    }

    public File getFile() {
        return file;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long lastModified() {
        return lastModified;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }

    public long getLength() {
        return length;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    public int getHeight() {
        return height;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getWidth() {
        return width;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public Object getProperties() {
        return properties;
    }

    public void setProperties(Object properties) {
        this.properties = properties;
    }

    public BufferedImage getImage() {
        return image;
    }

    public void setImage(BufferedImage image) {
        this.image = image;
    }

    public int getIconSize() {
        return iconSize;
    }

    public synchronized void unCompleted() {
        isComplete = false;
    }

    public synchronized boolean isCompleted() {
        return isComplete;
    }

    public synchronized boolean checkCompleted() {
        if (isComplete) {
            return true;
        }
        isComplete = true;
        return false;
    }

    public void update(ExplorerModel model, int index) {
        image = null;
        unCompleted();
        iconSize = model.getIconSize();
        LoadImageTask task = new LoadImageTask(model, index, this, iconSize);
        TP_EXECUTOR.remove(task);
        TP_EXECUTOR.execute(task);
    }
}
