package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.utils.NumUtils;

import javax.swing.*;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class ExplorerPopup implements PopupMenuListener {

    private ExplorerPane explorerPane;
    private final JCheckBoxMenuItem nameSortItem;
    private final JCheckBoxMenuItem dateSortItem;
    private final JCheckBoxMenuItem dimSortItem;
    private final JCheckBoxMenuItem lengthSortItem;
    private final JCheckBoxMenuItem smallIconItem;
    private final JCheckBoxMenuItem mediumIconItem;
    private final JCheckBoxMenuItem largeIconItem;
    private final JCheckBoxMenuItem noInfoItem;
    private final JCheckBoxMenuItem briefInfoItem;
    private final JCheckBoxMenuItem fullInfoItem;
    private final JCheckBoxMenuItem detailViewItem;
    private final JCheckBoxMenuItem iconViewItem;
    private final JMenuItem fullScreenItem;

    private final JMenu sortMenu;
    private final JMenu iconMenu;
    private final JMenu infoMenu;
    private final JMenu viewMenu;

    public ExplorerPopup(ExplorerPane explorerPane) {
        this.explorerPane = explorerPane;

        nameSortItem = new JCheckBoxMenuItem(Message.get("SortByName"));
        nameSortItem.setActionCommand(String.valueOf(ExplorerPane.NAME_COLUMN));
        ActionListener sortListener = e -> {
            int column = NumUtils.intValue(e.getActionCommand());
            explorerPane.setColumnSort(column, 1);
        };
        nameSortItem.addActionListener(sortListener);

        dateSortItem = new JCheckBoxMenuItem(Message.get("SortByDate"));
        dateSortItem.setActionCommand(String.valueOf(ExplorerPane.DATE_COLUMN));
        dateSortItem.addActionListener(sortListener);

        lengthSortItem = new JCheckBoxMenuItem(Message.get("SortByLength"));
        lengthSortItem.setActionCommand(String.valueOf(ExplorerPane.LENGTH_COLUMN));
        lengthSortItem.addActionListener(sortListener);

        dimSortItem = new JCheckBoxMenuItem(Message.get("SortByDimension"));
        dimSortItem.setActionCommand(String.valueOf(ExplorerPane.DIM_COLUMN));
        dimSortItem.addActionListener(sortListener);

        JCheckBoxMenuItem formatSortItem = new JCheckBoxMenuItem(Message.get("SortByFormat"));
        formatSortItem.setActionCommand(String.valueOf(ExplorerPane.FORMAT_COLUMN));
        formatSortItem.addActionListener(sortListener);

        smallIconItem = new JCheckBoxMenuItem(Message.get("SmallIcon"));
        smallIconItem.setActionCommand(String.valueOf(ExplorerPane.SMALL_ICON));
        ActionListener iconSizeListener = e -> {
            int size = NumUtils.intValue(e.getActionCommand());
            explorerPane.setIconSize(size);
        };
        smallIconItem.addActionListener(iconSizeListener);

        mediumIconItem = new JCheckBoxMenuItem(Message.get("MediumIcon"));
        mediumIconItem.setActionCommand(String.valueOf(ExplorerPane.MEDIUM_ICON));
        mediumIconItem.addActionListener(iconSizeListener);

        largeIconItem = new JCheckBoxMenuItem(Message.get("LargeIcon"));
        largeIconItem.setActionCommand(String.valueOf(ExplorerPane.LARGE_ICON));
        largeIconItem.addActionListener(iconSizeListener);

        noInfoItem = new JCheckBoxMenuItem(Message.get("NoInfo"));
        noInfoItem.setActionCommand(String.valueOf(ExplorerPane.NO_INFO));
        ActionListener infoDetailListener = e -> {
            int info = NumUtils.intValue(e.getActionCommand());
            explorerPane.setInfoDetail(info);
        };
        noInfoItem.addActionListener(infoDetailListener);

        briefInfoItem = new JCheckBoxMenuItem(Message.get("BriefInfo"));
        briefInfoItem.setActionCommand(String.valueOf(ExplorerPane.BRIEF_INFO));
        briefInfoItem.addActionListener(infoDetailListener);

        fullInfoItem = new JCheckBoxMenuItem(Message.get("FullInfo"));
        fullInfoItem.setActionCommand(String.valueOf(ExplorerPane.FULL_INFO));
        fullInfoItem.addActionListener(infoDetailListener);

        detailViewItem = new JCheckBoxMenuItem(Message.get("DetailMode"));
        detailViewItem.setActionCommand("DetailMode");
        ActionListener viewModeListener = e -> {
            String mode = e.getActionCommand();
            explorerPane.setViewMode("DetailMode".equals(mode) ? ExplorerPane.ViewMode.Detail : ExplorerPane.ViewMode.Icon);
        };
        detailViewItem.addActionListener(viewModeListener);

        iconViewItem = new JCheckBoxMenuItem(Message.get("IconMode"));
        iconViewItem.setActionCommand("IconMode");
        iconViewItem.addActionListener(viewModeListener);

        ButtonGroup group1 = new ButtonGroup();
        group1.add(nameSortItem);
        group1.add(dateSortItem);
        group1.add(dimSortItem);
        group1.add(lengthSortItem);
        group1.add(formatSortItem);

        ButtonGroup group2 = new ButtonGroup();
        group2.add(smallIconItem);
        group2.add(mediumIconItem);
        group2.add(largeIconItem);

        ButtonGroup group3 = new ButtonGroup();
        group3.add(noInfoItem);
        group3.add(briefInfoItem);
        group3.add(fullInfoItem);

        ButtonGroup group4 = new ButtonGroup();
        group4.add(detailViewItem);
        group4.add(iconViewItem);

        fullScreenItem = new JMenuItem(Message.get("FullScreen"));
        ActionListener fullScreenListener = e -> {
            Action action = explorerPane.getLeftMouse2ClickAction();
            if (action != null) {
                action.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "LeftMouse2Click"));
            } else {
                explorerPane.showFullScreenPane();
            }
        };
        fullScreenItem.addActionListener(fullScreenListener);

        sortMenu = new JMenu(Message.get("ImageSort"));
        sortMenu.add(nameSortItem);
        sortMenu.add(dateSortItem);
        sortMenu.add(lengthSortItem);
        sortMenu.add(dimSortItem);
        sortMenu.add(formatSortItem);

        iconMenu = new JMenu(Message.get("IconSize"));
        iconMenu.add(smallIconItem);
        iconMenu.add(mediumIconItem);
        iconMenu.add(largeIconItem);

        infoMenu = new JMenu(Message.get("InfoDetail"));
        infoMenu.add(noInfoItem);
        infoMenu.add(briefInfoItem);
        infoMenu.add(fullInfoItem);

        viewMenu = new JMenu(Message.get("ViewMode"));
        viewMenu.add(detailViewItem);
        viewMenu.add(iconViewItem);
    }

    public JPopupMenu createPopupMenu() {
        return addToPopupMenu(null);
    }

    public JPopupMenu addToPopupMenu(JPopupMenu popupMenu) {
        if (popupMenu == null) {
            popupMenu = new JPopupMenu();
        }

        popupMenu.add(fullScreenItem);
        popupMenu.add(sortMenu);
        popupMenu.add(infoMenu);
        popupMenu.add(iconMenu);
        popupMenu.add(viewMenu);

        popupMenu.addPopupMenuListener(this);
        return popupMenu;
    }

    @Override
    public void popupMenuWillBecomeVisible(PopupMenuEvent e) {
        ExplorerPane.ViewMode viewMode = explorerPane.getViewMode();
        iconViewItem.setSelected(ExplorerPane.ViewMode.Icon == viewMode);
        detailViewItem.setSelected(ExplorerPane.ViewMode.Detail == viewMode);

        ColumnSort cs = explorerPane.getPrimarySort();
        int primaryColumn = (cs == null) ? -1 : cs.getColumn();
        nameSortItem.setSelected(ExplorerPane.NAME_COLUMN == primaryColumn);
        dateSortItem.setSelected(ExplorerPane.DATE_COLUMN == primaryColumn);
        dimSortItem.setSelected(ExplorerPane.DIM_COLUMN == primaryColumn);
        lengthSortItem.setSelected(ExplorerPane.LENGTH_COLUMN == primaryColumn);

        int infoDetail = explorerPane.getInfoDetail();
        noInfoItem.setSelected(ExplorerPane.NO_INFO == infoDetail);
        briefInfoItem.setSelected(ExplorerPane.BRIEF_INFO == infoDetail);
        fullInfoItem.setSelected(ExplorerPane.FULL_INFO == infoDetail);
        infoMenu.setVisible(viewMode == ExplorerPane.ViewMode.Icon);

        int iconSize = explorerPane.getIconSize();
        smallIconItem.setSelected(ExplorerPane.SMALL_ICON == iconSize);
        mediumIconItem.setSelected(ExplorerPane.MEDIUM_ICON == iconSize);
        largeIconItem.setSelected(ExplorerPane.LARGE_ICON == iconSize);
        iconMenu.setVisible(viewMode == ExplorerPane.ViewMode.Icon);

    }

    @Override
    public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
        // Do nothing
    }

    @Override
    public void popupMenuCanceled(PopupMenuEvent e) {
        // Do nothing
    }

    public JCheckBoxMenuItem getBriefInfoItem() {
        return briefInfoItem;
    }

    public JCheckBoxMenuItem getDateSortItem() {
        return dateSortItem;
    }

    public JCheckBoxMenuItem getDetailViewItem() {
        return detailViewItem;
    }

    public JCheckBoxMenuItem getFullInfoItem() {
        return fullInfoItem;
    }

    public JMenuItem getFullScreenItem() {
        return fullScreenItem;
    }

    public JMenu getIconMenu() {
        return iconMenu;
    }

    public JCheckBoxMenuItem getIconViewItem() {
        return iconViewItem;
    }

    public JMenu getInfoMenu() {
        return infoMenu;
    }

    public JCheckBoxMenuItem getLargeIconItem() {
        return largeIconItem;
    }

    public JCheckBoxMenuItem getLengthSortItem() {
        return lengthSortItem;
    }

    public JCheckBoxMenuItem getMediumIconItem() {
        return mediumIconItem;
    }

    public JCheckBoxMenuItem getNameSortItem() {
        return nameSortItem;
    }

    public JCheckBoxMenuItem getNoInfoItem() {
        return noInfoItem;
    }

    public JCheckBoxMenuItem getDimensionSortItem() {
        return dimSortItem;
    }

    public JCheckBoxMenuItem getSmallIconItem() {
        return smallIconItem;
    }

    public JMenu getSortMenu() {
        return sortMenu;
    }

    public JMenu getViewMenu() {
        return viewMenu;
    }
}
