package com.adlitteram.jasmin.gui.layout;

import java.awt.*;

/**
 * A vertical layout manager similar to java.awt.FlowLayout. Like FlowLayout
 * components do not expand to fill available space except when the horizontal
 * alignment is <code>BOTH</code> in which case components are stretched
 * horizontally. Unlike FlowLayout, components will not wrap to form another
 * column if there isn't enough space vertically. VerticalLayout can optionally
 * anchor components to the top or bottom of the display area or center them
 * between the top and bottom.
 * <p>
 * Revision date 04 April 1999
 *
 * @author Colin Mummery e-mail:equitysoft@iname.com
 * Homepage:www.kagi.com/equitysoft - Based on 'FlexLayout' in Java class
 * libraries Vol 2 Chan/Lee Addison-Wesley 1998
 */
public class VerticalLayout implements LayoutManager {

    /**
     * The horizontal alignment constant that designates centering. Also used to
     * designate center anchoring.
     */
    public final static int CENTER = 0;

    /**
     * The horizontal alignment constant that designates right justification.
     */
    public final static int RIGHT = 1;

    /**
     * The horizontal alignment constant that designates left justification.
     */
    public final static int LEFT = 2;

    /**
     * The horizontal alignment constant that designates stretching the
     * component horizontally.
     */
    public final static int BOTH = 3;

    /**
     * The anchoring constant that designates anchoring to the top of the
     * display area
     */
    public final static int TOP = 1;

    /**
     * The anchoring constant that designates anchoring to the bottom of the
     * display area
     */
    public final static int BOTTOM = 2;

    private final int vgap; //the vertical vgap between components...defaults to 5
    private final int alignment; //LEFT, RIGHT, CENTER or BOTH...how the components are justified
    private final int anchor; //TOP, BOTTOM or CENTER ...where are the components positioned in an overlarge space

    //Constructors

    /**
     * Constructs an instance of VerticalLayout with a vertical vgap of 5
     * pixels, horizontal centering and anchored to the top of the display area.
     */
    public VerticalLayout() {
        this(0, LEFT, TOP);
    }

    /**
     * Constructs a VerticalLayout instance with horizontal centering, anchored
     * to the top with the specified vgap
     *
     * @param vgap An int value indicating the vertical seperation of the
     *             components
     */
    public VerticalLayout(int vgap) {
        this(vgap, LEFT, TOP);
    }

    /**
     * Constructs a VerticalLayout instance anchored to the top with the
     * specified vgap and horizontal alignment
     *
     * @param vgap      An int value indicating the vertical seperation of the
     *                  components
     * @param alignment An int value which is one of
     *                  <code>RIGHT, LEFT, CENTER, BOTH</code> for the horizontal alignment.
     */
    public VerticalLayout(int vgap, int alignment) {
        this(vgap, alignment, TOP);
    }

    /**
     * Constructs a VerticalLayout instance with the specified vgap, horizontal
     * alignment and anchoring
     *
     * @param vgap      An int value indicating the vertical seperation of the
     *                  components
     * @param alignment An int value which is one of
     *                  <code>RIGHT, LEFT, CENTER, BOTH</code> for the horizontal alignment.
     * @param anchor    An int value which is one of
     *                  <code>TOP, BOTTOM, CENTER</code> indicating where the components are to
     *                  appear if the display area exceeds the minimum necessary.
     */
    public VerticalLayout(int vgap, int alignment, int anchor) {
        this.vgap = vgap;
        this.alignment = alignment;
        this.anchor = anchor;
    }

    //----------------------------------------------------------------------------
    private Dimension layoutSize(Container parent, boolean minimum) {
        Dimension dim = new Dimension(0, 0);
        Dimension d;
        synchronized (parent.getTreeLock()) {
            int n = parent.getComponentCount();
            for (int i = 0; i < n; i++) {
                Component c = parent.getComponent(i);
                if (c.isVisible()) {
                    d = minimum ? c.getMinimumSize() : c.getPreferredSize();
                    dim.width = Math.max(dim.width, d.width);
                    dim.height += d.height;
                    if (i > 0) {
                        dim.height += vgap;
                    }
                }
            }
        }
        Insets insets = parent.getInsets();
        dim.width += insets.left + insets.right;
        dim.height += insets.top + insets.bottom + vgap + vgap;
        return dim;
    }

    /**
     * Lays out the container.
     */
    @Override
    public void layoutContainer(Container parent) {

        Insets insets = parent.getInsets();
        Dimension dim = layoutSize(parent, false);

        synchronized (parent.getTreeLock()) {
            int n = parent.getComponentCount();
            Dimension pd = parent.getSize();
            int y = 0;

            //work out the total size
            for (int i = 0; i < n; i++) {
                Component c = parent.getComponent(i);
                Dimension d = c.getPreferredSize();
                y += d.height + vgap;
            }
            y -= vgap; //otherwise there's a vgap too many

            //Work out the anchor paint
            switch (anchor) {
                case TOP:
                    y = insets.top;
                    break;
                case CENTER:
                    y = (pd.height - y) / 2;
                    break;
                default:
                    y = pd.height - y - insets.bottom;
                    break;
            }

            //do layout
            for (int i = 0; i < n; i++) {
                Component c = parent.getComponent(i);
                Dimension d = c.getPreferredSize();
                int x = insets.left;
                int wid = d.width;

                switch (alignment) {
                    case CENTER:
                        x = (pd.width - d.width) / 2;
                        break;
                    case RIGHT:
                        x = pd.width - d.width - insets.right;
                        break;
                    case BOTH:
                        wid = pd.width - insets.left - insets.right;
                        break;
                    default:
                        break;
                }
                c.setBounds(x, y, wid, d.height);
                y += d.height + vgap;
            }
        }
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return layoutSize(parent, false);
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        return layoutSize(parent, false);
    }

    /**
     * Not used by this class
     */
    @Override
    public void addLayoutComponent(String name, Component comp) {
    }

    /**
     * Not used by this class
     */
    @Override
    public void removeLayoutComponent(Component comp) {
    }

    @Override
    public String toString() {
        return getClass().getName() + "[vgap=" + vgap + " align=" + alignment + " anchor=" + anchor + "]";
    }
}
