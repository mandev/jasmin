package com.adlitteram.jasmin.image;

import static com.adlitteram.jasmin.image.ImageUtils.closeQuietly;

import com.twelvemonkeys.imageio.stream.BufferedFileImageInputStream;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;
import javax.imageio.ImageIO;
import javax.imageio.ImageReadParam;
import javax.imageio.ImageReader;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.stream.ImageInputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ImageTool {

  private static final Logger LOGGER = LoggerFactory.getLogger(ImageReader.class);

  static {
    ImageIO.setUseCache(false);
  }

  public static ImageInfo readImageInfo(File file) {
    ImageReader reader = null;
    ImageInfo imageInfo = null;

    try (ImageInputStream iis = new BufferedFileImageInputStream(file)) {
      Iterator<ImageReader> iter = ImageIO.getImageReaders(iis);
      if (iter.hasNext()) {
        reader = iter.next();
        reader.setInput(iis, false, true);
        int w = reader.getWidth(0);
        int h = reader.getHeight(0);
        if (w > 0 && h > 0) {

          ImageTypeSpecifier its = reader.getRawImageType(0);
          if (its == null) {
            Iterator<ImageTypeSpecifier> it = reader.getImageTypes(0);
            its = it.next();
          }
          boolean hasAlpha = its.getColorModel().hasAlpha();
          imageInfo = new ImageInfo(w, h, reader.getFormatName(), hasAlpha);
        } else {
          throw new IOException("Unable to read image data");
        }
      }
    } catch (Exception ex) {
      LOGGER.warn(file.getPath(), ex);
    } finally {
      disposeQuietly(reader);
    }

    if (imageInfo == null) {
      imageInfo = readAjgImageInfo(file);
    }

    return imageInfo;
  }

  public static BufferedImage readImage(File file) {
    return readXImage(file, new ReadParam()).getImage();
  }

  public static BufferedImage readImage(File file, ReadParam rparam) {
    return readXImage(file, rparam).getImage();
  }

  public static XImage readXImage(File file) {
    return readXImage(file, new ReadParam());
  }

  public static XImage readXImage(File file, ReadParam rparam) {
    ImageReader reader = null;
    XImage xImg = null;

    try (ImageInputStream iis = new BufferedFileImageInputStream(file)) {

      Iterator<ImageReader> readers = ImageIO.getImageReaders(iis);
      if (readers.hasNext()) {
        reader = readers.next();
        reader.setInput(iis, false, true); // Don't read Metatdata

        ImageTypeSpecifier its = reader.getRawImageType(0);
        if (its == null) {
          Iterator<ImageTypeSpecifier> it = reader.getImageTypes(0);
          its = it.next();
        }

        // Set Input
        ImageReadParam param = reader.getDefaultReadParam();
        param.setSourceSubsampling(rparam.getSubSampling(), rparam.getSubSampling(), 0, 0);
        param.setDestinationType(its);

        // Set Properties
        String format = reader.getFormatName();
        String path = file.getPath();
        long size = file.length();
        long time = file.lastModified();
        int width = reader.getWidth(0);
        int height = reader.getHeight(0);

        BufferedImage image = reader.read(0, param);
        if (image != null) {
          xImg = XImage.createXImage(image, width, height, format, size, time, rparam.getSubSampling(), path);
        }
      }
    } catch (Exception ex) {
      LOGGER.warn(file.getPath(), ex);
    } finally {
      disposeQuietly(reader);
    }

    if (xImg == null) {
      xImg = readAjgImage(file, rparam);
    }

    return xImg;
  }

  // Read AJG Image Info
  private static ImageInfo readAjgImageInfo(File file) {
    ZipFile zipFile = null;
    ImageInputStream iis = null;
    ImageReader reader = null;
    ImageInfo imageInfo = null;

    try {
      zipFile = new ZipFile(file);

      // Jpeg Entry
      ZipEntry entry = zipFile.getEntry("rgb.jpg");
      if (entry == null) {
        throw new ZipException("The rgb.jpg entry does not exist");
      }

      InputStream is = zipFile.getInputStream(entry);
      iis = ImageIO.createImageInputStream(is);
      Iterator iter = ImageIO.getImageReaders(iis);
      reader = (ImageReader) iter.next();
      reader.setInput(iis, false, true);

      int w = reader.getWidth(0);
      int h = reader.getHeight(0);
      if (w > 0 && h > 0) {
        imageInfo = new ImageInfo(w, h, "ajg", true);
      } else {
        throw new IOException("Unable to read image data");
      }
    } catch (Exception ex) {
      LOGGER.warn(file.getPath() + " " + ex.getMessage());
    } finally {
      disposeQuietly(reader);
      closeQuietly(zipFile);
      closeQuietly(iis);
    }
    return imageInfo;
  }

  // Read AJG Image  (Zip with (CS_sRGB) Jpeg and Alpha Mask)
  private static XImage readAjgImage(File file, ReadParam rparam) {
    XImage xImg = null;
    ZipFile zipFile = null;
    ImageInputStream iis = null;
    ImageReader reader = null;
    byte[] alphaBuffer = null;

    try {
      String path = file.getPath();
      long size = file.length();
      long time = file.lastModified();

      zipFile = new ZipFile(file);

      // Jpeg Entry
      ZipEntry entry = zipFile.getEntry("rgb.jpg");
      if (entry == null) {
        throw new ZipException("The rgb.jpg entry does not exist in the file");
      }

      InputStream is = zipFile.getInputStream(entry);
      iis = ImageIO.createImageInputStream(is);
      Iterator iterator = ImageIO.getImageReaders(iis);
      reader = (ImageReader) iterator.next();
      reader.setInput(iis, false, true); // Ignore Metatdata

      int sampling = rparam.getSubSampling();
      int width = reader.getWidth(0);
      int height = reader.getHeight(0);

      ImageReadParam param = reader.getDefaultReadParam();
      param.setSourceSubsampling(sampling, sampling, 0, 0);
      BufferedImage bi = reader.read(0, param);
      is.close();

      // Alpha Entry
      entry = zipFile.getEntry("a.bin");
      if (entry != null) {
        is = zipFile.getInputStream(entry);
        ObjectInputStream ois = new ObjectInputStream(is);
        try {
          alphaBuffer = (byte[]) ois.readObject();
        } catch (ClassNotFoundException ex) {
          LOGGER.warn(null, ex);
        }
        is.close();
      }

      // Create an image with an int pixels databuffer
      if (bi.getType() != BufferedImage.TYPE_INT_RGB || bi.getType() != BufferedImage.TYPE_INT_ARGB) {
        BufferedImage bi2 = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_RGB);
        Graphics2D g2 = bi2.createGraphics();
        g2.drawImage(bi, 0, 0, null);
        g2.dispose();
        bi = bi2;
      }

      // Create ARGB Image
      BufferedImage image = new BufferedImage(bi.getWidth(), bi.getHeight(), BufferedImage.TYPE_INT_ARGB);
      int[] pixels = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();

      for (int i = 0, y = 0; y < height; y += sampling) {
        for (int x = 0; x < width; x += sampling, i++) {
          pixels[i] |= alphaBuffer[y * width + x] & 0xff << 24;
        }
      }

      image.getRaster().setDataElements(0, 0, bi.getWidth(), bi.getHeight(), pixels);
      xImg = XImage.createXImage(image, width, height, "ajg", size, time, sampling, path);
    } catch (Exception ex) {
      LOGGER.warn(file.getPath() + " " + ex.getMessage());
    } finally {
      disposeQuietly(reader);
      closeQuietly(zipFile);
      closeQuietly(iis);
    }
    return xImg;
  }

  private static void disposeQuietly(ImageReader reader) {
    if (reader != null) {
      reader.dispose();
    }
  }

}
