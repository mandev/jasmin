package com.adlitteram.jasmin.image.icc;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.awt.color.ColorSpace;
import java.awt.color.ICC_Profile;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class IccProfileManager {

    private static final Logger LOGGER = LoggerFactory.getLogger(IccProfileManager.class);

    // TODO: look at the profile in the OS dependent directory
    private static String iccDirName = "C:/Windows/System32/spool/drivers/color/";

    private IccProfilePath[] rgbProfiles;
    private IccProfilePath[] cmykProfiles;
    private IccProfilePath[] grayProfiles;

    private static final Comparator<IccProfilePath> PROFILE_COMP = (IccProfilePath o1, IccProfilePath o2) -> {
        String s1 = IccUtils.getDescription(o1.getIccProfile());
        String s2 = IccUtils.getDescription(o2.getIccProfile());
        return s1.compareTo(s2);
    };

    public IccProfileManager() {
    }

    private void initProfiles() {

        ArrayList<IccProfilePath> rgbList = new ArrayList<>();
        ArrayList<IccProfilePath> cmykList = new ArrayList<>();
        ArrayList<IccProfilePath> grayList = new ArrayList<>();

        // Base profiles
        rgbList.add(IccProfilePath.BUILT_IN_RGB);
        grayList.add(IccProfilePath.BUILT_IN_GRAY);
        cmykList.add(IccProfilePath.BUILT_IN_CMYK);

        File dir = new File(iccDirName);
        if (dir.isDirectory()) {
            String[] filenames = dir.list((File d, String name) -> {
                String s = name.toLowerCase();
                return s.endsWith(".icc") || s.endsWith(".icm") || s.endsWith(".pf");
            });
            if (filenames != null) {
                for (String filename : filenames) {
                    String path = iccDirName + File.separator + filename;
                    IccProfilePath profile = IccProfilePath.getInstance(path);
                    if (profile == null) {
                        continue;
                    }
                    int type = profile.getIccProfile().getColorSpaceType();
                    switch (type) {
                        case ColorSpace.TYPE_RGB:
                            rgbList.add(profile);
                            break;
                        case ColorSpace.TYPE_CMYK:
                            cmykList.add(profile);
                            break;
                        case ColorSpace.TYPE_GRAY:
                            grayList.add(profile);
                            break;
                        default:
                            LOGGER.info("Unknown profile type:{}", path);
                            break;
                    }
                }
            }
        }

        rgbProfiles = new IccProfilePath[rgbList.size()];
        cmykProfiles = new IccProfilePath[cmykList.size()];
        grayProfiles = new IccProfilePath[grayList.size()];

        rgbList.toArray(rgbProfiles);
        cmykList.toArray(cmykProfiles);
        grayList.toArray(grayProfiles);

        Arrays.sort(rgbProfiles, PROFILE_COMP);
        Arrays.sort(cmykProfiles, PROFILE_COMP);
        Arrays.sort(grayProfiles, PROFILE_COMP);
    }

    public IccProfilePath getProfile(String path) {
        for (IccProfilePath profile : getRgbProfiles()) {
            if (profile.getPath().equals(path)) {
                return profile;
            }
        }
        for (IccProfilePath profile : getGrayProfiles()) {
            if (profile.getPath().equals(path)) {
                return profile;
            }
        }
        for (IccProfilePath profile : getCmykProfiles()) {
            if (profile.getPath().equals(path)) {
                return profile;
            }
        }
        return null;
    }

    public IccProfilePath getProfile(ICC_Profile iccProfile) {
        int type = iccProfile.getColorSpaceType();
        if (type == ColorSpace.TYPE_RGB) {
            for (IccProfilePath profile : getRgbProfiles()) {
                if (profile.getIccProfile() == iccProfile) {
                    return profile;
                }
            }
        }
        if (type == ColorSpace.TYPE_GRAY) {
            for (IccProfilePath profile : getGrayProfiles()) {
                if (profile.getIccProfile() == iccProfile) {
                    return profile;
                }
            }
        }
        if (type == ColorSpace.TYPE_CMYK) {
            for (IccProfilePath profile : getCmykProfiles()) {
                if (profile.getIccProfile() == iccProfile) {
                    return profile;
                }
            }
        }
        return null;
    }

    public IccProfilePath[] getRgbProfiles() {
        if (rgbProfiles == null) {
            initProfiles();
        }
        return rgbProfiles;
    }

    public IccProfilePath[] getCmykProfiles() {
        if (cmykProfiles == null) {
            initProfiles();
        }
        return cmykProfiles;
    }

    public IccProfilePath[] getGrayProfiles() {
        if (grayProfiles == null) {
            initProfiles();
        }
        return grayProfiles;
    }

    public String getIccDirname() {
        return iccDirName;
    }

    public void setIccDirname(String str) {
        iccDirName = str;
        rgbProfiles = null;
        grayProfiles = null;
        cmykProfiles = null;
    }
}
