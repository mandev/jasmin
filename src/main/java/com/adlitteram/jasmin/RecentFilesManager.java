
package com.adlitteram.jasmin;

import org.slf4j.LoggerFactory;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import org.slf4j.Logger;

abstract public class RecentFilesManager implements ActionListener {

    private static final Logger logger = LoggerFactory.getLogger(RecentFilesManager.class);
    //
    //private AppManager appManager;
    private ArrayList<String> filenameList;

    public RecentFilesManager() {
        //this.appManager = appManager;

        int keep = XProp.getInt("RecentFiles.Keep", 4);
        filenameList = new ArrayList<String>(keep);

        for (int i = 0; i < keep; i++) {
            String fileName = XProp.get("RecentFiles.Filename_" + i);
            if (fileName != null) filenameList.add(fileName);
        }
    }

    public ArrayList<String> getFilenameList() {
        return filenameList;
    }

    public ActionListener getActionListener() {
        return this;
    }

    public void saveProperties() {
        int keep = XProp.getInt("RecentFiles.Keep", 4);
        int max = Math.min(keep, filenameList.size());
        for (int i = 0; i < max; i++) {
            XProp.put("RecentFiles.Filename_" + i, filenameList.get(i));
        }

        for (int i = max;; i++) {
            String filename = "RecentFiles.Filename_" + i;
            if (XProp.get(filename) == null) break;
            XProp.unsetProperty(filename);
        }
    }

    public void addFilename(String filename) {
        if (filename != null) {
            int index = filenameList.indexOf(filename);
            if (index >= 0) filenameList.remove(index);
            filenameList.add(0, filename);
        }
    }

    abstract public void actionPerformed(ActionEvent e) ;

}
