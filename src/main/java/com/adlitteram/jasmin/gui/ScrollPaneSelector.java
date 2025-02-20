package com.adlitteram.jasmin.gui;

import javax.swing.*;
import javax.swing.event.MouseInputAdapter;
import javax.swing.event.MouseInputListener;
import java.awt.*;
import java.awt.event.ContainerAdapter;
import java.awt.event.ContainerEvent;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;
import java.util.Map;

/**
 * ScrollPaneSelector is a little utility class that provides a means to quickly
 * scroll both vertically and horizontally on a single mouse click, by dragging
 * a selection rectangle over a "thumbnail" of the scrollPane's viewport view.
 * <p>
 * Once the selector is installed on a given JScrollPane instance, a little
 * button appears as soon as at least one of its scroll bars is made visible.
 *
 * @author weebib
 */
public class ScrollPaneSelector extends JComponent {

    private static final Color GRAY_ALPHA = new Color(200, 200, 200, 128);
    private static final double MAX_SIZE = 200;
    private static final Icon LAUNCH_SELECTOR_ICON = new Icon() {

        @Override
        public void paintIcon(Component c, Graphics g, int x, int y) {
            Color tmpColor = g.getColor();
            g.setColor(Color.DARK_GRAY);
            g.drawRect(2, 2, 10, 10);
            g.drawRect(4, 5, 6, 4);
            g.setColor(tmpColor);
        }

        @Override
        public int getIconWidth() {
            return 15;
        }

        @Override
        public int getIconHeight() {
            return 15;
        }
    };
    private static final Map<JScrollPane, ScrollPaneSelector> theInstalledScrollPaneSelectors = new HashMap<>();
    private static final String COMPONENT_ORIENTATION = "componentOrientation";

    private LayoutManager theFormerLayoutManager;
    private JScrollPane theScrollPane;
    private JComponent theComponent;
    private final JPopupMenu thePopupMenu;
    private final JComponent theButton;
    private BufferedImage theImage;
    private Rectangle theStartRectangle;
    private Rectangle theRectangle;
    private Point theStartPoint;
    private double theScale;
    private final PropertyChangeListener theComponentOrientationListener;
    private final ContainerAdapter theViewPortViewListener;

    /**
     * Installs a ScrollPaneSelector to the given JScrollPane instance.
     *
     * @param aScrollPane
     */
    public static synchronized void installScrollPaneSelector(JScrollPane aScrollPane) {
        if (aScrollPane == null) {
            return;
        }
        if (theInstalledScrollPaneSelectors.containsKey(aScrollPane)) {
            return;
        }
        ScrollPaneSelector scrollPaneSelector = new ScrollPaneSelector();
        scrollPaneSelector.installOnScrollPane(aScrollPane);
        theInstalledScrollPaneSelectors.put(aScrollPane, scrollPaneSelector);
    }

    /**
     * Removes the ScrollPaneSelector from the given JScrollPane.
     *
     * @param aScrollPane
     */
    public static synchronized  void uninstallScrollPaneSelector(JScrollPane aScrollPane) {
        if (aScrollPane == null) {
            return;
        }
        ScrollPaneSelector scrollPaneSelector = theInstalledScrollPaneSelectors.get(aScrollPane);
        if (scrollPaneSelector == null) {
            return;
        }
        scrollPaneSelector.uninstallFromScrollPane();
        theInstalledScrollPaneSelectors.remove(aScrollPane);
    }

