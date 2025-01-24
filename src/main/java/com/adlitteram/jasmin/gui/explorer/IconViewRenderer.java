package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.utils.GuiUtils;
import com.adlitteram.jasmin.utils.NumUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.*;
import java.awt.image.BufferedImage;

public class IconViewRenderer extends JPanel implements ListCellRenderer<ImageFile> {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM);

    private static final Color BORDER_COLOR = new Color(153, 222, 253);
    private static final Color BG1_COLOR = new Color(240, 248, 253);
    private static final Color BG2_COLOR = new Color(213, 239, 252);
    private static final Color BG3_COLOR = new Color(223, 242, 252);
    private static final Color BG4_COLOR = new Color(245, 190, 130);
    private static final Color BG5_COLOR = new Color(255, 225, 190);

    private final String[] text;

    private Paint selectPaint;
    private Paint checkPaint;
    private Paint overPaint;
    private Border emptyBorder;
    private BufferedImage image;
    private Paint foregroundPaint;
    private Paint backgroundPaint;
    private Paint borderPaint;
    private int gap = 0;
    private int infoDetail;
    private int iconSize;

    public IconViewRenderer() {
        setOpaque(false);
        text = new String[4];
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends ImageFile> list, ImageFile imageFile, int row, boolean isSelected, boolean hasFocus) {
        IconViewList ivList = (IconViewList) list;
        image = imageFile.getImage();
        infoDetail = ivList.getExplorerPane().getInfoDetail();
        iconSize = ivList.getExplorerPane().getIconSize();

        if (infoDetail == ExplorerPane.FULL_INFO) {
            text[0] = imageFile.getName();
            text[1] = (imageFile.getWidth() == 0 || imageFile.getHeight() == 0) ? "" : imageFile.getWidth() + "x" + imageFile.getHeight() + " pixels";
            text[2] = NumUtils.toByteSize(imageFile.getLength()) + ((imageFile.getFormat() == null) ? "" : " - " + imageFile.getFormat());
            text[3] = DATE_FORMAT.format(imageFile.firstCreated());
        } else if (infoDetail == ExplorerPane.BRIEF_INFO) {
            text[0] = imageFile.getName();
        }

        if (ivList.getIconGap() != gap) {
            gap = ivList.getIconGap();
            emptyBorder = BorderFactory.createEmptyBorder(gap + 2, gap + 2, gap + 2, gap + 2);
            overPaint = new GradientPaint(0, 0, BG1_COLOR, 0, ivList.getFixedCellHeight(), BG3_COLOR);
            selectPaint = new GradientPaint(0, 0, BG1_COLOR, 0, ivList.getFixedCellHeight(), BG2_COLOR);
            checkPaint = new GradientPaint(0, 0, BG5_COLOR, 0, ivList.getFixedCellHeight(), BG4_COLOR);
        }

        setBorder(emptyBorder);

        if (isSelected) {
            backgroundPaint = selectPaint;
            borderPaint = BORDER_COLOR;
        } else if (ivList.getOverIndex() == row) {
            backgroundPaint = overPaint;
            borderPaint = null;
        } else {
            backgroundPaint = null;
            borderPaint = null;
        }

        if (ivList.getExplorerPane().checkImageFile(imageFile)) {
            backgroundPaint = checkPaint;
        }

        foregroundPaint = Color.black;
        return this;
    }

    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_OFF);

        float yOffset = 0;

        if (backgroundPaint != null) {
            g2.setPaint(backgroundPaint);
            g2.fillRoundRect(gap, gap, getWidth() - gap * 2, getHeight() - gap * 2, 10, 10);
        }

        if (borderPaint != null) {
            g2.setPaint(borderPaint);
            g2.drawRoundRect(gap, gap, getWidth() - gap * 2, getHeight() - gap * 2, 10, 10);
        }

        if (foregroundPaint != null) {
            if (infoDetail == ExplorerPane.BRIEF_INFO) {
                g2.setColor(Color.BLACK);
                String str = GuiUtils.clipStringIfNecessary(g2.getFontMetrics(), text[0], getWidth() - (gap + 2) * 2);
                int w = SwingUtilities.computeStringWidth(g2.getFontMetrics(), str);
                int x = Math.round((getWidth() - w) / 2f);
                int y = getHeight() - gap - 1 - g2.getFontMetrics().getDescent();
                g2.drawString(str, x, y);
                yOffset = g2.getFontMetrics().getHeight();
            } else if (infoDetail == ExplorerPane.FULL_INFO) {
                g2.setColor(Color.BLACK);
                int h = g2.getFontMetrics().getHeight();
                int y = Math.round((getHeight() - h * text.length) / 2f) + g2.getFontMetrics().getAscent();
                int x = iconSize + gap + 18;
                for (String text1 : text) {
                    String str = GuiUtils.clipStringIfNecessary(g2.getFontMetrics(), text1, getWidth() - x - (gap + 2));
                    g2.drawString(str, x, y);
                    y += h;
                }
            }
        }

        if (image != null) {
            int x, y;
            switch (infoDetail) {
                case ExplorerPane.BRIEF_INFO:
                    x = Math.round((getWidth() - image.getWidth()) / 2f);
                    y = Math.round((getHeight() - gap - 1 - g2.getFontMetrics().getDescent() - yOffset - image.getHeight()));
                    break;
                case ExplorerPane.FULL_INFO:
                    x = gap + 8;
                    y = Math.round((getHeight() - image.getHeight()) / 2f);
                    break;
                default:
                    x = Math.round((getWidth() - image.getWidth()) / 2f);
                    y = Math.round((getHeight() - image.getHeight()) / 2f);
                    break;
            }

            g2.drawImage(image, x, y, this);
            g2.setColor(Color.WHITE);
            g2.drawRect(x - 1, y - 1, image.getWidth() + 1, image.getHeight() + 1);
            g2.drawRect(x - 2, y - 2, image.getWidth() + 3, image.getHeight() + 3);
            g2.setColor(Color.LIGHT_GRAY);
            g2.drawRect(x - 3, y - 3, image.getWidth() + 5, image.getHeight() + 5);
        }

    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void validate() {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override
    public void invalidate() {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     *
     * @since 1.5
     */
    @Override
    public void repaint() {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void revalidate() {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(long tm, int x, int y, int width, int height) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void repaint(Rectangle r) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, byte oldValue, byte newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, char oldValue, char newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, short oldValue, short newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, int oldValue, int newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, long oldValue, long newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, float oldValue, float newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, double oldValue, double newValue) {
        // Do nothing
    }

    /**
     * Overridden for performance reasons. See the
     * <a href="#override">Implementation Note</a>
     * for more information.
     */
    @Override
    public void firePropertyChange(String propertyName, boolean oldValue, boolean newValue) {
        // Do nothing
    }

}
