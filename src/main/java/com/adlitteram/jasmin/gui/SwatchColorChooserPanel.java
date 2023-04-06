package com.adlitteram.jasmin.gui;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.Serializable;
import javax.accessibility.AccessibleContext;
import javax.swing.Icon;
import javax.swing.JColorChooser;
import javax.swing.JPanel;
import javax.swing.UIManager;
import javax.swing.border.Border;
import javax.swing.border.CompoundBorder;
import javax.swing.border.LineBorder;
import javax.swing.colorchooser.AbstractColorChooserPanel;
import javax.swing.colorchooser.ColorSelectionModel;

public class SwatchColorChooserPanel extends AbstractColorChooserPanel {

    SwatchPanel swatchPanel;
    MouseListener mainSwatchListener;
    private KeyListener mainSwatchKeyListener;

    public SwatchColorChooserPanel() {
        super();
        setInheritsPopupMenu(true);
    }

    int getInt(Object key, int defaultValue) {
        Object value = UIManager.get(key, getLocale());

        if (value instanceof Integer) {
            return ((Integer) value);
        }
        if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException nfe) {
            }
        }
        return defaultValue;
    }

    @Override
    public String getDisplayName() {
        return UIManager.getString("ColorChooser.swatchesNameText", getLocale());
    }

    /**
     * Provides a hint to the look and feel as to the <code>KeyEvent.VK</code>
     * constant that can be used as a mnemonic to access the panel. A return
     * value <= 0 indicates there is no mnemonic. <p>
     * The return value here is a hint, it is ultimately up to the look and feel
     * to honor the return value in some meaningful way.
     * <p>
     * This implementation looks up the value from the default
     * <code>ColorChooser.swatchesMnemonic</code>, or if it isn't available (or
     * not an <code>Integer</code>) returns -1. The lookup for the default is
     * done through the <code>UIManager</code>:
     * <code>UIManager.get("ColorChooser.swatchesMnemonic");</code>.
     *
     * @return KeyEvent.VK constant identifying the mnemonic; <= 0 for no
     * mnemonic @see #getDisplayedMnemonicI ndex @since 1.4
     */
    @Override
    public int getMnemonic() {
        return getInt("ColorChooser.swatchesMnemonic", -1);
    }

    /**
     * Provides a hint to the look and feel as to the index of the character in
     * <code>getDisplayName</code> that should be visually identified as the
     * mnemonic. The look and feel should only use this if
     * <code>getMnemonic</code> returns a value > 0.
     * <p>
     * The return value here is a hint, it is ultimately up to the look and feel
     * to honor the return value in some meaningful way. For example, a look and
     * feel may wish to render each <code>AbstractColorChooserPanel</code> in a
     * <code>JTabbedPane</code>, and further use this return value to underline
     * a character in the <code>getDisplayName</code>.
     * <p>
     * This implementation looks up the value from the default
     * <code>ColorChooser.rgbDisplayedMnemonicIndex</code>, or if it isn't
     * available (or not an <code>Integer</code>) returns -1. The lookup for the
     * default is done through the <code>UIManager</code>:
     * <code>UIManager.get("ColorChooser.swatchesDisplayedMnemonicIndex");</code>.
     *
     * @return Character index to render mnemonic for; -1 to provide no visual
     * identifier for this panel.
     * @see #getMnemonic
     * @since 1.4
     */
    @Override
    public int getDisplayedMnemonicIndex() {
        return getInt("ColorChooser.swatchesDisplayedMnemonicIndex", -1);
    }

    @Override
    public Icon getSmallDisplayIcon() {
        return null;
    }

    @Override
    public Icon getLargeDisplayIcon() {
        return null;
    }

    /**
     * The background color, foreground color, and font are already set to the
     * defaults from the defaults table before this method is called.
     */
    @Override
    public void installChooserPanel(JColorChooser enclosingChooser) {
        super.installChooserPanel(enclosingChooser);
    }

    @Override
    protected void buildChooser() {

        GridBagLayout gb = new GridBagLayout();
        GridBagConstraints gbc = new GridBagConstraints();
        JPanel superHolder = new JPanel(gb);

        swatchPanel = new MainSwatchPanel();
        swatchPanel.putClientProperty(AccessibleContext.ACCESSIBLE_NAME_PROPERTY, getDisplayName());
        swatchPanel.setInheritsPopupMenu(true);

        mainSwatchKeyListener = new MainSwatchKeyListener();
        mainSwatchListener = new MainSwatchListener();
        swatchPanel.addMouseListener(mainSwatchListener);
        swatchPanel.addKeyListener(mainSwatchKeyListener);

        JPanel mainHolder = new JPanel(new BorderLayout());
        Border border = new CompoundBorder(new LineBorder(Color.black), new LineBorder(Color.white));
        mainHolder.setBorder(border);
        mainHolder.add(swatchPanel, BorderLayout.CENTER);

        gbc.anchor = GridBagConstraints.LAST_LINE_START;
        gbc.gridwidth = 1;
        gbc.gridheight = 2;
        Insets oldInsets = gbc.insets;
        gbc.insets = new Insets(0, 0, 0, 5);
        superHolder.add(mainHolder, gbc);
        gbc.insets = oldInsets;
        superHolder.setInheritsPopupMenu(true);
        add(superHolder);
    }

    @Override
    public void uninstallChooserPanel(JColorChooser enclosingChooser) {
        super.uninstallChooserPanel(enclosingChooser);
        swatchPanel.removeMouseListener(mainSwatchListener);
        swatchPanel.removeKeyListener(mainSwatchKeyListener);
        swatchPanel = null;
        mainSwatchListener = null;
        mainSwatchKeyListener = null;
        removeAll();  // strip out all the sub-components
    }

    @Override
    public void updateChooser() {

    }

    void setSelectedColor(Color color) {
        ColorSelectionModel model = getColorSelectionModel();
        if (model != null) {
            model.setSelectedColor(color);
        }
    }

    private class MainSwatchKeyListener extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            if (KeyEvent.VK_SPACE == e.getKeyCode()) {
                Color color = swatchPanel.getSelectedColor();
                setSelectedColor(color);
            }
        }
    }

    class MainSwatchListener extends MouseAdapter implements Serializable {

        @Override
        public void mousePressed(MouseEvent e) {
            if (isEnabled()) {
                Color color = swatchPanel.getColorForLocation(e.getX(), e.getY());
                setSelectedColor(color);
                swatchPanel.setSelectedColorFromLocation(e.getX(), e.getY());
                swatchPanel.requestFocusInWindow();
            }
        }
    }

}

