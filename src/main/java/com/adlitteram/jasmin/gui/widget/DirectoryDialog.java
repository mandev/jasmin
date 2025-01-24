package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.Message;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;

public class DirectoryDialog extends JDialog {

    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    private int status;
    private final String dirName;
    private DirectoryChooser dc;
    private JButton okButton;

    private final ActionListener approveListener = e -> {
        status = dc.getSelectedDirectory() != null ? APPROVE_OPTION : CANCEL_OPTION;
        dispose();
    };

    public DirectoryDialog(Frame frame, String dirName, String title) {
        super(frame, title, true);

        this.dirName = dirName;
        status = CANCEL_OPTION;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(buildDirectoryPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);
        setLocationRelativeTo(getParent());
        pack();
    }

    private JComponent buildDirectoryPanel() {
        File dir = new File(dirName);
        dc = dir.isDirectory() ? new DirectoryChooser(dir) : new DirectoryChooser();
        dc.addActionListener(approveListener);
        dc.addPropertyChangeListener((PropertyChangeEvent ev) -> {
            if (ev.getPropertyName().equals("selectedDirectory")) {
                okButton.setEnabled(dc.getSelectedDirectory() != null);
            }
        });

        dc.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JScrollPane sp = new JScrollPane(dc);
        sp.setPreferredSize(new Dimension(300, 300));
        return sp;
    }

    private JPanel buildButtonPanel() {
        okButton = new JButton(Message.get("Ok"));
        okButton.addActionListener(approveListener);

        JButton cancelButton = new JButton(Message.get("Cancel"));
        cancelButton.addActionListener(e -> {
            status = CANCEL_OPTION;
            dispose();
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
