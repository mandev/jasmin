package com.adlitteram.jasmin.image.writer.ajg;

import com.adlitteram.jasmin.image.XImage;
import java.awt.AlphaComposite;
import java.awt.Graphics2D;
import org.slf4j.LoggerFactory;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.imageio.ImageIO;
import org.slf4j.Logger;

public class AjgImageWriter {

    private static final Logger logger = LoggerFactory.getLogger(AjgImageWriter.class);

    public static void write(XImage ximage, File dstFile) throws IOException {

        try (FileOutputStream fos = new FileOutputStream(dstFile);
                ZipOutputStream zout = new ZipOutputStream(fos)) {

            BufferedImage bi = getRgbImage(ximage.getImage());
            zout.setLevel(0);
            zout.putNextEntry(new ZipEntry("rgb.jpg"));
            ImageIO.write(bi, "jpeg", zout);

            // Save Alpha Raster
            zout.setLevel(9);
            zout.putNextEntry(new ZipEntry("a.bin"));
            try (ObjectOutputStream oos = new ObjectOutputStream(zout)) {
                oos.writeObject(getAlphaMask(bi));
            }
        }
    }

    private static BufferedImage getRgbImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_RGB) {
            BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_RGB);
            Graphics2D g2 = bi.createGraphics();
            //g2.setComposite(AlphaComposite.Src);
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            return bi;
        }
        return image;
    }

    private static BufferedImage getArgbImage(BufferedImage image) {
        if (image.getType() != BufferedImage.TYPE_INT_ARGB) {
            BufferedImage bi = new BufferedImage(image.getWidth(), image.getHeight(), BufferedImage.TYPE_INT_ARGB);
            Graphics2D g2 = bi.createGraphics();
            g2.setComposite(AlphaComposite.Src);
            g2.drawImage(image, 0, 0, null);
            g2.dispose();
            return bi;
        }
        return image;
    }

    private static byte[] getAlphaMask(BufferedImage image) {
        BufferedImage bi = getArgbImage(image);
        int[] pixels = ((DataBufferInt) bi.getRaster().getDataBuffer()).getData();
        byte mask[] = new byte[pixels.length];
        for (int i = 0; i < pixels.length; i++) {
            mask[i] = (byte) ((pixels[i] >> 24) & 0xFF);
        }
        return mask;
    }
}
