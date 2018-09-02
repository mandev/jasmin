package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.imagetool.ImageInfo;
import com.adlitteram.imagetool.ImageUtils;
import com.adlitteram.imagetool.ReadParam;
import com.adlitteram.imagetool.Imager;
import com.adlitteram.jasmin.Message;
import org.slf4j.LoggerFactory;
import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.image.BufferedImage;
import java.io.File;
import org.slf4j.Logger;
import javax.swing.JPanel;
import org.apache.commons.lang3.SystemUtils;

public class FullScreenPane extends Window implements KeyListener, MouseListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullScreenPane.class);

    private ImagePanel imagePanel;
    private File[] files;
    private BufferedImage image;
    private int index;
    private int width;
    private int height;
    private int topMargin;

    public FullScreenPane(Frame frame, File[] files, File file) {
        super(frame);
        init(files, file);
    }

    public FullScreenPane(Window window, File[] files, File file) {
        super(window);
        init(files, file);
    }

    private void init(File[] files, File file) {
        this.files = files;
        index = getIndex(file);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        topMargin = SystemUtils.IS_OS_MAC_OSX ? 25 : 0;
        width = screen.width;
        height = screen.height - topMargin;
        image = getPreview(width, height);

        imagePanel = new ImagePanel();
        imagePanel.setBackground(Color.GRAY);
        imagePanel.addMouseListener(this);
        imagePanel.addKeyListener(this);
        imagePanel.requestFocusInWindow();
        add(imagePanel);

        setBackground(Color.GRAY);
        setBounds(0, 0, screen.width, screen.height);
        setFocusable(true);

        toFront();
        setVisible(true);

        imagePanel.requestFocusInWindow();
        toFront();
    }

    private int getIndex(File selectedFile) {
        for (int i = 0; i < files.length; i++) {
            if (selectedFile == files[i]) {
                return i;
            }
        }
        return 0;
    }

    private void nextIndex() {
        index++;
        if (index >= files.length) {
            index = 0;
        }
    }

    private void backIndex() {
        index--;
        if (index < 0) {
            index = files.length - 1;
        }
    }

    public void displayImage() {
        image = getPreview(width, height);
        imagePanel.repaint();
    }

    public void rotateImage() {
        if (image != null) {
            float r = Math.min((float) width / (float) image.getHeight(), (float) height / (float) image.getWidth());
            image = (r < 1) ? ImageUtils.getScaledRotatedRGBImage(image, r, r, -Math.PI / 2d) : ImageUtils.getRotatedRGBImage(image, -Math.PI / 2d);
            imagePanel.repaint();
        }
    }

    private BufferedImage getPreview(int w, int h) {
        GuiUtils.setCursorOnWait(this, true);
        BufferedImage img = null;
        ImageInfo info = Imager.readImageInfo(files[index]);
        if (info != null) {
            int sampling = Math.max(info.getWidth() / w, info.getHeight() / h) + 1;
            img = Imager.readImage(files[index], new ReadParam(sampling));
            if (img != null && (sampling > 1 || img.getWidth() > w || img.getHeight() > h)) {
                img = ImageUtils.getScaledRGBImage(img, w, h, true);
            }
        }
        GuiUtils.setCursorOnWait(this, false);
        return img;
    }

    class ImagePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);

            if (image != null) {
                int x = (int) ((width - image.getWidth()) / 2f);
                int y = (int) ((height - image.getHeight()) / 2f);
                g.drawImage(image, x, y + topMargin, null);
            }

            g.setColor(Color.GREEN);
            g.drawString(Message.get("ImageViewer.Close"), 5, g.getFontMetrics().getHeight() + topMargin);
        }
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                dispose();
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                backIndex();
                displayImage();
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                nextIndex();
                displayImage();
                break;

            case KeyEvent.VK_R:
                rotateImage();
                break;
        }

        e.consume();
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        dispose();
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}
