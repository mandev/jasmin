package com.adlitteram.jasmin.image.icc;

import org.slf4j.LoggerFactory;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.color.ColorSpace;
import java.awt.color.ICC_ColorSpace;
import java.awt.color.ICC_Profile;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.ComponentColorModel;
import java.awt.image.DataBuffer;
import java.awt.image.DirectColorModel;
import java.awt.image.IndexColorModel;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import org.slf4j.Logger;

public class IccUtils {

    private static final Logger logger = LoggerFactory.getLogger(IccUtils.class);

    public static final String[] COLORSPACE_TYPE = {
        "XYZ", "Lab", "Luv", "YCbCr", "Yxy", "RGB", "GRAY", "HSV", "HLS", "CMYK", "UNDEFINED",
        "CMY", "2CLR", "3CLR", "4CLR", "5CLR", "6CLR", "7CLR", "8CLR", "9CLR", "ACLR", "BCLR",
        "CCLR", "DCLR", "ECLR", "FCLR"};

    public static final ICC_Profile CS_sRGB_PROFILE = ICC_Profile.getInstance(ColorSpace.CS_sRGB); // sRGB profile
    public static final ICC_Profile CS_GRAY_PROFILE = ICC_Profile.getInstance(ColorSpace.CS_GRAY); // Gamma 1.0 Kodak profile
    public static final ICC_Profile DF_GRAY_PROFILE = getProfile(IccUtils.class, "/com/adlitteram/jasmin/profiles/gray.icc"); // Gamma 2.2 profile
    public static final ICC_Profile DF_CMYK_PROFILE = getProfile(IccUtils.class, "/com/adlitteram/jasmin/profiles/cmyk.icc"); // GRACoL2006_Coated1v2.icc profile

    public static final ICC_ColorSpace CS_sRGB_COLORSPACE = (ICC_ColorSpace) ColorSpace.getInstance(ColorSpace.CS_sRGB);
    public static final ICC_ColorSpace CS_GRAY_COLORSPACE = (ICC_ColorSpace) ColorSpace.getInstance(ColorSpace.CS_GRAY);
    public static final ICC_ColorSpace DF_GRAY_COLORSPACE = new ICC_ColorSpace(DF_GRAY_PROFILE);
    public static final ICC_ColorSpace DF_CMYK_COLORSPACE = new ICC_ColorSpace(DF_CMYK_PROFILE);

    public static ICC_Profile getDefaultRgbProfile() {
        return CS_sRGB_PROFILE;
    }

    public static ICC_Profile getDefaultCmykProfile() {
        return DF_CMYK_PROFILE;
    }

    public static ICC_Profile getDefaultGrayProfile() {
        return DF_GRAY_PROFILE;
    }

    public static ICC_Profile getProfile(String fileName) {
        try {
            return ICC_Profile.getInstance(fileName);
        }
        catch (IOException ex) {
            logger.warn("Cannot load profile: {}", fileName, ex);
            return null;
        }
    }

    public static ICC_Profile getProfile(Class clazz, String path) {
        try (InputStream is = clazz.getResourceAsStream(path)) {
            return ICC_Profile.getInstance(is);
        }
        catch (IOException ex) {
            logger.warn("Cannot load profile: {}", path, ex);
            return null;
        }
    }

    public static ICC_ColorSpace getColorSpace(ICC_Profile profile) {
        if (profile == CS_sRGB_PROFILE) {
            return CS_sRGB_COLORSPACE;
        }
        if (profile == CS_GRAY_PROFILE) {
            return CS_GRAY_COLORSPACE;
        }
        if (profile == DF_GRAY_PROFILE) {
            return DF_GRAY_COLORSPACE;
        }
        if (profile == DF_CMYK_PROFILE) {
            return DF_CMYK_COLORSPACE;
        }
        return new ICC_ColorSpace(profile);
    }

    public static String getColorSpaceName(int colorSpaceType) {
        return colorSpaceType >= 0 && colorSpaceType < COLORSPACE_TYPE.length ? COLORSPACE_TYPE[colorSpaceType] : "Unknown";
    }

    public static String getBuiltInName(ColorSpace colorSpace) {
        if (colorSpace == CS_sRGB_COLORSPACE) {
            return "CS_sRGB";
        }
        if (colorSpace == DF_GRAY_COLORSPACE) {
            return "DF_GRAY";
        }
        if (colorSpace == DF_CMYK_COLORSPACE) {
            return "DF_CMYK";
        }
        if (colorSpace == CS_GRAY_COLORSPACE) {
            return "CS_GRAY";
        }
        if (colorSpace == ColorSpace.getInstance(ColorSpace.CS_sRGB)) {
            return "CS_LINEAR_RGB";
        }
        if (colorSpace == ColorSpace.getInstance(ColorSpace.CS_CIEXYZ)) {
            return "CS_CIEXYZ";
        }
        if (colorSpace == ColorSpace.getInstance(ColorSpace.CS_PYCC)) {
            return "CS_PYCC";
        }

        int colorSpaceType = colorSpace.getType();
        return colorSpaceType >= 0 && colorSpaceType < COLORSPACE_TYPE.length ? COLORSPACE_TYPE[colorSpaceType] : "Unknown";
    }

