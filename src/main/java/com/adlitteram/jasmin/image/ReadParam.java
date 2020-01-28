package com.adlitteram.jasmin.image;

import com.adlitteram.jasmin.image.icc.IccUtils;
import java.awt.color.ICC_Profile;

public class ReadParam {

    private int subSampling = 1;
    private boolean convertToRGB;       // Built-in sRGB Colorspace
    private boolean useEmbeddedProfile;
    private ICC_Profile rgbProfile;
    private ICC_Profile cmykProfile;
    private ICC_Profile grayProfile;

    public ReadParam() {
        this(1);
    }

    public ReadParam(int subSampling) {
        this(subSampling, true);
    }

    public ReadParam(int subSampling, boolean convertToRGB) {
        this(subSampling, convertToRGB, true, null, null, null);
    }

    public ReadParam(int subSampling, boolean convertToRGB, boolean useEmbeddedProfile,
            ICC_Profile rgbProfile, ICC_Profile cmykProfile, ICC_Profile grayProfile) {

        this.subSampling = Math.max(1, subSampling);
        this.convertToRGB = convertToRGB;
        this.useEmbeddedProfile = useEmbeddedProfile;
        this.rgbProfile = rgbProfile;
        this.cmykProfile = cmykProfile;
        this.grayProfile = grayProfile;
    }

    public int getSubSampling() {
        return subSampling;
    }

    public boolean isConvertToCSsRGB() {
        return convertToRGB;
    }

    public boolean useEmbeddedProfile() {
        return useEmbeddedProfile;
    }

    public ICC_Profile getRgbProfile() {
        if (rgbProfile == null) {
            rgbProfile = IccUtils.getDefaultRgbProfile();
        }
        return rgbProfile;
    }

    public ICC_Profile getCmykProfile() {
        if (cmykProfile == null) {
            cmykProfile = IccUtils.getDefaultCmykProfile();
        }
        return cmykProfile;
    }

    public ICC_Profile getGrayProfile() {
        if (grayProfile == null) {
            grayProfile = IccUtils.getDefaultGrayProfile();
        }
        return grayProfile;
    }
}
