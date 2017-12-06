/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.adlitteram.jasmin.gui.explorer;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import javax.swing.ListModel;
import javax.swing.event.MouseInputAdapter;

public class IconViewListener extends MouseInputAdapter {
    private Rectangle rectangle;
    private Point srcPoint;
    private Color rcolor;
    private Color pcolor;
    private AlphaComposite composite;
    private int[] selectedIndices;

    public IconViewListener() {
        rectangle = new Rectangle();
        rcolor = SystemColor.activeCaption;
        pcolor = makeColor(rcolor);
        composite = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.1f);
    }

    private Color makeColor(Color c) {
        int r = c.getRed();
        int g = c.getGreen();
        int b = c.getBlue();
        return (r > g) ? (r > b) ? new Color(r, 0, 0) : new Color(0, 0, b) : (g > b) ? new Color(0, g, 0) : new Color(0, 0, b);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        IconViewList list = (IconViewList) e.getSource();
        int index = list.locationToIndex(e.getPoint());
        Rectangle r = list.getCellBounds(index, index);
        if (r != null) {
            r = getInnerBounds(r, list.getIconGap());
            if (r.contains(e.getPoint())) {
                list.setDragEnabled(list.getExplorerPane().isDragEnabled());
            }
            else {
                if (list.getExplorerPane().isRubberBandEnabled()) {
                    if (e.isControlDown()) {
                        selectedIndices = list.getSelectedIndices();
                    }
                    else {
                        list.clearSelection();
                        selectedIndices = null;
                    }
                    list.setDragEnabled(false);
                }
            }
        }
        else {
            list.setDragEnabled(false);
        }
        list.repaint();
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        IconViewList list = (IconViewList) e.getSource();
        if (list.getExplorerPane().isRubberBandEnabled() && !list.getDragEnabled()) {
            if (srcPoint == null) srcPoint = e.getPoint();
            rectangle.setFrameFromDiagonal(srcPoint, e.getPoint());
            list.setSelectedIndices(getIntersectsIcons(list, rectangle));
            list.repaint();
        }
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        IconViewList list = (IconViewList) e.getSource();
        //list.setDragEnabled(list.getExplorerPane().isDragEnabled());
        srcPoint = null;
        list.repaint();
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        IconViewList list = (IconViewList) e.getSource();
        int index = list.locationToIndex(e.getPoint());
        Rectangle r = list.getCellBounds(index, index);
        if (r != null) {
            Rectangle rect = getInnerBounds(r, list.getIconGap());
            list.setOverIndex(rect.contains(e.getPoint()) ? index : -1);
        }
        else {
            list.setOverIndex(-1);
        }

        list.repaint();
    }

    @Override
    public void mouseExited(MouseEvent e) {
        IconViewList list = (IconViewList) e.getSource();
        list.setOverIndex(-1);
        list.repaint();
    }

    private Rectangle getInnerBounds(Rectangle r, int s) {
        return new Rectangle(r.x + s, r.y + s, r.width - s * 2, r.height - s * 2);
    }

    private int[] getIntersectsIcons(IconViewList list, Rectangle p) {
        ListModel model = list.getModel();
        ArrayList<Integer> selectedCells = new ArrayList<Integer>(model.getSize());

        for (int i = 0; i < model.getSize(); i++) {
            Rectangle r = getInnerBounds(list.getCellBounds(i, i), list.getIconGap());
            if (p.intersects(r)) selectedCells.add(i);
        }

        if (selectedIndices != null) {
            for (int j = 0; j < selectedIndices.length; j++) {
                int val = selectedIndices[j];
                int idx = selectedCells.indexOf(val);
                if (idx >= 0) selectedCells.remove(idx);
                else selectedCells.add(val);
            }
        }

        int[] il = new int[selectedCells.size()];
        for (int i = 0; i < selectedCells.size(); i++) {
            il[i] = selectedCells.get(i);
        }
        return il;
    }

    public void paint(Graphics g) {
        if (srcPoint != null) {
            Graphics2D g2d = (Graphics2D) g;
            g2d.setPaint(rcolor);
            g2d.drawRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
            g2d.setComposite(composite);
            g2d.setPaint(pcolor);
            g2d.fillRect(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        }
    }
}
