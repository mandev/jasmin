
package com.adlitteram.jasmin.color;

import com.adlitteram.jasmin.utils.NumUtils;
import java.awt.Color;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;

public class NamedColor extends Color {
    public static final NamedColor BLACK = new NamedColor(Color.BLACK, 0, 0, 0, 255, "Black");
    public static final NamedColor WHITE = new NamedColor(Color.WHITE, 0, 0, 0, 0, "White");
    //
    public static final int TYPE_RGB = 0;
    public static final int TYPE_RED = 1;
    public static final int TYPE_GREEN = 2;
    public static final int TYPE_BLUE = 3;
    public static final int TYPE_CMYK = 4;
    public static final int TYPE_CYAN = 5;
    public static final int TYPE_MAGENTA = 6;
    public static final int TYPE_YELLOW = 7;
    public static final int TYPE_BLACK = 8;
    private String name;
    private int cmyk;

    public NamedColor(ColorSpace cs, float[] components, float alpha) {
        super(cs, components, alpha);
        name = "R" + getRed() + " G" + getGreen() + " B" + getBlue() + " A" + getAlpha();
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(float r, float g, float b) {
        super(r, g, b);
        name = "R" + getRed() + " G" + getGreen() + " B" + getBlue();
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(float r, float g, float b, float a) {
        super(r, g, b, a);
        name = "R" + getRed() + " G" + getGreen() + " B" + getBlue() + " A" + getAlpha();
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(int rgb) {
        super(rgb);
        name = "R" + getRed() + " G" + getGreen() + " B" + getBlue();
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(int rgba, boolean hasalpha) {
        super(rgba, hasalpha);
        name = "R" + getRed() + " G" + getGreen() + " B" + getBlue() + " A" + getAlpha();
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(int r, int g, int b) {
        super(r, g, b);
        name = "R" + r + " G" + g + " B" + b ;
        cmyk = fromRgbToCmyk(null, r, g, b);
    }

    public NamedColor(int r, int g, int b, int a) {
        super(r, g, b, a);
        name = "R" + r + " G" + g + " B" + b + " A" + a;
        cmyk = fromRgbToCmyk(null, r, g, b);
    }

    public NamedColor(Color color) {
        this(color, "R" + color.getRed() + " G" + color.getGreen() + " B" + color.getBlue());
    }

    public NamedColor(Color color, String name) {
        super(color.getRGB());
        this.name = name;
        cmyk = fromRgbToCmyk(null, getRed(), getGreen(), getBlue());
    }

    public NamedColor(NamedColor color, String name) {
        super(color.getRGB());
        this.name = name;
        this.cmyk = color.cmyk;
    }

    public NamedColor(int rgb, int cmyk, String name) {
        super(rgb);
        this.name = name;
        this.cmyk = cmyk;
    }

    public NamedColor(Color color, int c, int m, int y, int k, String name) {
        super(color.getRGB());
        this.name = name;
        cmyk = ((c & 0xFF) << 24) | ((m & 0xFF) << 16) | ((y & 0xFF) << 8) | ((k & 0xFF));
    }

    // Factory
    // Factory
    public static NamedColor buildCmykColor(int c, int m, int y, int k) {
        return buildCmykColor(null, c, m, y, k, "");
    }

    public static NamedColor buildCmykColor(int c, int m, int y, int k, String name) {
        return buildCmykColor(null, c, m, y, k, "");
    }

    public static NamedColor buildCmykColor(ICC_ColorSpace cs, int c, int m, int y, int k) {
        return buildCmykColor(cs, c, m, y, k, "");
    }

    public static NamedColor buildCmykColor(ICC_ColorSpace cs, int c, int m, int y, int k, String name) {
        int rgb = fromCmykToRgb(cs, c, m, y, k);
        int cmyk = ((c & 0xFF) << 24) | ((m & 0xFF) << 16) | ((y & 0xFF) << 8) | ((k & 0xFF) << 0);
        return new NamedColor(rgb, cmyk, name);
    }

    // for example rgb,cmyk (ie. 1234,7890)
    public static NamedColor buildFromProperty(String sColor) {
        int i = sColor.indexOf(',');
        if (i != -1)
            return new NamedColor(NumUtils.intValue(sColor.substring(0, i)), NumUtils.intValue(sColor.substring(i + 1)), "");

        return new NamedColor(NumUtils.intValue(sColor));
    }

    public String getStringProperty() {
        return getRGB() + "," + cmyk;
    }

    // Methods
    public static int fromCmykToRgb(ICC_ColorSpace cs, int c, int m, int y, int k) {

        if (cs != null) {
            float[] cmykArray = {(float) c / 255f, (float) m / 255f, (float) y / 255f, (float) k / 255f};
            float[] rgbArray = cs.toRGB(cmykArray);
            int r = Math.round(rgbArray[0] * 255f);
            int g = Math.round(rgbArray[1] * 255f);
            int b = Math.round(rgbArray[2] * 255f);
            return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        }
        else {
            int r = Math.round(255 - Math.min(255, c * (255 - k) / 255f + k));
            int g = Math.round(255 - Math.min(255, m * (255 - k) / 255f + k));
            int b = Math.round(255 - Math.min(255, y * (255 - k) / 255f + k));
            return ((r & 0xFF) << 16) | ((g & 0xFF) << 8) | ((b & 0xFF));
        }
    }

    public static int fromRgbToCmyk(ICC_ColorSpace cs, int r, int g, int b) {

        if (cs != null) {
            float[] rgbArray = {(float) r / 255f, (float) g / 255f, (float) b / 255f};
            float[] cmykArray = cs.fromRGB(rgbArray);
            int c = Math.round(cmykArray[0] * 255f);
            int m = Math.round(cmykArray[1] * 255f);
            int y = Math.round(cmykArray[2] * 255f);
            int k = Math.round(cmykArray[3] * 255f);
            return ((c & 0xFF) << 24) | ((m & 0xFF) << 16) | ((y & 0xFF) << 8) | ((k & 0xFF));
        }
        else {
            int c = 255 - r;
            int m = 255 - g;
            int y = 255 - b;
            int k = Math.min(c, Math.min(m, y));

            if (k != 255) {
                c = Math.round(255f * (c - k) / (255 - k));
                m = Math.round(255f * (m - k) / (255 - k));
                y = Math.round(255f * (y - k) / (255 - k));
            }
            else c = m = y = 0;

            return ((c & 0xFF) << 24) | ((m & 0xFF) << 16) | ((y & 0xFF) << 8) | ((k & 0xFF));
        }
    }

    public int getCyan() {
        return (cmyk >> 24) & 0xFF;
    }

    public int getMagenta() {
        return (cmyk >> 16) & 0xFF;
    }

    public int getYellow() {
        return (cmyk >> 8) & 0xFF;
    }

    public int getBlack() {
        return (cmyk) & 0xFF;
    }

    public int getCMYK() {
        return cmyk;
    }

    public String getName() {
        return name;
    }

    public String getDisplayName() {
        return name + " [r=" + getRed() + " g=" + getGreen() + " b=" + getBlue() + "] [c=" + getCyan() + " m=" + getMagenta() + " y=" + getYellow()
           + " k=" + getBlack() + "] ";
    }

    @Override
    public String toString() {
        return getClass().getName() + "[r=" + getRed() + ",g=" + getGreen() + ",b=" + getBlue() + "] " + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) return true;
        return (obj instanceof NamedColor && name.equals(((NamedColor) obj).getName()) && getRGB() == ((NamedColor) obj).getRGB() && getCMYK()
           == ((NamedColor) obj).getCMYK());
    }

    @Override
    public int hashCode() {
        int h1 = (name == null) ? 0 : name.hashCode();
        return h1 + 3 * getRGB() - 7 * getCMYK();
    }
}