    public static String getDescription(ICC_Profile profile) {
        byte[] data = profile.getData(ICC_Profile.icSigProfileDescriptionTag);
        int textLen = intFromBigEndian(data, 8);
        return new String(data, 12, textLen - 1);
    }

    public static String getCopyright(ICC_Profile profile) {
        byte[] data = profile.getData(ICC_Profile.icSigCopyrightTag);
        return new String(data, 8, data.length - 8 - 1);
    }

    public static String getProfileVersion(ICC_Profile profile) {
        return profile.getMajorVersion() + "." + profile.getMinorVersion();
    }

    public static String getColorSpaceType(ColorSpace colorSpace) {
        return getType(colorSpace.getType());
    }

    public static String getProfileClassName(ICC_Profile profile) {
        switch (profile.getProfileClass()) {
            case ICC_Profile.CLASS_OUTPUT:
                return "output";
            case ICC_Profile.CLASS_INPUT:
                return "input";
            case ICC_Profile.CLASS_DISPLAY:
                return "display";
            case ICC_Profile.CLASS_COLORSPACECONVERSION:
                return "conversion";
        }
        return "other";
    }

    public static String getProfileType(ICC_Profile profile) {
        return getType(profile.getColorSpaceType());
    }

    public static String getPCSType(ICC_Profile profile) {
        return getType(profile.getPCSType());
    }

    public static String getType(int type) {
        switch (type) {
            case ColorSpace.TYPE_XYZ:
                return "XYZ";
            case ColorSpace.TYPE_Lab:
                return "Lab";
            case ColorSpace.TYPE_Luv:
                return "Luv";
            case ColorSpace.TYPE_YCbCr:
                return "YCbCr";
            case ColorSpace.TYPE_Yxy:
                return "Yxy";
            case ColorSpace.TYPE_RGB:
                return "RGB";
            case ColorSpace.TYPE_GRAY:
                return "GRAY";
            case ColorSpace.TYPE_HSV:
                return "HSV";
            case ColorSpace.TYPE_HLS:
                return "HLS";
            case ColorSpace.TYPE_CMYK:
                return "CMYK";
            case ColorSpace.TYPE_CMY:
                return "CMY";
            case ColorSpace.TYPE_2CLR:
                return "2CLR";
            case ColorSpace.TYPE_3CLR:
                return "3CLR";
            case ColorSpace.TYPE_4CLR:
                return "4CLR";
            case ColorSpace.TYPE_5CLR:
                return "5CLR";
            case ColorSpace.TYPE_6CLR:
                return "6CLR";
            case ColorSpace.TYPE_7CLR:
                return "7CLR";
            case ColorSpace.TYPE_8CLR:
                return "8CLR";
            case ColorSpace.TYPE_9CLR:
                return "9CLR";
            case ColorSpace.TYPE_ACLR:
                return "ACLR";
            case ColorSpace.TYPE_BCLR:
                return "BCLR";
            case ColorSpace.TYPE_CCLR:
                return "CCLR";
            case ColorSpace.TYPE_DCLR:
                return "DCLR";
            case ColorSpace.TYPE_ECLR:
                return "ECLR";
            case ColorSpace.TYPE_FCLR:
                return "FCLR";
            case ColorSpace.CS_sRGB:
                return "BuiltIn sRGB";
            case ColorSpace.CS_LINEAR_RGB:
                return "BuiltIn Linear RGB";
            case ColorSpace.CS_CIEXYZ:
                return "BuiltIn CIE XYZ";
            case ColorSpace.CS_PYCC:
                return "BuiltIn PYCC";
            case ColorSpace.CS_GRAY:
                return "BuiltIn Gray";
        }
        return "";
    }

    public static boolean isCS_sRGB(BufferedImage image) {
        return image.getColorModel().getColorSpace().isCS_sRGB();
    }

    public static boolean isCS_sGRAY(BufferedImage image) {
        return image.getColorModel().getColorSpace() == IccUtils.CS_GRAY_COLORSPACE;
    }

    public static boolean isRGB(BufferedImage image) {
        return image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_RGB;
    }

    public static boolean isCMYK(BufferedImage image) {
        return image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_CMYK;
    }

    public static boolean isGRAY(BufferedImage image) {
        return image.getColorModel().getColorSpace().getType() == ColorSpace.TYPE_GRAY;
    }

    public static BufferedImage applyProfile(BufferedImage srcImage, ICC_Profile dstProfile) {
        return applyColorSpace(srcImage, getColorSpace(dstProfile));
    }

