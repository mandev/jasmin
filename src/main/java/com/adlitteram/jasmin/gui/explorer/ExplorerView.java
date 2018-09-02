/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.adlitteram.jasmin.gui.explorer;

import java.awt.Point;

public interface ExplorerView {

    public ExplorerPane getExplorerPane();

    public int getLocationToIndex(Point point);

    public void refreshView();
}
