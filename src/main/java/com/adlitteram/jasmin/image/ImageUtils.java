package com.adlitteram.jasmin.image;

import com.adlitteram.jasmin.image.icc.IccUtils;

import javax.imageio.stream.ImageInputStream;
import javax.imageio.stream.ImageOutputStream;
import java.awt.*;
import java.awt.color.ICC_Profile;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.util.zip.ZipFile;

public final class ImageUtils {

    public static final int NEAREST_INTERPOLATION = 0;
    public static final int BILINEAR_INTERPOLATION = 1;
    public static final int BICUBIC_INTERPOLATION = 2;

    private static final int OPAQUE_RGB = BufferedImage.TYPE_INT_RGB;
    private static final int ALPHA_RGB = BufferedImage.TYPE_INT_ARGB;

    private ImageUtils() {
    }

    public static BufferedImage copyImage(BufferedImage img) {
        return new BufferedImage(img.getColorModel(), copyRaster(img.getRaster()), img.isAlphaPremultiplied(), null);
    }

    public static WritableRaster copyRaster(Raster raster) {
        int width = raster.getWidth();
        int height = raster.getHeight();
        int startX = raster.getMinX();
        int startY = raster.getMinY();
        WritableRaster wr = raster.createCompatibleWritableRaster();
        Object tdata = null;
        for (int i = startY; i < startY + height; i++) {
            tdata = raster.getDataElements(startX, i, width, 1, tdata);
            wr.setDataElements(startX, i, width, 1, tdata);
        }
        return wr;
    }

    public static BufferedImage createImage(BufferedImage image) {
        return createImage(image, image.getWidth(), image.getHeight());
    }

    public static BufferedImage createImage(BufferedImage image, int width, int height) {
        ColorModel colorModel = image.getColorModel();
        WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
        return new BufferedImage(colorModel, raster, colorModel.isAlphaPremultiplied(), null);
    }

    // Return an empty (A)RGB BufferedImage, enforce transparency
    public static BufferedImage createRGBImage(int width, int height, boolean hasAlpha) {
        return new BufferedImage(width, height, (hasAlpha) ? ALPHA_RGB : OPAQUE_RGB);
    }

    // Return an CS_sRGB with 24 or 32 bits Direct Color Model bufferedImage
    public static BufferedImage getRGBImage(Image image) {
        if (image instanceof BufferedImage img) {
            return getRGBImage(img, img.getColorModel().hasAlpha());
        } else {
            return getRGBImage(image, true);
        }
    }

    // Return an CS_sRGB with 24 or 32 bits Direct Color Model bufferedImage
    public static BufferedImage getRGBImage(Image image, boolean hasAlpha) {
        if (image instanceof BufferedImage img) {
            int type = img.getType();

            // BI with built-in Direct Color Model type with CSsRGB colorSpace
            if ((hasAlpha && type == ALPHA_RGB) || (!hasAlpha && type == OPAQUE_RGB)) {
                return img;
            }
        }

        // Not a BI or BI with built-in CSsRGB ColorSpace -> convert to 24 or 32 bits Direct Color Model
        BufferedImage img = createRGBImage(image.getWidth(null), image.getHeight(null), hasAlpha);
        Graphics2D g = img.createGraphics();
        g.setComposite(AlphaComposite.Src);
        g.drawImage(image, 0, 0, null);
        g.dispose();
        return img;
    }

    // Return a scaled (A)RGB BufferedImage
    public static BufferedImage getScaledRGBImage(Image img, int maxSize) {
        return getScaledRGBImage(img, maxSize, maxSize, true);
    }

    // Return a scaled (A)RGB BufferedImage
    public static BufferedImage getScaledRGBImage(Image img, int maxWidth, int maxHeight, boolean keepRatio) {
        double scalex = (double) maxWidth / (double) img.getWidth(null);
        double scaley = (double) maxHeight / (double) img.getHeight(null);
        if (keepRatio) {
            scalex = scaley = Math.min(scalex, scaley);
        }
        return getScaledRGBImage(img, scalex, scaley);
    }

    // Return a scaled (A)RGB BufferedImage
    public static BufferedImage getScaledRGBImage(Image image, double sx, double sy) {
        if (sx == 1d && sy == 1d) {
            return getRGBImage(image);
        }

        BufferedImage img;
        boolean hasAlpha = true;

        img = createRGBImage((int) (image.getWidth(null) * sx + .5f), (int) (image.getHeight(null) * sy + .5f), hasAlpha);
        Graphics2D g2 = img.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.drawImage(image, 0, 0, img.getWidth(), img.getHeight(), null);
        g2.dispose();
        return img;
    }

    // Return a rotated compatible (A)RGB BufferedImage
    public static BufferedImage getRotatedRGBImage(Image img, double rotate) {
        return (rotate == 0) ? getRGBImage(img) : getScaledRotatedRGBImage(img, 1d, 1d, rotate);
    }

    // Return a scaled and rotated compatible BufferedImage
    public static BufferedImage getScaledRotatedRGBImage(Image image, double sx, double sy, double rotate) {
        if (rotate == 0) {
            return getScaledRGBImage(image, sx, sy);
        }

        boolean hasAlpha;

        if (image instanceof BufferedImage img) {
            ColorModel colorModel = img.getColorModel();
            hasAlpha = colorModel.hasAlpha();
            if (!colorModel.getColorSpace().isCS_sRGB()) {
                // Image with non CSsRGB colorspace
                BufferedImage bi = createRGBImage(img.getWidth(), img.getHeight(), hasAlpha);
                image = IccUtils.convertToImage(img, bi, ICC_Profile.icPerceptual);
            }
        } else {
            hasAlpha = true;
        }

        AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
        af.rotate(-rotate);
        Rectangle rectangle = new Rectangle(image.getWidth(null), image.getHeight(null));
        Rectangle2D bounds = af.createTransformedShape(rectangle).getBounds2D();

        BufferedImage dst = createRGBImage((int) (bounds.getWidth() + .5f), (int) (bounds.getHeight() + .5f), hasAlpha);
        Graphics2D g2 = dst.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        af = AffineTransform.getTranslateInstance(-bounds.getX(), -bounds.getY());
        af.scale(sx, sy);
        af.rotate(-rotate);
        g2.drawImage(image, af, null);
        g2.dispose();
        return dst;
    }

