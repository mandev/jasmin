package com.adlitteram.jasmin.image.icc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.color.ICC_Profile;

public class IccProfilePath {

    private static final Logger logger = LoggerFactory.getLogger(IccProfilePath.class);

    public static final IccProfilePath BUILT_IN_RGB = new IccProfilePath(IccUtils.CS_sRGB_PROFILE, "BUILT_IN_RGB");
    public static final IccProfilePath BUILT_IN_GRAY = new IccProfilePath(IccUtils.DF_GRAY_PROFILE, "BUILT_IN_GRAY");
    public static final IccProfilePath BUILT_IN_CMYK = new IccProfilePath(IccUtils.DF_CMYK_PROFILE, "BUILT_IN_CMYK");

    private final ICC_Profile profile;
    private final String path;

    public static IccProfilePath getInstance(String path) {
        try {
            if (BUILT_IN_RGB.getPath().equals(path)) {
                return BUILT_IN_RGB;
            }
            if (BUILT_IN_GRAY.getPath().equals(path)) {
                return BUILT_IN_GRAY;
            }
            if (BUILT_IN_CMYK.getPath().equals(path)) {
                return BUILT_IN_CMYK;
            }
            ICC_Profile profile = ICC_Profile.getInstance(path);
            if (profile != null) {
                return new IccProfilePath(profile, path);
            }
        } catch (Exception ex) {
            logger.warn("Cannot load profile: {} - {}", path, ex.getMessage());
        }
        return null;
    }

    private IccProfilePath(ICC_Profile profile, String path) {
        this.profile = profile;
        this.path = path;
    }

    public String getPath() {
        return path;
    }

    public ICC_Profile getIccProfile() {
        return profile;
    }
}
