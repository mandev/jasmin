package com.adlitteram.jasmin.image;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

public class XImage {

    private final BufferedImage image;
    private final int sourceWidth;
    private final int sourceHeight;
    private final String format;
    private final long size;
    private final long modtime;
    private final String path;
    private final int subsampling;

    private XImage(BufferedImage image, int width, int height, String format,
                   long size, long modtime, int subsampling, String path) {

        this.image = image;             // image data
        this.sourceWidth = width;       // image.getWidth() can be different from sourceWidth
        this.sourceHeight = height;    // image.getHeight() can be different from sourceHeight
        this.format = format;           // initial format JPEG, PNG, etc
        this.size = size;               // file size
        this.modtime = modtime;         // file last modifcation time
        this.path = path;               // file path
        this.subsampling = subsampling; // subsamplig factor
    }

    public static XImage createXImage(BufferedImage image, XImage ximg) {
        return new XImage(image, ximg.getSourceWidth(), ximg.getSourceHeight(),
                ximg.getFormat(), ximg.getSize(), ximg.getTime(),
                ximg.getSubsampling(), ximg.getPath());
    }

    public static XImage createXImage(BufferedImage image, int width, int height, String format, long size, long modtime, int subsampling, String path) {
        return new XImage(image, width, height, format, size, modtime, subsampling, path);
    }

    public static XImage createXImage(File file, int sampling)  {
        return ImageTool.readXImage(file, new ReadParam(sampling));
    }

    public static XImage createErrorXImage() {
        return createErrorXImage("");
    }

    public static XImage createErrorXImage(String path) {
        return createErrorXImage(path, 64, 64);
    }

    public static XImage createErrorXImage(String path, int width, int height) {
        BufferedImage bi = ImageUtils.createCompatibleImage(width, height, false);
        Graphics g = bi.getGraphics();
        g.setColor(Color.LIGHT_GRAY);
        g.fillRect(0, 0, width, height);
        g.dispose();
        return new XImage(bi, width, height, "???", 1, 1, 1, path);
    }

    public BufferedImage getImage() {
        return image;
    }

    public int getSourceWidth() {
        return sourceWidth;
    }

    public int getSourceHeight() {
        return sourceHeight;
    }

    public String getFormat() {
        return format;
    }

    public long getSize() {
        return size;
    }

    public long getTime() {
        return modtime;
    }

    public String getPath() {
        return path;
    }

    public int getSubsampling() {
        return subsampling;
    }

    public boolean isEmpty() {
        return (image == null);
    }

    public int getPixelSize() {
        return (image == null) ? -1 : image.getColorModel().getPixelSize();
    }

    //@Override
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final XImage other = (XImage) obj;
        if (sourceWidth != other.sourceWidth) {
            return false;
        }
        if (sourceHeight != other.sourceHeight) {
            return false;
        }
        if (subsampling != other.subsampling) {
            return false;
        }
        if (modtime != other.modtime) {
            return false;
        }
        if (!Objects.equals(format, other.format)) {
            return false;
        }
        if (!Objects.equals(path, other.path)) {
            return false;
        }
        return Objects.equals(image, other.image);
    }

    //@Override
    @Override
    public int hashCode() {
        int h1 = (image == null) ? 0 : image.hashCode();
        int h2 = (isEmpty()) ? 0 : 1;
        int h3 = (format == null) ? 0 : format.hashCode();
        int h4 = (path == null) ? 0 : path.hashCode();
        return h1 + h2 + h3 + 3 * sourceWidth - 7 * sourceHeight + (int) modtime + 13 * h4;
    }

    //@Override
    @Override
    public String toString() {
        return "[ sourceWidth=" + sourceWidth
                + " , sourceHeight=" + sourceHeight
                + " , size=" + size
                + " , modtime=" + modtime
                + " , format=" + format
                + " , path=" + path + " ]";
    }
}
