
package com.adlitteram.jasmin.gui.widget;

import com.adlitteram.jasmin.Message;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.WindowConstants;

public class DirectoryDialog extends JDialog {

    public static final int APPROVE_OPTION = JFileChooser.APPROVE_OPTION;
    public static final int CANCEL_OPTION = JFileChooser.CANCEL_OPTION;
    private int status;
    private String dirname;
    private DirectoryChooser dc;
    private JButton okButton;
    //////////////////////////////////////////////////////////////////////////////////////////////
    // LISTENERS
    //////////////////////////////////////////////////////////////////////////////////////////////
    ActionListener approveListener = new ActionListener() {

        @Override
        public void actionPerformed(ActionEvent e) {
            dispose();
            if (dc.getSelectedDirectory() != null) status = APPROVE_OPTION;
            else status = CANCEL_OPTION;
        }
    };

    //////////////////////////////////////////////////////////////////////////////////////////////
    // CONSTRUCTORS
    //////////////////////////////////////////////////////////////////////////////////////////////
    public DirectoryDialog(Frame frame, String dirname, String title) {
        super(frame, title, true);

        this.dirname = dirname;
        status = CANCEL_OPTION;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);

        // build panel
        getContentPane().add(buildDirectoryPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);

        //dialog.setBounds(200, 200, 300, 350);
        setLocationRelativeTo(getParent());
        pack();
    }

    //////////////////////////////////////////////////////////////////////////////////////////////
    // METHODS
    //////////////////////////////////////////////////////////////////////////////////////////////
    private JComponent buildDirectoryPanel() {
        File dir = new File(dirname);
        if (dir == null || !dir.isDirectory()) {
            dc = new DirectoryChooser();
        }
        else {
            dc = new DirectoryChooser(dir);
        }

        dc.addActionListener(approveListener);
        dc.addPropertyChangeListener(new PropertyChangeListener() {

            @Override
            public void propertyChange(PropertyChangeEvent ev) {
                if (ev.getPropertyName().equals("selectedDirectory")) {
                    okButton.setEnabled(dc.getSelectedDirectory() != null);
                }
            }
        });

        dc.setBorder(BorderFactory.createEmptyBorder(4, 4, 4, 4));
        JScrollPane sp = new JScrollPane(dc);
        sp.setPreferredSize(new Dimension(300, 300));
        return sp;
    }

    private JPanel buildButtonPanel() {
        okButton = new JButton(Message.get("Ok"));
        //okButton.setEnabled(false) ;

        okButton.addActionListener(approveListener);

        JButton cancelButton = new JButton(Message.get("Cancel"));
        cancelButton.addActionListener(new ActionListener() {

            @Override
            public void actionPerformed(ActionEvent e) {
                dispose();
                status = CANCEL_OPTION;
            }
        });

        int w[] = {5, 0, -5, 5, -3, 6};
        int h[] = {10, 0, 10};
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