    public static BufferedImage applyColorSpace(BufferedImage srcImage, ColorSpace dstColorSpace) {
        // Source
        ColorModel srcColorModel = srcImage.getColorModel();
        ColorSpace srcColorSpace = srcColorModel.getColorSpace();

        // Destination
        BufferedImage dstImage = null;
        if (srcColorSpace.getType() == dstColorSpace.getType()) {

            // Unfortunately ColorSpace does not subclass the equals method
            if (srcColorSpace.isCS_sRGB() && dstColorSpace.isCS_sRGB()) {
                return srcImage;
            }

            WritableRaster srcRaster = srcImage.getRaster();
            SampleModel srcSampleModel = srcRaster.getSampleModel();

            if (srcColorModel instanceof ComponentColorModel) {
                ColorModel dstColorModel = new ComponentColorModel(dstColorSpace, srcColorModel.getComponentSize(), srcColorModel.hasAlpha(),
                        srcColorModel.isAlphaPremultiplied(), srcColorModel.getTransparency(), srcColorModel.getTransferType());

                dstImage = new BufferedImage(dstColorModel, srcRaster, dstColorModel.isAlphaPremultiplied(), null);
            }
            else if (srcColorModel instanceof DirectColorModel) {
                DirectColorModel directColorModel = (DirectColorModel) srcColorModel;
                ColorModel dstColorModel = new DirectColorModel(dstColorSpace, directColorModel.getPixelSize(), directColorModel.getRedMask(), directColorModel.getGreenMask(),
                        directColorModel.getBlueMask(), directColorModel.getAlphaMask(), directColorModel.isAlphaPremultiplied(), directColorModel.getTransferType());

                dstImage = new BufferedImage(dstColorModel, srcRaster, dstColorModel.isAlphaPremultiplied(), null);
            }
            else if (srcColorModel instanceof IndexColorModel) {
                // IndexColorModel are always CS_RGB and cannot be modified as such
                ColorModel dstModel = new ComponentColorModel(dstColorSpace, srcColorModel.hasAlpha(), srcColorModel.isAlphaPremultiplied(),
                        srcColorModel.getTransparency(), DataBuffer.TYPE_BYTE);

                ColorModel ccm = new ComponentColorModel(srcColorSpace, srcColorModel.hasAlpha(), srcColorModel.isAlphaPremultiplied(),
                        srcColorModel.getTransparency(), DataBuffer.TYPE_BYTE);

                BufferedImage bi = new BufferedImage(ccm, ccm.createCompatibleWritableRaster(srcRaster.getWidth(), srcRaster.getHeight()), ccm.isAlphaPremultiplied(), null);
                Graphics2D g = bi.createGraphics();
                g.setComposite(AlphaComposite.Src);
                g.drawImage(srcImage, 0, 0, null);
                g.dispose();

                dstImage = new BufferedImage(dstModel, bi.getRaster(), dstModel.isAlphaPremultiplied(), null);
            }
            else {
                logger.info("Unknown color model");
            }
        }
        else {
            logger.info("Colorspaces are not compatible: {} vs {}", srcColorSpace.getType(), dstColorSpace.getType());
        }
        return dstImage;
    }

    public static BufferedImage convertToImage(BufferedImage srcImage, BufferedImage dstImage, int render) {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        return new IccColorConvertOp(hints, render).filter(srcImage, dstImage);
    }

    public static BufferedImage convertToColorSpace(BufferedImage srcImage, ColorSpace dstColorSpace, int render) {
        RenderingHints hints = new RenderingHints(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
        return new IccColorConvertOp(dstColorSpace, hints, render).filter(srcImage, null);
    }

    public static BufferedImage convertToProfile(BufferedImage srcImage, ICC_Profile dstProfile, int render) {
        return convertToColorSpace(srcImage, getColorSpace(dstProfile), render);
    }

    public static BufferedImage convertFromProfileToProfile(BufferedImage srcImage, ICC_Profile srcProfile, ICC_Profile dstProfile, int render) {
        return convertToProfile(applyProfile(srcImage, srcProfile), dstProfile, render);
    }

    private static int intFromBigEndian(byte[] array, int index) {
        return (((array[index] & 0xff) << 24)
                | ((array[index + 1] & 0xff) << 16)
                | ((array[index + 2] & 0xff) << 8)
                | (array[index + 3] & 0xff));
    }

    private static void intToBigEndian(int value, byte[] array, int index) {
        array[index] = (byte) (value >> 24);
        array[index + 1] = (byte) (value >> 16);
        array[index + 2] = (byte) (value >> 8);
        array[index + 3] = (byte) (value);
    }

    private static short shortFromBigEndian(byte[] array, int index) {
        return (short) (((array[index] & 0xff) << 8) | (array[index + 1] & 0xff));
    }

    private static void shortToBigEndian(short value, byte[] array, int index) {
        array[index] = (byte) (value >> 8);
        array[index + 1] = (byte) (value);
    }
}