class SwatchPanel extends JPanel {

    protected Color[] colors;
    protected Dimension swatchSize;
    protected Dimension numSwatches;
    protected Dimension gap;

    private int selRow;
    private int selCol;

    public SwatchPanel() {
        initValues();
        initColors();
        setToolTipText(""); // register for events
        setOpaque(true);
        setBackground(Color.white);
        setFocusable(true);
        setInheritsPopupMenu(true);

        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(FocusEvent e) {
                repaint();
            }

            @Override
            public void focusLost(FocusEvent e) {
                repaint();
            }
        });

        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent e) {
                int typed = e.getKeyCode();
                switch (typed) {
                    case KeyEvent.VK_UP:
                        if (selRow > 0) {
                            selRow--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_DOWN:
                        if (selRow < numSwatches.height - 1) {
                            selRow++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_LEFT:
                        if (selCol > 0 && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        } else if (selCol < numSwatches.width - 1
                                && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_RIGHT:
                        if (selCol < numSwatches.width - 1
                                && SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol++;
                            repaint();
                        } else if (selCol > 0 && !SwatchPanel.this.getComponentOrientation().isLeftToRight()) {
                            selCol--;
                            repaint();
                        }
                        break;
                    case KeyEvent.VK_HOME:
                        selCol = 0;
                        selRow = 0;
                        repaint();
                        break;
                    case KeyEvent.VK_END:
                        selCol = numSwatches.width - 1;
                        selRow = numSwatches.height - 1;
                        repaint();
                        break;
                }
            }
        });
    }

    public Color getSelectedColor() {
        return getColorForCell(selCol, selRow);
    }

    protected void initValues() {

    }

    @Override
    public void paintComponent(Graphics g) {
        g.setColor(getBackground());
        g.fillRect(0, 0, getWidth(), getHeight());
        for (int row = 0; row < numSwatches.height; row++) {
            int y = row * (swatchSize.height + gap.height);
            for (int column = 0; column < numSwatches.width; column++) {
                Color c = getColorForCell(column, row);
                g.setColor(c);
                int x;
                if (!this.getComponentOrientation().isLeftToRight()) {
                    x = (numSwatches.width - column - 1) * (swatchSize.width + gap.width);
                } else {
                    x = column * (swatchSize.width + gap.width);
                }
                g.fillRect(x, y, swatchSize.width, swatchSize.height);
                g.setColor(Color.black);
                g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);

                if (selRow == row && selCol == column && this.isFocusOwner()) {
                    Color c2 = new Color(c.getRed() < 125 ? 255 : 0,
                            c.getGreen() < 125 ? 255 : 0,
                            c.getBlue() < 125 ? 255 : 0);
                    g.setColor(c2);

                    g.drawLine(x, y, x + swatchSize.width - 1, y);
                    g.drawLine(x, y, x, y + swatchSize.height - 1);
                    g.drawLine(x + swatchSize.width - 1, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y, x + swatchSize.width - 1, y + swatchSize.height - 1);
                    g.drawLine(x, y + swatchSize.height - 1, x + swatchSize.width - 1, y);
                }
            }
        }
    }

    @Override
    public Dimension getPreferredSize() {
        int x = numSwatches.width * (swatchSize.width + gap.width) - 1;
        int y = numSwatches.height * (swatchSize.height + gap.height) - 1;
        return new Dimension(x, y);
    }

    protected void initColors() {
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        Color color = getColorForLocation(e.getX(), e.getY());
        return color.getRed() + ", " + color.getGreen() + ", " + color.getBlue();
    }

    public void setSelectedColorFromLocation(int x, int y) {
        if (!this.getComponentOrientation().isLeftToRight()) {
            selCol = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            selCol = x / (swatchSize.width + gap.width);
        }
        selRow = y / (swatchSize.height + gap.height);
        repaint();
    }

    public Color getColorForLocation(int x, int y) {
        int column;
        if (!this.getComponentOrientation().isLeftToRight()) {
            column = numSwatches.width - x / (swatchSize.width + gap.width) - 1;
        } else {
            column = x / (swatchSize.width + gap.width);
        }
        int row = y / (swatchSize.height + gap.height);
        return getColorForCell(column, row);
    }

    private Color getColorForCell(int column, int row) {
        return colors[(row * numSwatches.width) + column]; // (STEVE) - change data orientation here
    }

}

