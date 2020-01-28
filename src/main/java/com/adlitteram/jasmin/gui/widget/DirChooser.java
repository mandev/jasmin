package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.utils.PlatformUtils;
import java.awt.Component;
import java.awt.FileDialog;
import java.awt.Frame;
import java.io.File;
import javax.swing.JFileChooser;

public class DirChooser {

    static final int LOAD = FileDialog.LOAD;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    private boolean isNative;
    private FileDialog awtChooser;
    private DirectoryDialog swingChooser;
    private String title;
    private String dirname;
    private File selectedDir;

    public DirChooser(String dirname) {
        this(null, dirname);
    }

    public DirChooser(Component cmp, String dirname) {
        this(cmp, dirname, Message.get("DirectoryDialog.Title"));
    }

    public DirChooser(Component cmp, String dirname, String title) {
        this.title = title;
        this.dirname = dirname;
        if (dirname == null) {
            dirname = PlatformUtils.getHomeDir();
        }

        Frame frame = (cmp instanceof Frame) ? (Frame) cmp : null;
        isNative = XProp.getBoolean("DirChooser.IsNative", false);

        if (isNative) {
            awtChooser = new FileDialog(frame, title);
            awtChooser.setMode(LOAD);
            awtChooser.setDirectory(dirname);
            awtChooser.setLocationRelativeTo(frame);
        } else {
            swingChooser = new DirectoryDialog(frame, dirname, title);
            swingChooser.setLocationRelativeTo(frame);
        }
    }

    private int showAwtDialog() {
        System.setProperty("apple.awt.fileDialogForDirectories", "true");
        awtChooser.setVisible(true);
        System.setProperty("apple.awt.fileDialogForDirectories", "false");

        String filename = awtChooser.getFile();
        if (filename != null) {
            selectedDir = new File(awtChooser.getDirectory(), filename);
            return APPROVE_OPTION;
        }
        return CANCEL_OPTION;
    }

    private int showSwingDialog() {
        if (swingChooser.showDialog() == APPROVE_OPTION) {
            selectedDir = swingChooser.getSelectedDirectory();
            return APPROVE_OPTION;
        }
        return CANCEL_OPTION;
    }

    public boolean isNative() {
        return isNative;
    }

    public int showDialog() {
        selectedDir = null;
        return (isNative) ? showAwtDialog() : showSwingDialog();
    }

    public Component getChooser() {
        if (isNative) {
            return awtChooser;
        } else {
            return swingChooser;
        }
    }

    public File getSelectedDirectory() {
        return selectedDir;
    }
}
