/*
 * ImageViewer.java
 *
 * Created on 13 avril 2005, 11:46
 */
package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.imagetool.EXImage;
import com.adlitteram.imagetool.ImageInfo;
import com.adlitteram.imagetool.ImageUtils;
import com.adlitteram.imagetool.ReadParam;
import com.adlitteram.imagetool.Imager;
import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.gui.listener.EscapeListener;
import org.slf4j.LoggerFactory;
import com.adlitteram.jasmin.utils.GuiUtils;
import java.awt.BorderLayout;
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
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import org.slf4j.Logger;
import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import org.apache.commons.lang3.SystemUtils;

public class FullScreenViewer extends Window implements KeyListener, MouseListener, MouseMotionListener {

    private static final Logger LOGGER = LoggerFactory.getLogger(FullScreenViewer.class);
    //
    private JLabel closeLabel;
    private ImagePanel imagePanel;
    private ArrayList<EXImage> selectedImages;
    private EXImage selectedImage;
    private BufferedImage image;
    private int currentIndex;
    private boolean reloadImage = true;
    private boolean subSampling = true;

    public FullScreenViewer(Frame frame, ArrayList<EXImage> selectedImages, EXImage selectedImage) {
        super(frame);
        init(selectedImages, selectedImage);
    }

    public FullScreenViewer(Window window, ArrayList<EXImage> selectedImages, EXImage selectedImage) {
        super(window);
        init(selectedImages, selectedImage);
    }

    private void init(ArrayList<EXImage> selectedImages, EXImage selectedImage) {
        this.selectedImages = selectedImages;
        this.selectedImage = selectedImage;

        imagePanel = new ImagePanel();
        imagePanel.setBackground(Color.GRAY);
        imagePanel.addMouseListener(this);
        imagePanel.addMouseMotionListener(this);

        closeLabel = new JLabel(Message.get("ImageViewer.Close"));
        closeLabel.setForeground(Color.GREEN);
        closeLabel.setBackground(Color.GRAY);
        if (SystemUtils.IS_OS_MAC_OSX) {
            closeLabel.setBorder(BorderFactory.createEmptyBorder(25, 0, 0, 0));
        }

        setLayout(new BorderLayout());
        add(imagePanel, BorderLayout.CENTER);
        add(closeLabel, BorderLayout.NORTH);

        addKeyListener(new EscapeListener(this));
        addKeyListener(this);
        addMouseListener(this);
        addMouseMotionListener(this);

        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(0, 0, screen.width, screen.height);
        setBackground(Color.GRAY);
        setFocusable(true);
        toFront();
        setVisible(true);

        requestFocusInWindow();
        currentIndex = getImageIndex(selectedImage);
    }

//    public void showViewer() {
//
//    }
    public void setLabelVisible(boolean b) {
        closeLabel.setVisible(b);
        validate();
        repaint();
    }

    public int getImageIndex(EXImage eximage) {
        for (int i = 0; i < selectedImages.size(); i++) {
            if (eximage == selectedImages.get(i)) {
                return i;
            }
        }
        return 0;
    }

    public EXImage getImage() {
        return (currentIndex < 0) ? selectedImage : selectedImages.get(currentIndex);
    }

    public void displayNextImage() {
        if (selectedImages.size() > 1) {
            currentIndex++;
            if (currentIndex >= selectedImages.size()) {
                currentIndex = 0;
            }
            displayImage();
        }
        else if (selectedImages.size() <= 0) {
            dispose();
        }
    }

    public void displayPrevImage() {
        if (selectedImages.size() > 1) {
            currentIndex--;
            if (currentIndex < 0) {
                currentIndex = selectedImages.size() - 1;
            }
            displayImage();
        }
        else if (selectedImages.size() <= 0) {
            dispose();
        }
    }

