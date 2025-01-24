package com.adlitteram.jasmin.gui.combo;

import javax.swing.*;
import javax.swing.plaf.basic.BasicComboPopup;
import java.awt.*;

public class JExtComboBox<E> extends JComboBox<E> {

    private int colCount = 1;
    private Component lastComponent;

    public JExtComboBox() {
        super();
        setLightWeightPopupEnabled(true);
    }

    public JExtComboBox(E[] objs) {
        super(objs);
        setLightWeightPopupEnabled(true);
    }

    public JExtComboBox(ComboBoxModel<E> model) {
        super(model);
        setLightWeightPopupEnabled(true);
    }

    @Override
    public void updateUI() {
        super.updateUI();
        resizePopup();
    }

    @Override
    public void firePopupMenuWillBecomeVisible() {
        resizePopup();
        super.firePopupMenuWillBecomeVisible();
    }

    private void resizePopup() {
        BasicComboPopup popup = (BasicComboPopup) getUI().getAccessibleChild(this, 0);
        if (popup == null) {
            return;
        }

        JList list = popup.getList();
        list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
        int rowCount = (int) Math.ceil(getItemCount() / (double) colCount);
        list.setVisibleRowCount(rowCount);

        int width = list.getPreferredSize().width;
        int height = list.getPreferredSize().height;

        int popupHeight = getPopupHeight(popup, getMaximumRowCount());
        if (popupHeight < list.getPreferredSize().height) {
            height = popupHeight;
            width = Math.max(popup.getPreferredSize().width, width + 18);
        }

        JComponent comp = (JComponent) popup.getComponent(0);

        popup.removeAll();
        popup.setLayout(new BorderLayout());
        popup.add(comp, BorderLayout.CENTER);
        if (lastComponent != null) {
            popup.add(lastComponent, BorderLayout.SOUTH);
        }
        popup.setPreferredSize(new Dimension(width, height));
    }

    private int getPopupHeight(BasicComboPopup popup, int rowCount) {
        JList list = popup.getList();
        rowCount = Math.min(rowCount, list.getModel().getSize());
        ListCellRenderer<? super E> cellRenderer = list.getCellRenderer();

        int height = 0;
        for (int i = 0; i < rowCount; ++i) {
            E element = (E) list.getModel().getElementAt(i);
            Component c = cellRenderer.getListCellRendererComponent(list, element, i, false, false);
            height += c.getPreferredSize().height;
        }
        return height == 0 ? 100 : height;
    }

    public int getColumnCount() {
        return colCount;
    }

    public void setColumnCount(int colCount) {
        this.colCount = colCount;
    }

    public Component getLastComponent() {
        return lastComponent;
    }

    public void setLastComponent(Component lastComponent) {
        this.lastComponent = lastComponent;
    }
}
