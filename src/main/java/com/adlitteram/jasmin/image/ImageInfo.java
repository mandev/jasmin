package com.adlitteram.jasmin.image;

public class ImageInfo {

    private final int width;
    private final int height;
    private final String format;
    private final boolean hasAlpha;

    public ImageInfo(int width, int height, String format, boolean hasAlpha) {
        this.width = width;
        this.height = height;
        this.format = format;
        this.hasAlpha = hasAlpha;
    }

    public String getFormat() {
        return format;
    }

    public int getHeight() {
        return height;
    }

    public int getWidth() {
        return width;
    }

    public boolean hasAlpha() {
        return hasAlpha;
    }

    public boolean isValidImage() {
        return width > 0 && height > 0;
    }
}