    public void rotateImage() {
        if (image != null) {
            GuiUtils.setCursorOnWait(this, true);
            float ratio = Math.min((float) imagePanel.getWidth() / (float) image.getHeight(), (float) imagePanel.getHeight()
                    / (float) image.getWidth());
            image = (ratio < 1) ? ImageUtils.getScaledRotatedRGBImage(image, ratio, ratio, -Math.PI / 2) : ImageUtils.getRotatedRGBImage(image, Math.PI
                    / 2);
            GuiUtils.setCursorOnWait(this, false);
            imagePanel.repaint();
        }
    }

    private BufferedImage getPreview(int w, int h) {
        EXImage eximage = getImage();
        if (reloadImage && eximage.getPath() != null && eximage.getPath().trim().length() > 0) {
            File file = new File(eximage.getPath());
            ImageInfo info = Imager.readImageInfo(file);
            if (info != null && info.isValidImage()) {
                BufferedImage img;
                if (subSampling) {
                    int sampling = Math.max((int) (info.getWidth() / w) + 1, (int) (info.getHeight() / h) + 1);
                    img = Imager.readImage(file, new ReadParam(sampling));
                }
                else {
                    BufferedImage bi = Imager.readImage(file, new ReadParam(1));
                    double r = Math.min(1, Math.min((double) w / (double) bi.getWidth(), (double) h / (double) bi.getHeight()));
                    img = ImageUtils.getScaledRGBImage(bi, r, r);
                }
                if (img != null) {
                    return img;
                }
            }
        }

        double r = Math.min(1, Math.min((double) w / (double) eximage.getSourceWidth(), (double) h / (double) eximage.getSourceHeight()));
        return ImageUtils.getScaledRGBImage(eximage.getImage(), r, r);
    }

    public void displayImage() {
        GuiUtils.setCursorOnWait(this, true);
        image = getPreview(imagePanel.getWidth(), imagePanel.getHeight());
        GuiUtils.setCursorOnWait(this, false);
        imagePanel.repaint();
    }

    /**
     * @return the reloadImage
     */
    public boolean isReloadImage() {
        return reloadImage;
    }

    /**
     * @param reloadImage the reloadImage to set
     */
    public void setReloadImage(boolean reloadImage) {
        this.reloadImage = reloadImage;
    }

    /**
     * @return the subSampling
     */
    public boolean isSubSampling() {
        return subSampling;
    }

    /**
     * @param subSampling the subSampling to set
     */
    public void setSubSampling(boolean subSampling) {
        this.subSampling = subSampling;
    }

    class ImagePanel extends JPanel {

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (image != null) {
                int x = (int) ((getWidth() - image.getWidth()) / 2f);
                int y = (int) ((getHeight() - image.getHeight()) / 2f);
                g.drawImage(image, x, y, null);
            }
        }
    }

    /**
     * Invoked when a key has been typed. This event occurs when a key press is
     * followed by a key release.
     *
     * @param e
     */
    @Override
    public void keyTyped(KeyEvent e) {
    }

    /**
     * Invoked when a key has been pressed.
     *
     * @param e
     */
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                dispose();
                break;

            case KeyEvent.VK_LEFT:
            case KeyEvent.VK_UP:
                displayPrevImage();
                break;

            case KeyEvent.VK_RIGHT:
            case KeyEvent.VK_DOWN:
                displayNextImage();
                break;

            case KeyEvent.VK_R:
                rotateImage();
                break;
        }

        e.consume();
    }

    /**
     * Invoked when a key has been released.
     *
     * @param e
     */
    @Override
    public void keyReleased(KeyEvent e) {
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on
     * a component.
     *
     * @param e
     */
    @Override
    public void mouseClicked(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     * @param e
     */
    @Override
    public void mousePressed(MouseEvent e) {
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
     * @param e
     */
    @Override
    public void mouseReleased(MouseEvent e) {
        // Dispose current window on mouseReleased and not mousePressed otherwise 
        // remaining (drag, release) mouseEvent are forwarded to the underlying window
        dispose();
    }

    /**
     * Invoked when the mouse enters a component.
     *
     * @param e
     */
    @Override
    public void mouseEntered(MouseEvent e) {
    }

    /**
     * Invoked when the mouse exits a component.
     *
     * @param e
     */
    @Override
    public void mouseExited(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseMoved(MouseEvent e) {
    }
}
