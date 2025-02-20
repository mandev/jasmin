package com.adlitteram.jasmin.gui.explorer;

import javax.swing.*;
import java.awt.*;

public class IconViewList extends JList<ImageFile> implements ExplorerView {

    private final ExplorerPane explorerPane;
    private final IconViewListener iconViewListener;
    private int overIndex;
    private int cellGap = 5;

    public IconViewList(ExplorerPane explorerPane) {
        super();
        this.explorerPane = explorerPane;

        setModel(explorerPane.getModel().getListModel());
        setSelectionModel(explorerPane.getSelectionModel());
        setCellRenderer(new IconViewRenderer());
        setBackground(Color.WHITE);
        setDragEnabled(true);
        setDropMode(DropMode.ON);

        iconViewListener = new IconViewListener();
        addMouseListener(iconViewListener);
        addMouseMotionListener(iconViewListener);
    }

    @Override
    public ExplorerPane getExplorerPane() {
        return explorerPane;
    }

    protected int getOverIndex() {
        return overIndex;
    }

    protected void setOverIndex(int overIndex) {
        this.overIndex = overIndex;
    }

    protected int getIconGap() {
        return cellGap;
    }

    protected void setIconGap(int cellGap) {
        this.cellGap = cellGap;
        refreshView();
    }

    protected void setModel(ExplorerModel model) {
        setModel(model.getListModel());
    }

    @Override
    public void refreshView() {
        int size = explorerPane.getIconSize() + 2 * cellGap + 12;
        int info = explorerPane.getInfoDetail();
        switch (info) {
            case ExplorerPane.NO_INFO:
                setFixedCellHeight(size);
                setFixedCellWidth(size);
                break;
            case ExplorerPane.BRIEF_INFO:
                setFixedCellHeight(size + getFontMetrics(getFont()).getHeight());
                setFixedCellWidth(size);
                break;
            default:
                setFixedCellHeight(size);
                setFixedCellWidth(size * 3 / 2 + 120);
                break;
        }
    }

    @Override
    public int getLocationToIndex(Point point) {
        return locationToIndex(point);
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        iconViewListener.paint(g);
    }
}