    //-- Constructor ------
    private ScrollPaneSelector() {
        setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));
        theScrollPane = null;
        theImage = null;
        theStartRectangle = null;
        theRectangle = null;
        theStartPoint = null;
        theScale = 0.0;
        theButton = new JLabel(LAUNCH_SELECTOR_ICON);
        MouseInputListener mil = new MouseInputAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                Point p = e.getPoint();
                SwingUtilities.convertPointToScreen(p, theButton);
                display(p);
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                if (theStartPoint != null) {
                    Point newPoint = e.getPoint();
                    SwingUtilities.convertPointToScreen(newPoint, (Component) e.getSource());
                    int deltaX = (int) ((newPoint.x - theStartPoint.x) / theScale);
                    int deltaY = (int) ((newPoint.y - theStartPoint.y) / theScale);
                    scroll(deltaX, deltaY);
                }
                theStartPoint = null;
                theStartRectangle = theRectangle;
            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (theStartPoint == null) {
                    return;
                }
                Point newPoint = e.getPoint();
                SwingUtilities.convertPointToScreen(newPoint, (Component) e.getSource());
                moveRectangle(newPoint.x - theStartPoint.x, newPoint.y - theStartPoint.y);
            }
        };
        theButton.addMouseListener(mil);
        theButton.addMouseMotionListener(mil);
        setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
        thePopupMenu = new JPopupMenu();
        thePopupMenu.setLayout(new BorderLayout());
        thePopupMenu.add(this, BorderLayout.CENTER);
        theComponentOrientationListener = (PropertyChangeEvent evt) -> {
            if (theScrollPane != null) {
                theScrollPane.setCorner(ScrollPaneConstants.LOWER_LEADING_CORNER, null);
                theScrollPane.setCorner(ScrollPaneConstants.LOWER_TRAILING_CORNER, theButton);
            }
        };
        theViewPortViewListener = new ContainerAdapter() {

            @Override
            public void componentAdded(ContainerEvent e) {
                if (thePopupMenu.isVisible()) {
                    thePopupMenu.setVisible(false);
                }
                Component comp = theScrollPane.getViewport().getView();
                theComponent = (comp instanceof JComponent c) ? c : null;
            }
        };
    }

    //-- JComponent overriden methods ------
    @Override
    public Dimension getPreferredSize() {
        if (theImage == null || theRectangle == null) {
            return new Dimension();
        }
        Insets insets = getInsets();
        return new Dimension(theImage.getWidth(null) + insets.left + insets.right,
                theImage.getHeight(null) + insets.top + insets.bottom);
    }

    @Override
    protected void paintComponent(Graphics g1D) {
        if (theImage == null || theRectangle == null) {
            return;
        }
        Graphics2D g = (Graphics2D) g1D;
        Insets insets = getInsets();
        int xOffset = insets.left;
        int yOffset = insets.top;
        int availableWidth = getWidth() - insets.left - insets.right;
        int availableHeight = getHeight() - insets.top - insets.bottom;
        g.drawImage(theImage, xOffset, yOffset, null);
        Color tmpColor = g.getColor();
        Rectangle fullRect = new Rectangle(xOffset, yOffset, availableWidth, availableHeight);
        Rectangle[] rects = SwingUtilities.computeDifference(fullRect, theRectangle);
        g.setColor(GRAY_ALPHA);
        for (Rectangle r : rects) {
            g.fillRect(r.x, r.y, r.width, r.height);
        }
        g.setColor(Color.BLACK);
        g.draw(theRectangle);
        g.setColor(tmpColor);
    }

    //-- Private methods ------
    private void installOnScrollPane(JScrollPane aScrollPane) {
        if (theScrollPane != null) {
            uninstallFromScrollPane();
        }
        theScrollPane = aScrollPane;
        theFormerLayoutManager = theScrollPane.getLayout();
        theScrollPane.setLayout(new TweakedScrollPaneLayout());
        theScrollPane.addPropertyChangeListener(COMPONENT_ORIENTATION, theComponentOrientationListener);
        theScrollPane.getViewport().addContainerListener(theViewPortViewListener);
        theScrollPane.setCorner(ScrollPaneConstants.LOWER_TRAILING_CORNER, theButton);
        Component comp = theScrollPane.getViewport().getView();
        theComponent = (comp instanceof JComponent c) ? c : null;
    }

    private void uninstallFromScrollPane() {
        if (theScrollPane == null) {
            return;
        }
        if (thePopupMenu.isVisible()) {
            thePopupMenu.setVisible(false);
        }
        theScrollPane.setCorner(ScrollPaneConstants.LOWER_TRAILING_CORNER, null);
        theScrollPane.removePropertyChangeListener(COMPONENT_ORIENTATION, theComponentOrientationListener);
        theScrollPane.getViewport().removeContainerListener(theViewPortViewListener);
        theScrollPane.setLayout(theFormerLayoutManager);
        theScrollPane = null;
    }

    private void display(Point aPointOnScreen) {
        if (theComponent == null) {
            return;
        }
        double compWidth = theComponent.getWidth();
        double compHeight = theComponent.getHeight();
        double scaleX = MAX_SIZE / compWidth;
        double scaleY = MAX_SIZE / compHeight;
        theScale = Math.min(scaleX, scaleY);
        theImage = new BufferedImage((int) (theComponent.getWidth() * theScale), (int) (theComponent.getHeight() * theScale), BufferedImage.TYPE_INT_RGB);

        Graphics2D g = theImage.createGraphics();
        g.scale(theScale, theScale);
        theComponent.paint(g);
        theStartRectangle = theComponent.getVisibleRect();
        Insets insets = getInsets();
        theStartRectangle.x = (int) (theScale * theStartRectangle.x + insets.left);
        theStartRectangle.y = (int) (theScale * theStartRectangle.y + insets.right);
        theStartRectangle.width *= theScale;
        theStartRectangle.height *= theScale;
        theRectangle = theStartRectangle;

        Dimension pref = thePopupMenu.getPreferredSize();
        Point buttonLocation = theButton.getLocationOnScreen();
        Point popupLocation = new Point((theButton.getWidth() - pref.width) / 2, (theButton.getHeight() - pref.height) / 2);
        Point centerPoint = new Point(buttonLocation.x + popupLocation.x + theRectangle.x + theRectangle.width / 2, buttonLocation.y + popupLocation.y + theRectangle.y + theRectangle.height / 2);
        try {
            // Attempt to move the mouse pointer to the center of the selector's rectangle.
            new Robot().mouseMove(centerPoint.x, centerPoint.y);
            theStartPoint = centerPoint;
        } catch (AWTException e) {
            // Since we cannot move the cursor, we'll move the popup instead.
            theStartPoint = aPointOnScreen;
            popupLocation.x += theStartPoint.x - centerPoint.x;
            popupLocation.y += theStartPoint.y - centerPoint.y;
        }
        thePopupMenu.show(theButton, popupLocation.x, popupLocation.y);
    }

    private void moveRectangle(int aDeltaX, int aDeltaY) {
        if (theStartRectangle == null) {
            return;
        }

        Insets insets = getInsets();
        Rectangle newRect = new Rectangle(theStartRectangle);
        newRect.x += aDeltaX;
        newRect.y += aDeltaY;
        newRect.x = Math.min(Math.max(newRect.x, insets.left), getWidth() - insets.right - newRect.width);
        newRect.y = Math.min(Math.max(newRect.y, insets.right), getHeight() - insets.bottom - newRect.height);
        Rectangle clip = new Rectangle();
        Rectangle2D.union(theRectangle, newRect, clip);
        clip.grow(2, 2);
        theRectangle = newRect;
        paintImmediately(clip);
    }

    private void scroll(int aDeltaX, int aDeltaY) {
        if (theComponent == null) {
            return;
        }
        Rectangle rect = theComponent.getVisibleRect();
        rect.x += aDeltaX;
        rect.y += aDeltaY;
        theComponent.scrollRectToVisible(rect);
        thePopupMenu.setVisible(false);
    }
}