class MainSwatchPanel extends SwatchPanel {

    @Override
    protected void initValues() {
        swatchSize = UIManager.getDimension("ColorChooser.swatchesSwatchSize", getLocale());
        numSwatches = new Dimension(23, 7);
        gap = new Dimension(1, 1);
    }

    @Override
    protected void initColors() {
        int[] rawValues = initRawValues();
        int numColors = rawValues.length / 3;

        colors = new Color[numColors];
        for (int i = 0; i < numColors; i++) {
            colors[i] = new Color(rawValues[(i * 3)], rawValues[(i * 3) + 1], rawValues[(i * 3) + 2]);
        }
    }

    private int[] initRawValues() {
        return new int[]{
            255, 255, 255, // first row
            205, 198, 192,
            218, 215, 203,
            216, 199, 185,
            229, 203, 177,
            248, 204, 166,
            252, 209, 137,
            241, 227, 187,
            237, 235, 170,
            225, 222, 174,
            200, 229, 154,
            156, 220, 217,
            209, 223, 214,
            224, 230, 230,
            215, 223, 230,
            143, 202, 231,
            202, 227, 233,
            224, 220, 227,
            241, 219, 223,
            233, 197, 203,
            235, 202, 184,
            255, 218, 185,
            234, 196, 183,
            220, 220, 220, // second row
            181, 172, 166,
            202, 192, 182,
            205, 182, 167,
            221, 185, 154,
            250, 195, 150,
            255, 198, 116,
            235, 221, 156,
            237, 234, 156,
            218, 214, 156,
            189, 225, 138,
            99, 206, 202,
            183, 206, 196,
            172, 192, 198,
            197, 210, 224,
            94, 182, 228,
            114, 181, 204,
            220, 216, 226,
            243, 201, 211,
            229, 154, 170,
            235, 189, 169,
            255, 160, 122,
            198, 166, 147,
            175, 175, 175, // third row
            162, 151, 145,
            189, 177, 166,
            192, 164, 148,
            211, 169, 133,
            251, 175, 115,
            255, 182, 82,
            232, 206, 121,
            237, 233, 140,
            211, 205, 139,
            165, 216, 103,
            0, 178, 169,
            157, 188, 176,
            164, 174, 181,
            155, 178, 206,
            0, 152, 219,
            48, 149, 180,
            198, 189, 210,
            244, 178, 193,
            219, 77, 105,
            233, 166, 143,
            255, 99, 71,
            182, 115, 92,
            153, 153, 153, // fourth row
            141, 129, 123,
            170, 156, 143,
            171, 136, 118,
            189, 138, 94,
            246, 146, 64,
            255, 160, 47,
            243, 211, 17,
            236, 227, 84,
            198, 191, 112,
            105, 190, 40,
            0, 135, 112,
            124, 162, 149,
            137, 150, 160,
            112, 144, 183,
            0, 91, 187,
            0, 131, 190,
            160, 146, 180,
            242, 140, 163,
            208, 16, 58,
            231, 143, 119,
            240, 128, 128,
            172, 103, 94,
            102, 102, 102, // fifth row
            118, 106, 101,
            133, 115, 99,
            150, 109, 91,
            167, 111, 62,
            236, 122, 8,
            212, 118, 0,
            236, 194, 0,
            234, 222, 41,
            174, 164, 68,
            88, 166, 24,
            0, 115, 99,
            87, 133, 117,
            81, 98, 111,
            70, 130, 180,
            0, 84, 159,
            0, 90, 139,
            120, 101, 146,
            234, 102, 130,
            183, 18, 52,
            213, 43, 30,
            205, 92, 92,
            165, 89, 76,
            51, 51, 51, // sixth row
            93, 79, 75,
            102, 85, 70,
            119, 74, 52,
            139, 69, 19,
            217, 94, 0,
            156, 97, 20,
            235, 183, 0,
            225, 205, 0,
            155, 143, 46,
            91, 143, 34,
            0, 103, 90,
            44, 94, 79,
            57, 74, 88,
            0, 49, 80,
            0, 60, 105,
            0, 44, 95,
            97, 77, 125,
            222, 69, 97,
            152, 30, 50,
            152, 50, 34,
            178, 34, 34,
            155, 110, 81,
            1, 1, 1, // seventh row
            51, 43, 42,
            74, 60, 49,
            82, 45, 36,
            95, 51, 22,
            200, 78, 0,
            106, 73, 28,
            242, 175, 0,
            212, 186, 0,
            136, 123, 27,
            83, 104, 43,
            0, 87, 81,
            19, 53, 44,
            25, 39, 50,
            3, 30, 47,
            0, 43, 69,
            0, 32, 78,
            65, 45, 93,
            207, 47, 68,
            130, 36, 51,
            103, 51, 39,
            128, 0, 0,
            141, 60, 30
        };

    }
}
