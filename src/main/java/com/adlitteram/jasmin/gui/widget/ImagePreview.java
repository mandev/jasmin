package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.image.ImageInfo;
import com.adlitteram.jasmin.image.ImageTool;
import com.adlitteram.jasmin.image.ReadParam;
import com.adlitteram.jasmin.image.XImage;
import org.apache.commons.io.FileUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;

public class ImagePreview extends JComponent implements PropertyChangeListener, Runnable {

    private Thread thread;
    private ThumbFile thumbFile;
    private XImage ximage;

    private ImagePreview() {
        setPreferredSize(new Dimension(150, 150));
    }

    public static ImagePreview decorate(JFileChooser jfc) {
        ImagePreview ip = new ImagePreview();
        jfc.setAccessory(ip);
        jfc.addPropertyChangeListener(ip);
        ip.start();
        return ip;
    }

    public static ImagePreview decorate(FileChooser fc) {
        return (fc.getChooser() instanceof JFileChooser) ? decorate((JFileChooser) fc.getChooser()) : null;
    }

    // Start the thread
    public void start() {
        if (thread == null) {
            thread = new Thread(this);
            thread.setPriority(Thread.MIN_PRIORITY);
            thread.start();
        }
    }

    // Stop the running thread
    public void stop() {
        if (thread != null) {
            thread.interrupt();
            thread = null;
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent e) {
        String prop = e.getPropertyName();
        if (prop.equals(JFileChooser.SELECTED_FILE_CHANGED_PROPERTY)) {
            Object value = e.getNewValue();
            if (value != null && isShowing()) {
                setThumbFile(new ThumbFile((File) value, getWidth(), getHeight()));
            }
        } else if ("JFileChooserDialogIsClosingProperty".equals(prop)) {
            stop();
        }
    }

    private synchronized void setThumbFile(ThumbFile thumbFile) {
        this.thumbFile = thumbFile;
        notifyAll();
    }

    private synchronized ThumbFile getThumbFile() {
        while (thumbFile == null) {
            try {
                wait();
            } catch (InterruptedException e) {
                //logger.warn("", e);
            }
        }
        ThumbFile tf = thumbFile;
        thumbFile = null;
        return tf;
    }

    @Override
    public void run() {
        while (thread == Thread.currentThread()) {
            final XImage thumbImage = getThumbnail(getThumbFile());
            SwingUtilities.invokeLater(() -> {
                ximage = thumbImage;
                repaint();
            });
        }
    }

    private XImage getThumbnail(ThumbFile tf) {
        ImageInfo info = ImageTool.readImageInfo(tf.getFile());
        if (info != null && info.isValidImage()) {
            int w = (info.getWidth() / (tf.getWidth() - 10)) + 1;
            int h = (info.getHeight() / (tf.getHeight() - 17)) + 1;
            int sampling = Math.max(w, h);
            return ImageTool.readXImage(tf.getFile(), new ReadParam(sampling));
        }
        return null;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (ximage != null) {
            BufferedImage image = ximage.getImage();
            g.drawImage(image, (getWidth() - image.getWidth()) / 2 + 1, 17, null);
            g.drawString(ximage.getSourceWidth() + "x" + ximage.getSourceHeight() + " pixels - "
                    + FileUtils.byteCountToDisplaySize(ximage.getSize()), 5, 10);
        }
    }

    private static class ThumbFile {

        private final File file;
        private final int width;
        private final int height;

        public ThumbFile(File file, int width, int height) {
            this.file = file;
            this.width = width;
            this.height = height;
        }

        public File getFile() {
            return file;
        }

        public int getHeight() {
            return height;
        }

        public int getWidth() {
            return width;
        }
    }
}
