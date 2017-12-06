/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adlitteram.jasmin.utils;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.util.Date;
import org.slf4j.Logger;

public class ExifUtils {
    private static final Logger LOGGER = LoggerFactory.getLogger(ExifUtils.class);

    public static long getExifTime(File file) {
//        ExifMetadata exifMetadata = JpegImageWriter.getExifMetadata(file);
//        if (exifMetadata != null) {
//            Date date = exifMetadata.getDate(ExifTagDateTimeOriginal.INSTANCE);
//            if (date != null) return date.getTime();
//            date = exifMetadata.getDate(ExifTagCreateDate.INSTANCE);
//            if (date != null) return date.getTime();
//        }
        return file.lastModified();
    }

    public static Date getExifDate(File file) {
//        ExifMetadata exifMetadata = JpegImageWriter.getExifMetadata(file);
//        if (exifMetadata != null) {
//            Date date = exifMetadata.getDate(ExifTagDateTimeOriginal.INSTANCE);
//            if (date != null) return date;
//            date = exifMetadata.getDate(ExifTagCreateDate.INSTANCE);
//            if (date != null) return date;
//        }
        return null;
    }

    
    // All exifs tags
//    public static String[][] getExifs(Metadata metadata) {
//
//        Directory directory = metadata.getDirectory(ExifDirectory.class);
//        if (directory != null) {
//            int tagCount = directory.getTagCount();
//            String[][] rawData = new String[tagCount][2];
//            int i = 0;
//            for (Iterator iterator = directory.getTagIterator(); iterator.hasNext();) {
//                Tag tag = (Tag) iterator.next();
//                try {
//                    rawData[i][0] = tag.getTagName();
//                    rawData[i][1] = tag.getDescription();
//                    i++;
//                }
//                catch (MetadataException ex) {
//                    logger.info(tag.getDirectoryName() + " " + tag.getTagName() + " : " + ex.getMessage());
//                }
//            }
//            if (i < tagCount) return multiArrayCopy(rawData, new String[i][2], i);
//            return rawData;
//        }
//
//        return new String[0][2];
//    }

//    private static String[][] multiArrayCopy(String[][] source, String[][] destination, int length) {
//        for (int i = 0; i < length; i++) {
//            System.arraycopy(source[i], 0, destination[i], 0, source[i].length);
//        }
//        return destination;
//    }

//    public static Date getIptcCreatedDate(Metadata metadata, Date date) {
//        if (metadata != null) {
//            try {
//                Directory e = metadata.getDirectory(IptcDirectory.class);
//                if (e.containsTag(IptcDirectory.TAG_DATE_CREATED))
//                    return e.getDate(IptcDirectory.TAG_DATE_CREATED);
//            }
//            catch (MetadataException ex) {
//                logger.info(ex.getMessage());
//            }
//        }
//        return date;
//    }

//    public static Date getIptcReleaseDate(Metadata metadata, Date date) {
//        if (metadata != null) {
//            try {
//                Directory e = metadata.getDirectory(IptcDirectory.class);
//                if (e.containsTag(IptcDirectory.TAG_RELEASE_DATE))
//                    return e.getDate(IptcDirectory.TAG_RELEASE_DATE);
//            }
//            catch (MetadataException ex) {
//                logger.info(ex.getMessage());
//            }
//        }
//        return date;
//    }
}
