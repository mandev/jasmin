package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.jasmin.Message;
import java.util.ArrayList;
import javax.swing.table.AbstractTableModel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class FileTableModel extends AbstractTableModel {

    private static final Logger LOGGER = LoggerFactory.getLogger(FileTableModel.class);

    private static final String[] COL_NAMES = {
        Message.get("Format"),
        Message.get("Name"),
        Message.get("Size"),
        Message.get("w x h"),
        Message.get("Date"),};

    private final ExplorerModel explorerModel;
    private final ArrayList<ImageFile> imageFileList;
    private ImageFile previousImageFile;

    public FileTableModel(ExplorerModel explorerModel, ArrayList<ImageFile> imageFileList) {
        this.explorerModel = explorerModel;
        this.imageFileList = imageFileList;
    }

    @Override
    public int getColumnCount() {
        return COL_NAMES.length;
    }

    @Override
    public int getRowCount() {
        return imageFileList.size();
    }

    @Override
    public String getColumnName(int col) {
        return COL_NAMES[col];
    }

    private ImageFile getImageFile(int row) {
        ImageFile imageFile = imageFileList.get(row);
        if (imageFile != previousImageFile) {
            previousImageFile = imageFile;
            if (!imageFile.isCompleted()) {
                imageFile.update(explorerModel, row);
            }
        }
        return imageFile;
    }

    @Override
    public Object getValueAt(int row, int col) {
        ImageFile imageFile = getImageFile(row);

        switch (col) {
            case 0:
                String fmt = imageFile.getFormat();
                return (fmt == null) ? "" : fmt.toUpperCase();
            case 1:
                return imageFile.getName();
            case 2:
                return imageFile.getLength();
            case 3:
                int width = imageFile.getWidth();
                int height = imageFile.getHeight();
                return (width == 0 || height == 0) ? "" : width + "x" + height;
            case 4:
                return imageFile.firstCreated();
        }
        return null;
    }

    @Override
    public Class getColumnClass(int c) {
        return String.class;
    }

    @Override
    public boolean isCellEditable(int row, int col) {
        return false;
    }

    @Override
    public void setValueAt(Object value, int row, int col) {
    }
}
