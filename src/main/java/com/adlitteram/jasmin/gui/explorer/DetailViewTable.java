package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.utils.NumUtils;
import org.apache.commons.lang3.time.FastDateFormat;

import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableCellRenderer;
import javax.swing.table.TableColumnModel;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

class DetailViewTable extends JTable implements ExplorerView {

    private static final FastDateFormat DATE_FORMAT = FastDateFormat.getDateInstance(FastDateFormat.MEDIUM);
    private final ExplorerPane explorerPane;

    @Override
    public void refreshView() {
        getTableHeader().repaint();
    }

    @Override
    public int getLocationToIndex(Point point) {
        return rowAtPoint(point);
    }

    private static class CellRenderer extends DefaultTableCellRenderer {

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            super.getTableCellRendererComponent(table, value, isSelected, false, row, column);

            int modelCol = table.convertColumnIndexToModel(column);
            switch (modelCol) {
                case ExplorerPane.FORMAT_COLUMN, ExplorerPane.NAME_COLUMN:
                    setHorizontalAlignment(LEFT);
                    setText((String) value);
                    break;
                case ExplorerPane.LENGTH_COLUMN:
                    setHorizontalAlignment(RIGHT);
                    setText(NumUtils.toByteSize(NumUtils.longValue(value)));
                    break;
                case ExplorerPane.DIM_COLUMN:
                    setHorizontalAlignment(RIGHT);
                    setText((String) value);
                    break;
                case ExplorerPane.DATE_COLUMN:
                    setHorizontalAlignment(RIGHT);
                    setText(DATE_FORMAT.format(value));
                    break;
            }
            return this;
        }
    }

    public DetailViewTable(ExplorerPane explorerPane) {
        super();
        this.explorerPane = explorerPane;

        setDragEnabled(explorerPane.isDragEnabled());
        setModel(explorerPane.getModel().getTableModel());
        setSelectionModel(explorerPane.getSelectionModel());
        setDefaultRenderer(Object.class, new CellRenderer());
        setDragEnabled(explorerPane.isDragEnabled());

        getTableHeader().setDefaultRenderer(new SortableHeaderRenderer(getTableHeader().getDefaultRenderer()));
        getTableHeader().addMouseListener(new MouseHandler());

        getColumnModel().getColumn(ExplorerPane.FORMAT_COLUMN).setPreferredWidth(40);
        getColumnModel().getColumn(ExplorerPane.NAME_COLUMN).setPreferredWidth(200);
        getColumnModel().getColumn(ExplorerPane.DATE_COLUMN).setPreferredWidth(60);
        getColumnModel().getColumn(ExplorerPane.DIM_COLUMN).setPreferredWidth(60);
        getColumnModel().getColumn(ExplorerPane.LENGTH_COLUMN).setPreferredWidth(60);

        setFillsViewportHeight(true);
        setShowGrid(false);
    }

    @Override
    public ExplorerPane getExplorerPane() {
        return explorerPane;
    }

    public void setModel(ExplorerModel model) {
        setModel(model.getTableModel());
    }

    private class MouseHandler extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            JTableHeader h = (JTableHeader) e.getSource();
            TableColumnModel columnModel = h.getColumnModel();
            int viewColumn = columnModel.getColumnIndexAtX(e.getX());
            int column = columnModel.getColumn(viewColumn).getModelIndex();
            if (column != -1) {
                ColumnSort sort = explorerPane.getColumnSort(column);
                int direction = (sort == null || sort.isNotSorted()) ? ColumnSort.DESCENDING : sort.getInverseDirection();
                explorerPane.setColumnSort(column, direction);
            }
        }
    }

    private class SortableHeaderRenderer implements TableCellRenderer {

        private final TableCellRenderer tableCellRenderer;

        public SortableHeaderRenderer(TableCellRenderer tableCellRenderer) {
            this.tableCellRenderer = tableCellRenderer;
        }

        @Override
        public Component getTableCellRendererComponent(JTable table, Object value, boolean isSelected, boolean hasFocus, int row, int column) {
            Component c = tableCellRenderer.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
            if (c instanceof JLabel label) {
                label.setBackground(new Color(230, 230, 230));
                label.setHorizontalTextPosition(SwingConstants.LEFT);
                ColumnSort sort = explorerPane.getColumnSort(table.convertColumnIndexToModel(column));
                int size = label.getFont().getSize() / 2 + 1;
                label.setIcon(explorerPane.isPrimarySort(sort) ? new ArrowIcon(sort.isDescending(), size) : null);
            }
            return c;
        }
    }
}
