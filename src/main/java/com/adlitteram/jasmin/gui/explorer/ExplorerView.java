package com.adlitteram.jasmin.gui.explorer;

import java.awt.*;

public interface ExplorerView {

    ExplorerPane getExplorerPane();

    int getLocationToIndex(Point point);

    void refreshView();
}
