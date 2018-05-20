package com.adlitteram.jasmin.gui.explorer;

import com.adlitteram.imagetool.ImageInfo;
import com.adlitteram.imagetool.Imager;
import java.awt.Color;
import java.awt.Component;
import java.awt.Point;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import javax.swing.Action;
import javax.swing.DefaultListSelectionModel;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.JPopupMenu;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;
import javax.swing.TransferHandler;
import javax.swing.event.ListSelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ExplorerPane extends JScrollPane {

    private static final Logger LOGGER = LoggerFactory.getLogger(ExplorerPane.class);
    //
    public static final int ONE_COL_LAYOUT = 0;
    public static final int DEFAULT_LAYOUT = 1;
    public static final int ONE_ROW_LAYOUT = 2;
    //
    public static final int FORMAT_COLUMN = 0;
    public static final int NAME_COLUMN = 1;
    public static final int LENGTH_COLUMN = 2;
    public static final int DIM_COLUMN = 3;
    public static final int DATE_COLUMN = 4;

    public static final int SMALL_ICON = 64;
    public static final int MEDIUM_ICON = 96;
    public static final int LARGE_ICON = 128;

    public static final int NO_INFO = 0;
    public static final int BRIEF_INFO = 1;
    public static final int FULL_INFO = 2;

    public static enum ViewMode {

        Detail, Icon
    };

    private ViewMode viewMode;
    private ExplorerModel explorerModel;
    private ExplorerView currentView;
    private DetailViewTable detailView;
    private IconViewList iconView;
    private JPopupMenu popupMenu;
    private ArrayList<ColumnSort> columnSortList;
    private ListSelectionModel selectionModel;
    private ImageFileCheckable imageFileCheckable;
    private Action leftMouse2ClickAction;
    //
    private int infoDetail = ExplorerPane.NO_INFO;
    private boolean fullScreenOnClick = false;
    private boolean rubberBandEnabled = true;
    private boolean dragEnabled = true;
    private int viewLayout = DEFAULT_LAYOUT;

    public ExplorerPane() {
        this(new ExplorerModel());
    }

    public ExplorerPane(ExplorerModel explorerModel) {
        this.explorerModel = explorerModel;

        columnSortList = new ArrayList<>();
        selectionModel = new DefaultListSelectionModel();
        selectionModel.setSelectionMode(ListSelectionModel.MULTIPLE_INTERVAL_SELECTION);

        iconView = new IconViewList(this);
        detailView = new DetailViewTable(this);
        viewMode = ViewMode.Icon;
        currentView = iconView;
        currentView.refreshView();

        initMouseListener();
        setBackground(Color.WHITE);
        setViewportView((Component) currentView);
        doViewLayout();
    }

    public void setViewLayout(int displayLayout) {
        this.viewLayout = displayLayout;
        doViewLayout();
    }

    public int getViewLayout() {
        return viewLayout;
    }

    private void initMouseListener() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    if (leftMouse2ClickAction != null) {
                        leftMouse2ClickAction.actionPerformed(new ActionEvent(e.getSource(), e.getID(), "LeftMouse2Click"));
                    }
                    else if (fullScreenOnClick) {
                        showFullScreenPane();
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                int index = getLocationToIndex(e.getPoint());
                ListSelectionModel sm = ExplorerPane.this.getSelectionModel();
                if (!sm.isSelectedIndex(index)) {
                    sm.setSelectionInterval(index, index);
                }
                if (getPopupMenu() != null && SwingUtilities.isRightMouseButton(e)) {
                    getPopupMenu().show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });
    }

    public void showFullScreenPane() {
        File file = ExplorerPane.this.getSelectedFile();
        File[] files = ExplorerPane.this.getSelectedFiles();
        Window window = SwingUtilities.getWindowAncestor(ExplorerPane.this);
        FullScreenPane fsp = new FullScreenPane(window, files, file);
    }

    public Action getLeftMouse2ClickAction() {
        return leftMouse2ClickAction;
    }

    public void setLeftMouse2ClickAction(Action leftClickAction) {
        this.leftMouse2ClickAction = leftClickAction;
    }

    public void setDefaultImageFileCheckable() {
        if (!(imageFileCheckable instanceof FileCheck)) {
            setImageFileCheckable(new FileCheck(this));
        }
    }

    public ImageFileCheckable getImageFileCheckable() {
        return imageFileCheckable;
    }

    public void setImageFileCheckable(ImageFileCheckable imageFileCheckable) {
        this.imageFileCheckable = imageFileCheckable;
    }

    public boolean checkImageFile(ImageFile imageFile) {
        return (imageFileCheckable != null) ? imageFileCheckable.check(imageFile) : false;
    }

    public boolean isDragEnabled() {
        return dragEnabled;
    }

    public void setDragEnabled(boolean dragEnabled) {
        this.dragEnabled = dragEnabled;
        detailView.setDragEnabled(dragEnabled);
    }

    public boolean isRubberBandEnabled() {
        return rubberBandEnabled;
    }

    public void setRubberBandEnabled(boolean rubberBandEnabled) {
        this.rubberBandEnabled = rubberBandEnabled;
    }

    public boolean isFullScreenEnabled() {
        return fullScreenOnClick;
    }

    public void setFullScreenEnabled(boolean fullScreenOnClick) {
        this.fullScreenOnClick = fullScreenOnClick;
    }

    public int getInfoDetail() {
        return infoDetail;
    }

    public void setInfoDetail(int infoDetail) {
        this.infoDetail = infoDetail;
        currentView.refreshView();
    }

    public int getIconSize() {
        return explorerModel.getIconSize();
    }

    public void setIconSize(int iconSize) {
        explorerModel.setIconSize(iconSize);
        currentView.refreshView();
    }

    public int getIconGap() {
        return iconView.getIconGap();
    }

    public void setIconGap(int cellGap) {
        iconView.setIconGap(cellGap);
    }

    public boolean isIconViewMode() {
        return getViewMode() == ViewMode.Icon;
    }

    public boolean isDetailViewMode() {
        return getViewMode() == ViewMode.Detail;
    }

    public void setIconViewMode() {
        setViewMode(ViewMode.Icon);
    }

    public void setDetailViewMode() {
        setViewMode(ViewMode.Detail);
    }

    public ViewMode getViewMode() {
        return viewMode;
    }

    public void setViewMode(ViewMode viewMode) {
        ViewMode old = getViewMode();
        this.viewMode = viewMode;
        currentView = (viewMode == ViewMode.Detail) ? detailView : iconView;
        currentView.refreshView();

        setViewportView((Component) currentView);
        doViewLayout();
        ((JComponent) currentView).requestFocusInWindow();

        firePropertyChange("mode", old, viewMode);
    }

    private void doViewLayout() {
        if (viewMode == ViewMode.Detail) {
            setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
            setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        }
        else {
            if (viewLayout == ONE_COL_LAYOUT) {
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                iconView.setLayoutOrientation(JList.VERTICAL);
                iconView.setVisibleRowCount(-1);
            }
            else if (viewLayout == DEFAULT_LAYOUT) {
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
                iconView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                iconView.setVisibleRowCount(-1);
            }
            else if (viewLayout == ONE_ROW_LAYOUT) {
                setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_NEVER);
                iconView.setLayoutOrientation(JList.HORIZONTAL_WRAP);
                iconView.setVisibleRowCount(1);
                //getHorizontalScrollBar().setUnitIncrement(16);
            }
        }
    }

    public ExplorerModel getModel() {
        return explorerModel;
    }

    public void setModel(ExplorerModel model) {
        ExplorerModel old = this.explorerModel;
        this.explorerModel = model;
        firePropertyChange("model", old, model);
        if (model != old) {
            detailView.setModel(model);
            iconView.setModel(model);
            selectionModel.clearSelection();
        }
    }

    public void setSelectionMode(int selectionMode) {
        selectionModel.setSelectionMode(selectionMode);
    }

    public int getSelectionMode() {
        return selectionModel.getSelectionMode();
    }

    public int getLocationToIndex(Point point) {
        return currentView.getLocationToIndex(point);
    }

    public ListSelectionModel getSelectionModel() {
        return selectionModel;
    }

    public void setSelectionModel(ListSelectionModel selectionModel) {
        this.selectionModel = selectionModel;
    }

    public int[] getSelectedIndices() {
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();

        if ((iMin < 0) || (iMax < 0)) {
            return new int[0];
        }

        int[] rvTmp = new int[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (selectionModel.isSelectedIndex(i)) {
                rvTmp[n++] = i;
            }
        }
        int[] rv = new int[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    public void setSelectedIndices(int[] indices) {
        selectionModel.clearSelection();
        int size = explorerModel.size();
        for (int i = 0; i < indices.length; i++) {
            if (indices[i] < size) {
                selectionModel.addSelectionInterval(indices[i], indices[i]);
            }
        }
    }

    public void setSelectedFile(File file) {
        selectionModel.clearSelection();
        int size = explorerModel.size();
        for (int i = 0; i < size; i++) {
            if (file.equals(explorerModel.get(i))) {
                selectionModel.addSelectionInterval(i, i);
                return;
            }
        }
    }

    public void setSelectedImageFile(ImageFile imageFile) {
        selectionModel.clearSelection();
        int size = explorerModel.size();
        for (int i = 0; i < size; i++) {
            if (imageFile.equals(explorerModel.getImageFile(i))) {
                selectionModel.addSelectionInterval(i, i);
                return;
            }
        }
    }

    public File getSelectedFile() {
        int i = selectionModel.getMinSelectionIndex();
        return (i == -1) ? null : explorerModel.get(i);
    }

    public ImageFile getSelectedImageFile() {
        int i = selectionModel.getMinSelectionIndex();
        return (i == -1) ? null : explorerModel.getImageFile(i);
    }

    public File[] getSelectedFiles() {
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();
        if ((iMin < 0) || (iMax < 0)) {
            return new File[0];
        }
        File[] rvTmp = new File[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (selectionModel.isSelectedIndex(i)) {
                rvTmp[n++] = explorerModel.get(i);
            }
        }
        File[] rv = new File[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    public ImageFile[] getSelectedImageFiles() {
        int iMin = selectionModel.getMinSelectionIndex();
        int iMax = selectionModel.getMaxSelectionIndex();
        if ((iMin < 0) || (iMax < 0)) {
            return new ImageFile[0];
        }
        ImageFile[] rvTmp = new ImageFile[1 + (iMax - iMin)];
        int n = 0;
        for (int i = iMin; i <= iMax; i++) {
            if (selectionModel.isSelectedIndex(i)) {
                rvTmp[n++] = explorerModel.getImageFile(i);
            }
        }
        ImageFile[] rv = new ImageFile[n];
        System.arraycopy(rvTmp, 0, rv, 0, n);
        return rv;
    }

    public int getMinSelectionIndex() {
        return getSelectionModel().getMinSelectionIndex();
    }

    public int getMaxSelectionIndex() {
        return getSelectionModel().getMaxSelectionIndex();
    }

    // Sort
    public ColumnSort getPrimarySort() {
        return columnSortList.isEmpty() ? null : columnSortList.get(columnSortList.size() - 1);
    }

    public boolean isPrimarySort(ColumnSort sort) {
        if (columnSortList.isEmpty()) {
            return false;
        }
        return (sort == null) ? false : columnSortList.indexOf(sort) == columnSortList.size() - 1;
    }

    public void setColumnSort(ColumnSort cs) {
        setColumnSort(cs.getColumn(), cs.getDirection());
    }

    public void setColumnSort(int column, int status) {
        if (isSortAllowed(column)) {
            ColumnSort s = getColumnSort(column);
            if (s != null) {
                columnSortList.remove(s);
            }
            if (status != ColumnSort.NOT_SORTED) {
                columnSortList.add(new ColumnSort(column, status));
            }

            ArrayList<Comparator<ImageFile>> comparatorList = new ArrayList<>();
            for (ColumnSort sort : columnSortList) {
                Comparator<ImageFile> c = getComparator(sort);
                if (c != null) {
                    comparatorList.add(c);
                }
            }
            sort(comparatorList);
        }
    }

    public ColumnSort getColumnSort(int column) {
        for (int i = 0; i < columnSortList.size(); i++) {
            ColumnSort cs = columnSortList.get(i);
            if (cs.getColumn() == column) {
                return cs;
            }
        }
        return null;
    }

    public boolean isSortAllowed(int column) {
        switch (column) {
            case FORMAT_COLUMN:
            case NAME_COLUMN:
            case LENGTH_COLUMN:
            case DATE_COLUMN:
            case DIM_COLUMN:
                return true;
        }
        return false;
    }

    public void sort(ArrayList<Comparator<ImageFile>> comparators) {
        // Keep selected values in the list
        File[] files = getSelectedFiles();
        int[] indices = new int[files.length];

        explorerModel.sort(comparators);

        for (int j = 0; j < files.length; j++) {
            for (int i = 0; i < explorerModel.size(); i++) {
                if (files[j] == explorerModel.get(i)) {
                    indices[j] = i;
                    break;
                }
            }
        }

        setSelectedIndices(indices);
        currentView.refreshView();
    }

    public Comparator<ImageFile> getComparator(ColumnSort sort) {
        switch (sort.getColumn()) {
            case FORMAT_COLUMN:
                return new FormatComparator(sort.isDescending());
            case NAME_COLUMN:
                return new NameComparator(sort.isDescending());
            case LENGTH_COLUMN:
                return new LengthComparator(sort.isDescending());
            case DATE_COLUMN:
                return new DateComparator(sort.isDescending());
            case DIM_COLUMN:
                return new DimensionComparator(sort.isDescending());
        }
        return null;
    }

    // Drag & Drop
    @Override
    public void setTransferHandler(TransferHandler th) {
        detailView.setTransferHandler(th);
        iconView.setTransferHandler(th);
    }

    @Override
    public TransferHandler getTransferHandler() {
        return detailView.getTransferHandler();
    }

    // Listeners
    public void addListSelectionListener(ListSelectionListener selectionListener) {
        iconView.addListSelectionListener(selectionListener);
        detailView.getSelectionModel().addListSelectionListener(selectionListener);
    }

    public void removeListSelectionListener(ListSelectionListener selectionListener) {
        iconView.removeListSelectionListener(selectionListener);
        detailView.getSelectionModel().removeListSelectionListener(selectionListener);
    }

    @Override
    public void addMouseListener(MouseListener mouseListener) {
        detailView.addMouseListener(mouseListener);
        iconView.addMouseListener(mouseListener);
    }

    @Override
    public void removeMouseListener(MouseListener mouseListener) {
        detailView.removeMouseListener(mouseListener);
        iconView.removeMouseListener(mouseListener);
    }

    @Override
    public void addKeyListener(KeyListener keyListener) {
        detailView.addKeyListener(keyListener);
        iconView.addKeyListener(keyListener);
    }

    @Override
    public void removeKeyListener(KeyListener keyListener) {
        detailView.removeKeyListener(keyListener);
        iconView.removeKeyListener(keyListener);
    }

//    @Override
//    public Dimension getPreferredSize() {
//        return currentView == null ? super.getPreferredSize() : ((Container) currentView).getPreferredSize();
//    }
    // Utils
    public void setImagesFromDir(final File dir) {
        setModel(new ExplorerModel(getModel().getIconSize()));
        addImagesFromDir(dir);
    }

    public void addImagesFromDir(final File dir) {
        final ExplorerModel model = getModel();
        new SwingWorker<List<ImageFile>, ImageFile>() {
            @Override
            public List<ImageFile> doInBackground() {
                if (dir.isDirectory() && dir.canRead()) {
                    File[] files = dir.listFiles();
                    for (File file : files) {
                        ImageInfo info = Imager.readImageInfo(file);
                        if (info != null) {
                            ImageFile imageFile = new ImageFile(file);
                            imageFile.setFormat(info.getFormat());
                            imageFile.setHeight(info.getHeight());
                            imageFile.setWidth(info.getWidth());
                            publish(imageFile);
                        }
                    }
                }
                return null;
            }

            @Override
            protected void process(List<ImageFile> files) {
                for (ImageFile file : files) {
                    model.addImageFile(file);
                }
//                ColumnSort cs = getPrimarySort();
//                if (cs != null) setColumnSort(cs);
            }

            @Override
            public void done() {
                currentView.refreshView();
                ColumnSort cs = getPrimarySort();
                if (cs != null) {
                    setColumnSort(cs);
                }
            }
        }.execute();
    }

    public void addImagesFromFiles(final File[] files) {
        final ExplorerModel model = getModel();
        new SwingWorker<List<ImageFile>, ImageFile>() {
            @Override
            public List<ImageFile> doInBackground() {
                for (File file : files) {
                    ImageInfo info = Imager.readImageInfo(file);
                    if (info != null) {
                        ImageFile imageFile = new ImageFile(file);
                        imageFile.setFormat(info.getFormat());
                        imageFile.setHeight(info.getHeight());
                        imageFile.setWidth(info.getWidth());
                        publish(imageFile);
                    }
                }
                return null;
            }

            @Override
            protected void process(List<ImageFile> files) {
                for (ImageFile file : files) {
                    model.addImageFile(file);
                }
            }

            @Override
            public void done() {
                currentView.refreshView();
                ColumnSort cs = getPrimarySort();
                if (cs != null) {
                    setColumnSort(cs);
                }
            }
        }.execute();
    }

    // PopupMenu
    public JPopupMenu getPopupMenu() {
        return popupMenu;
    }

    public void setPopupMenu(JPopupMenu popupMenu) {
        this.popupMenu = popupMenu;
    }

    public void setDefaultPopupMenu() {
        setPopupMenu(new ExplorerPopup(this).createPopupMenu());
    }
}
