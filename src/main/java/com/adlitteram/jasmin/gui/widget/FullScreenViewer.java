package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.gui.listener.EscapeListener;
import com.adlitteram.jasmin.image.*;
import com.adlitteram.jasmin.utils.GuiUtils;
import org.apache.commons.lang3.SystemUtils;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.List;

public class FullScreenViewer extends Window implements KeyListener, MouseListener, MouseMotionListener {

    private JLabel closeLabel;
    private ImagePanel imagePanel;
    private List<XImage> selectedImages;
    private XImage selectedImage;
    private BufferedImage image;
    private int currentIndex;
    private boolean reloadImage = true;
    private boolean subSampling = true;

    public FullScreenViewer(Frame frame, List<XImage> selectedImages, XImage selectedImage) {
        super(frame);
        init(selectedImages, selectedImage);
    }

    public FullScreenViewer(Window window, List<XImage> selectedImages, XImage selectedImage) {
        super(window);
        init(selectedImages, selectedImage);
    }

    private void init(List<XImage> selectedImages, XImage selectedImage) {
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

    public void setLabelVisible(boolean b) {
        closeLabel.setVisible(b);
        validate();
        repaint();
    }

    public int getImageIndex(XImage ximage) {
        for (int i = 0; i < selectedImages.size(); i++) {
            if (ximage == selectedImages.get(i)) {
                return i;
            }
        }
        return 0;
    }

    public XImage getImage() {
        return (currentIndex < 0) ? selectedImage : selectedImages.get(currentIndex);
    }

    public void displayNextImage() {
        if (selectedImages.size() > 1) {
            currentIndex++;
            if (currentIndex >= selectedImages.size()) {
                currentIndex = 0;
            }
            displayImage();
        } else if (selectedImages.isEmpty()) {
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
        } else if (selectedImages.isEmpty()) {
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
        XImage ximage = getImage();
        if (reloadImage && ximage.getPath() != null && !ximage.getPath().trim().isEmpty()) {
            File file = new File(ximage.getPath());
            ImageInfo info = ImageTool.readImageInfo(file);
            if (info != null && info.isValidImage()) {
                BufferedImage img;
                if (subSampling) {
                    int sampling = Math.max((info.getWidth() / w) + 1, (info.getHeight() / h) + 1);
                    img = ImageTool.readImage(file, new ReadParam(sampling));
                } else {
                    BufferedImage bi = ImageTool.readImage(file, new ReadParam(1));
                    double r = Math.min(1, Math.min((double) w / (double) bi.getWidth(), (double) h / (double) bi.getHeight()));
                    img = ImageUtils.getScaledRGBImage(bi, r, r);
                }
                if (img != null) {
                    return img;
                }
            }
        }

        double r = Math.min(1, Math.min((double) w / (double) ximage.getSourceWidth(), (double) h / (double) ximage.getSourceHeight()));
        return ImageUtils.getScaledRGBImage(ximage.getImage(), r, r);
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
     */
    @Override
    public void keyTyped(KeyEvent e) {
        // Do nothing
    }

    /**
     * Invoked when a key has been pressed.
     *
     */
    @Override
    public void keyPressed(KeyEvent e) {

        switch (e.getKeyCode()) {
            case KeyEvent.VK_ESCAPE:
                dispose();
                break;

            case KeyEvent.VK_LEFT,  KeyEvent.VK_UP:
                displayPrevImage();
                break;

            case KeyEvent.VK_RIGHT, KeyEvent.VK_DOWN:
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
     */
    @Override
    public void keyReleased(KeyEvent e) {
            // Do nothing
    }

    /**
     * Invoked when the mouse button has been clicked (pressed and released) on
     * a component.
     *
     */
    @Override
    public void mouseClicked(MouseEvent e) {
        // Do nothing
    }

    /**
     * Invoked when a mouse button has been pressed on a component.
     *
     */
    @Override
    public void mousePressed(MouseEvent e) {
        // Do nothing
    }

    /**
     * Invoked when a mouse button has been released on a component.
     *
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
     */
    @Override
    public void mouseEntered(MouseEvent e) {
        // Do nothing
    }

    /**
     * Invoked when the mouse exits a component.
     *
     */
    @Override
    public void mouseExited(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        // Do nothing
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        // Do nothing
    }
}