    // Return an empty compatible BufferedImage, enforce transparency
    public static BufferedImage createCompatibleImage(int width, int height, boolean hasAlpha) {
        GraphicsConfiguration gc = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration();
        return gc.createCompatibleImage(width, height, (hasAlpha) ? Transparency.BITMASK : Transparency.OPAQUE);
    }

    // Return a compatible image from Image with same transparency - if possible
    public static BufferedImage getCompatibleImage(Image image) {
        boolean hasAlpha = !(image instanceof BufferedImage img) || img.getColorModel().hasAlpha();
        return getCompatibleImage(image, hasAlpha);
    }

    // Return a compatible BufferedImage from Image, enforce transparency
    public static BufferedImage getCompatibleImage(Image img, boolean hasAlpha) {
        BufferedImage bi = createCompatibleImage(img.getWidth(null), img.getHeight(null), hasAlpha);
        Graphics2D g2 = bi.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.drawImage(img, 0, 0, null);
        g2.dispose();
        return bi;
    }

    // Return a scaled and rotated compatible BufferedImage
    public static BufferedImage getScaledCompatibleImage(Image img, int maxSize) {
        return getScaledCompatibleImage(img, maxSize, maxSize, true);
    }

    public static BufferedImage getScaledCompatibleImage(Image img, int maxWidth, int maxHeight, boolean keepRatio) {
        double scalex = (double) maxWidth / (double) img.getWidth(null);
        double scaley = (double) maxHeight / (double) img.getHeight(null);
        if (keepRatio) {
            scalex = scaley = Math.min(scalex, scaley);
        }
        return getScaledCompatibleImage(img, scalex, scaley);
    }

    // Return a scaled and rotated compatible BufferedImage
    public static BufferedImage getScaledCompatibleImage(Image image, double sx, double sy) {
        if (sx == 1d && sy == 1d) {
            return getCompatibleImage(image);
        }

        boolean hasAlpha = !(image instanceof BufferedImage img) || img.getColorModel().hasAlpha();
        BufferedImage dst = createCompatibleImage((int) (image.getWidth(null) * sx + .5f), (int) (image.getHeight(null) * sy + .5f), hasAlpha);
        Graphics2D g2 = dst.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        g2.drawImage(image, 0, 0, dst.getWidth(), dst.getHeight(), null);
        g2.dispose();
        return dst;
    }

    // Return a rotated compatible BufferedImage
    public static BufferedImage getRotatedCompatibleImage(Image img, double rotate) {
        return (rotate == 0) ? getCompatibleImage(img) : getScaledRotatedCompatibleImage(img, 1d, 1d, rotate);
    }

    // Return a scaled and rotated compatible BufferedImage
    public static BufferedImage getScaledRotatedCompatibleImage(Image img, double sx, double sy, double rotate) {
        if (rotate == 0) {
            return getScaledCompatibleImage(img, sx, sy);
        }

        BufferedImage image = (img instanceof BufferedImage iimg) ? iimg : getCompatibleImage(img);

        AffineTransform af = AffineTransform.getScaleInstance(sx, sy);
        af.rotate(-rotate);
        Rectangle rectangle = new Rectangle(image.getWidth(), image.getHeight());
        Rectangle2D bounds = af.createTransformedShape(rectangle).getBounds2D();

        BufferedImage dst = createCompatibleImage((int) (bounds.getWidth() + .5f), (int) (bounds.getHeight() + .5f), true);
        Graphics2D g2 = dst.createGraphics();
        g2.setComposite(AlphaComposite.Src);
        g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        g2.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);

        af = AffineTransform.getTranslateInstance(-bounds.getX(), -bounds.getY());
        af.scale(sx, sy);
        af.rotate(-rotate);
        g2.drawImage(image, af, null);
        g2.dispose();
        return dst;
    }

    public static boolean isAjg(String str) {
        str = str.toLowerCase();
        return ("ajpg".equals(str) || "ajg".equals(str) || "ajpeg".equals(str));
    }

    public static boolean isJpeg(String str) {
        str = str.toLowerCase();
        return ("jpeg".equals(str) || "jpg".equals(str));
    }

    public static boolean isPng(String str) {
        str = str.toLowerCase();
        return ("png".equals(str));
    }

    public static boolean isPbm(String str) {
        str = str.toLowerCase();
        return ("pbm".equals(str) || "ppm".equals(str));
    }

    public static boolean isTiff(String str) {
        str = str.toLowerCase();
        return ("tiff".equals(str) || "tif".equals(str));
    }

    // Close ZipFile
    public static void closeQuietly(ZipFile file) {
        try {
            if (file != null) {
                file.close();
            }
        } catch (IOException ioe) {
            // Do nothing
        }
    }

    // Close the ImageInputStream
    public static void closeQuietly(ImageInputStream iis) {
        try {
            if (iis != null) {
                iis.close();
            }
        } catch (IOException ioe) {
            // Do nothing
        }
    }

    // Close the ImageOutputStream
    public static void closeQuietly(ImageOutputStream ios) {
        try {
            if (ios != null) {
                ios.close();
            }
        } catch (IOException ioe) {
            // Do nothing
        }
    }

}
