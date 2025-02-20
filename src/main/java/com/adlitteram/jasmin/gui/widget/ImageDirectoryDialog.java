package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.gui.explorer.ExplorerPane;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class ImageDirectoryDialog extends JDialog {

    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    private int status;
    private final String dirName;
    private DirectoryChooser dc;
    private ExplorerPane explorerPane;
    private JButton okButton;

    ActionListener approveListener = e -> {
        dispose();
        status = dc.getSelectedDirectory() != null ? APPROVE_OPTION : CANCEL_OPTION;
    };

    public ImageDirectoryDialog(Dialog dialog, String dirName, String title) {
        super(dialog, title, true);
        this.dirName = dirName;
        init();
    }

    public ImageDirectoryDialog(Frame frame, String dirName, String title) {
        super(frame, title, true);
        this.dirName = dirName;
        init();
    }

    private void init() {
        status = CANCEL_OPTION;
        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        getContentPane().setLayout(new BorderLayout(10, 0));
        getContentPane().add(buildDirectoryPanel(), BorderLayout.WEST);
        getContentPane().add(buildImagePanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(getParent());
    }

    private JComponent buildImagePanel() {

        explorerPane = new ExplorerPane();
        explorerPane.setIconSize(ExplorerPane.MEDIUM_ICON);
        explorerPane.setDefaultPopupMenu();
        explorerPane.setRubberBandEnabled(false);
        explorerPane.setDragEnabled(false);
        explorerPane.setPreferredSize(new Dimension((explorerPane.getIconSize() + 2 * explorerPane.getIconGap() + 12) * 3 + 20, 500));
        explorerPane.setImagesFromDir(new File(dirName));
        explorerPane.setFullScreenEnabled(true);
        return explorerPane;
    }

    private JComponent buildDirectoryPanel() {
        File dir = new File(dirName);
        if (!dir.isDirectory()) {
            dc = new DirectoryChooser();
        } else {
            dc = new DirectoryChooser(dir);
        }

        dc.addActionListener(approveListener);
        dc.addPropertyChangeListener((PropertyChangeEvent ev) -> {
            if (ev.getPropertyName().equals("selectedDirectory")) {
                File file = dc.getSelectedDirectory();
                if (file != null) {
                    okButton.setEnabled(true);
                    explorerPane.setImagesFromDir(file);
                }
            }
        });

        dc.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JScrollPane sp = new JScrollPane(dc);
        sp.setPreferredSize(new Dimension(250, 300));
        return sp;
    }

    private JPanel buildButtonPanel() {
        okButton = new JButton(Message.get("Ok"));
        okButton.addActionListener(approveListener);

        JButton cancelButton = new JButton(Message.get("Cancel"));
        cancelButton.addActionListener((ActionEvent e) -> {
            dispose();
            status = CANCEL_OPTION;
        });

        int[] w = {5, 0, -5, 5, -3, 6};
        int[] h = {10, 0, 10};
        HIGLayout l = new HIGLayout(w, h);
        HIGConstraints c = new HIGConstraints();
        l.setColumnWeight(2, 1);

        JPanel panel = new JPanel(l);
        panel.add(okButton, c.xy(3, 2));
        panel.add(cancelButton, c.xy(5, 2));
        return panel;
    }

    public int showDialog() {
        dc.scrollRowToVisible(Math.max(0, dc.getMinSelectionRow() - 4));
        setVisible(true);
        return status;
    }

    public File getSelectedDirectory() {
        return dc.getSelectedDirectory();
    }
}
