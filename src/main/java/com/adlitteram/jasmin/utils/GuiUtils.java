package com.adlitteram.jasmin.utils;

import com.adlitteram.jasmin.Application;
import com.adlitteram.jasmin.Applicationable;
import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.gui.widget.DirChooser;
import com.adlitteram.jasmin.gui.widget.FileChooser;
import com.adlitteram.jasmin.gui.widget.MultilineLabel;
import com.adlitteram.jasmin.property.XProp;
import com.ezware.dialog.task.TaskDialog;
import com.ezware.dialog.task.TaskDialog.StandardCommand;
import org.apache.commons.lang3.SystemUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.imageio.ImageIO;
import javax.swing.*;
import javax.swing.text.JTextComponent;
import java.awt.*;
import java.awt.Dialog.ModalityType;
import java.awt.event.ActionEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

public class GuiUtils {

    private static final Logger LOGGER = LoggerFactory.getLogger(GuiUtils.class);

    private static Applicationable application;

    private static final String INFO = Message.get("Information");
    private static final String ERROR = Message.get("Error");
    public static final String ICON_DIR = "icons/";
    public static final Icon OPEN_ICON = loadIcon("icons/folder.png", Application.class);
    public static final Icon HELP_ICON = loadIcon("icons/help.png", Application.class);

    public static void init(Applicationable app) {
        application = app;
    }

    // Center Component on Screen
    public static void centerToScreen(Component cmpt) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        int x = (screen.width - cmpt.getWidth()) / 2;
        int y = (screen.height - cmpt.getHeight()) / 2;
        cmpt.setLocation(Math.max(0, x), Math.max(0, y));
    }

    // Center Component on its Parent
    public static void centerToParent(Component parent, Component child) {
        if (parent == null) {
            centerToScreen(child);
        } else {
            Rectangle par = parent.getBounds();
            Rectangle chi = child.getBounds();
            child.setLocation(par.x + (par.width - chi.width) / 2, par.y + (par.height - chi.height) / 2);
        }
    }

    // Save Component Bounds
    public static void saveBounds(Component cmpt, String key) {
        Point pt = cmpt.getLocation();
        XProp.put(key + ".x", pt.x);
        XProp.put(key + ".y", pt.y);
        XProp.put(key + ".width", cmpt.getWidth());
        XProp.put(key + ".height", cmpt.getHeight());
    }

    // Load & set Component Bounds
    public static void loadBounds(Component cmpt, String key) {
        loadBounds(cmpt, key, false);
    }

    public static void loadBounds(Component cmpt, String key, boolean force) {
        Dimension screen = Toolkit.getDefaultToolkit().getScreenSize();
        Container parent = cmpt.getParent();
        int xx, yy, ww, hh;

        if (parent == null) {
            xx = 0;
            yy = 0;
            ww = screen.width;
            hh = screen.height;
        } else {
            xx = parent.getX();
            yy = parent.getY();
            ww = parent.getWidth();
            hh = parent.getHeight();
        }

        int width = cmpt.getWidth();
        if (width == 0) {
            width = ww / 2;
        }

        int height = cmpt.getHeight();
        if (height == 0) {
            height = hh / 2;
        }

        int x = (2 * xx + ww - width) / 2;
        int y = (2 * yy + hh - height) / 2;

        try {
            if (force || XProp.getBoolean("General.KeepDialogPos", true)) {
                x = XProp.getInt(key + ".x", x);
                y = XProp.getInt(key + ".y", y);
            }
            if (force || XProp.getBoolean("General.KeepDialogSize", true)) {
                width = XProp.getInt(key + ".width", width);
                height = XProp.getInt(key + ".height", height);
            }
            cmpt.setBounds(NumUtils.clamp(0, x, screen.width - width), NumUtils.clamp(0, y, screen.height - height), width, height);
        } catch (Exception e) {
            centerToScreen(cmpt);
        }
    }

    public static void saveDivider(JSplitPane splitPane, String key) {
        int location = splitPane.getDividerLocation();
        XProp.put(key + ".divider", location);
    }

    public static void loadDivider(JSplitPane splitPane, String key) {
        int location = XProp.getInt(key + ".divider", 500);
        splitPane.setDividerLocation(location);
    }

    public static JPanel concat(JComponent cp1, JComponent cp2) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(cp1);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(cp2);
        return panel;
    }

    public static JPanel addSeparator(String str) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JLabel(str));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(new JSeparator());
        return panel;
    }

    public static JPanel addLabel(String str, JComponent cp) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(new JLabel(str));
        panel.add(Box.createHorizontalStrut(5));
        panel.add(cp);
        return panel;
    }

    public static JPanel addLabel(JComponent cp, String str) {
        JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
        panel.add(cp);
        panel.add(Box.createHorizontalStrut(5));
        panel.add(new JLabel(str));
        return panel;
    }

    // Load Icon from Resource File
    public static Icon loadIcon(String key) {
        return loadIcon(key, application.getMainClass());
    }

    public static Icon loadIcon(String key, Class clazz) {
        String iconName = XProp.getProperty(key, key);
        String iconSize = XProp.get("Icon.Size");
        if (iconSize != null) {
            iconName = iconSize + "_" + iconName;
        }
        URL url = clazz.getResource(iconName);
        if (url == null) {
            LOGGER.info("Unable to load icon - url is null: " + iconName + " - Key:  " + key);
            return null;
        }
        return new ImageIcon(url);
    }

    private static URI URLtoURI(URL url) {
        try {
            if (url != null) {
                return url.toURI();
            }
        } catch (URISyntaxException ex) {
            LOGGER.warn("XProps.URLtoURI() : {}", url);
        }
        return null;
    }

    public static Image loadImage(String path) {
        return loadImage(path, application.getMainClass());
    }

    public static Image loadImage(String path, Class clazz) {
        try {
            URL url = clazz.getResource(path);
            if (url != null) {
                return ImageIO.read(url);
            }
            LOGGER.info("Not valid image URL (url=null) - " + path);
        } catch (IOException e) {
            LOGGER.warn("", e);
        }
        return null;
    }

    // Warning Message Dialog
    public static void showMessage(Object message) {
        showMessage(application.getMainFrame(), message);
    }

    // Error Message Dialog
    public static void showError(Object message) {
        showError(application.getMainFrame(), message);
    }

    public static void showException(Applicationable app, Window window, String title, String text, Throwable ex) {

        //window.requestFocusInWindow();
        TaskDialog dlg = new TaskDialog(window, title);
        dlg.setModalityType(ModalityType.TOOLKIT_MODAL);
        dlg.setIcon(TaskDialog.StandardIcon.ERROR);
        dlg.setInstruction(Message.get("ApplicationException"));
        dlg.setText((text == null || text.trim().length() == 0) ? ex.getLocalizedMessage() : text);
        dlg.setCommands(StandardCommand.CANCEL.derive(Message.get("Close")));

        StringBuilder sb = new StringBuilder(StrUtils.stackStraceAsString(ex));
        sb.append("\nOS_NAME = ").append(SystemUtils.OS_NAME);
        sb.append("\nOS_VERSION = ").append(SystemUtils.OS_VERSION);
        sb.append("\nJAVA_VERSION = ").append(SystemUtils.JAVA_VERSION);
        sb.append("\nJAVA_VENDOR = ").append(SystemUtils.JAVA_VENDOR);
        if (app != null) {
            sb.append("\nAPPLICATION_NAME = ").append(app.getApplicationName());
            sb.append("\nAPPLICATION_RELEASE = ").append(app.getApplicationRelease());
            sb.append("\nAPPLICATION_BUILD = ").append(app.getApplicationBuild());
        }

        final JTextArea textArea = new JTextArea();
        textArea.setDragEnabled(true);
        textArea.setEditable(false);
        textArea.setFont(UIManager.getFont("Label.font"));
        textArea.setText(sb.toString());
        textArea.setCaretPosition(0);

        JMenuItem copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(e -> textArea.copy());

        JMenuItem copyAllItem = new JMenuItem("Copy All");
        copyAllItem.addActionListener(e -> {
            textArea.selectAll();
            textArea.copy();
        });

        final JPopupMenu popmenu = new JPopupMenu();
        popmenu.add(copyItem);
        popmenu.add(copyAllItem);

        textArea.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent e) {
                if (SwingUtilities.isRightMouseButton(e)) {
                    popmenu.show(e.getComponent(), e.getX(), e.getY());
                }
            }
        });

        JScrollPane scroller = new JScrollPane(textArea);
        scroller.setPreferredSize(new Dimension(600, 200));
        dlg.getDetails().setExpandableComponent(scroller);
        dlg.getDetails().setExpanded(false);
        dlg.setVisible(true);
    }

    public static void showMessage(final Window frame, final Object message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(() -> doShowMessage(frame, message));
            } catch (InterruptedException | InvocationTargetException ex) {
                LOGGER.warn("", ex);
            }
        } else {
            doShowMessage(frame, message);
        }
    }

    // Error Message Dialog
    public static void showError(final Window frame, final Object message) {
        if (!SwingUtilities.isEventDispatchThread()) {
            try {
                SwingUtilities.invokeAndWait(() -> doShowError(frame, message));
            } catch (InterruptedException | InvocationTargetException ex) {
                LOGGER.warn("", ex);
            }
        } else {
            doShowError(frame, message);
        }
    }

    private static void doShowMessage(Window frame, Object message) {
        if (frame != null) {
            frame.toFront();
        }
        JOptionPane.showMessageDialog(frame, message, INFO, JOptionPane.INFORMATION_MESSAGE);
    }

    // Error Message Dialog
    private static void doShowError(Window frame, Object message) {
        if (frame != null) {
            frame.toFront();
        }
        JOptionPane.showMessageDialog(frame, message, ERROR, JOptionPane.ERROR_MESSAGE);
    }

    // Yes/No Message Dialog
    public static boolean showYesNoOptionDialog(final Component parent, final String title, final String text, final int width) {
        if (!SwingUtilities.isEventDispatchThread()) {
            class DialogState implements Runnable {

                boolean status;

                @Override
                public void run() {
                    status = doShowYesNoOptionDialog(parent, title, text, width);
                }
            }
            try {
                DialogState ds = new DialogState();
                SwingUtilities.invokeAndWait(ds);
                return ds.status;
            } catch (InterruptedException | InvocationTargetException ex) {
                LOGGER.warn("", ex);
                return false;
            }
        } else {
            return doShowYesNoOptionDialog(parent, title, text, width);
        }
    }

    private static boolean doShowYesNoOptionDialog(Component parent, String title, String text, int width) {
        MultilineLabel label = new MultilineLabel(text);
        label.setMaxWidth(width);

        Object[] options = {Message.get("Ok"), Message.get("Cancel")};

        return (JOptionPane.showOptionDialog(parent, label, title,
                JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]) == JOptionPane.YES_OPTION);
    }
    // Create a rectangele

    // Change cursor Icon
    public static void setCursorOnWait(Component cmpt, boolean on) {
        if (cmpt != null) {
            cmpt.setCursor(Cursor.getPredefinedCursor(on ? Cursor.WAIT_CURSOR : Cursor.DEFAULT_CURSOR));
        }
    }

    public static Rectangle createRect(int x, int y, int w, int h) {
        if (w < 0) {
            w = -w;
            x = x - w;
        }
        if (h < 0) {
            h = -h;
            y = y - h;
        }
        return new Rectangle(x, y, w, h);
    }

    public static Rectangle2D.Float createRect(float x, float y, float w, float h) {
        if (w < 0) {
            w = -w;
            x = x - w;
        }
        if (h < 0) {
            h = -h;
            y = y - h;
        }
        return new Rectangle2D.Float(x, y, w, h);
    }

    public static JButton createBrowseButton(JTextField textField) {
        return createBrowseButton(textField, textField, null, "save");
    }

    public static JButton createBrowseButton(Component parent, JTextField textField) {
        return createBrowseButton(parent, textField, null, "save");
    }

    public static JButton createBrowseButton(Component parent, JTextField textField, javax.swing.filechooser.FileFilter filter) {
        return createBrowseButton(parent, textField, null, "save");
    }

    // DIRECTORIES_ONLY / FILES_AND_DIRECTORIES / FILES_ONLY
    public static JButton createBrowseButton(Component parent, JTextField textField, javax.swing.filechooser.FileFilter filter, String mode) {
        Frame frame = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        JButton browseButton = new JButton(OPEN_ICON);
        browseButton.putClientProperty("BrowseTextField", textField);
        browseButton.putClientProperty("BrowseParent", frame);
        browseButton.putClientProperty("BrowseFilter", filter);
        browseButton.putClientProperty("BrowseMode", mode);

        browseButton.setContentAreaFilled(false);
        browseButton.setOpaque(false);
        browseButton.setMargin(new Insets(0, 3, 0, 5));
        browseButton.addActionListener((ActionEvent e) -> {
            JButton button = (JButton) e.getSource();
            JTextField textField1 = (JTextField) button.getClientProperty("BrowseTextField");
            FileChooser fc = (FileChooser) button.getClientProperty("BrowseFileChooser");
            if (fc == null) {
                Component parent1 = (Component) button.getClientProperty("Parent");
                fc = new FileChooser(parent1, Message.get("Select"));
                fc.setFile(textField1.getText());
                javax.swing.filechooser.FileFilter filter1 = (javax.swing.filechooser.FileFilter) button.getClientProperty("BrowseFilter");
                if (filter1 != null) {
                    fc.addFileFilter(filter1);
                }
                button.putClientProperty("BrowseFileChooser", fc);
            }
            int status = ("save".equals(button.getClientProperty("BrowseMode"))) ? fc.showSaveDialog() : fc.showOpenDialog();
            if (status == FileChooser.APPROVE_OPTION) {
                textField1.setText(fc.getSelectedFile().getPath());
                textField1.postActionEvent();
            }
        });
        return browseButton;
    }

    public static JButton createDirButton(JTextComponent textComponent) {
        return createDirButton(textComponent, textComponent);
    }

    public static JButton createDirButton(Component parent, JTextComponent textComponent) {

        Frame frame = (parent instanceof Frame) ? (Frame) parent : (Frame) SwingUtilities.getAncestorOfClass(Frame.class, parent);

        JButton browseButton = new JButton(OPEN_ICON);
        browseButton.putClientProperty("BrowseTextField", textComponent);
        browseButton.putClientProperty("BrowseParent", frame);
        browseButton.setContentAreaFilled(false);
        browseButton.setOpaque(false);
        browseButton.setMargin(new Insets(0, 3, 0, 5));

        browseButton.addActionListener((ActionEvent e) -> {
            JButton button = (JButton) e.getSource();

            JTextComponent textCmp = (JTextComponent) button.getClientProperty("BrowseTextField");
            DirChooser fc = (DirChooser) button.getClientProperty("BrowseFileChooser");

            if (fc == null) {
                String text = "";
                if (textCmp instanceof JTextField) {
                    text = textCmp.getText();
                } else {
                    String[] dirs = textCmp.getText().split(";");
                    if (dirs.length > 0) {
                        text = dirs[dirs.length - 1];
                    }
                }

                fc = new DirChooser((Frame) button.getClientProperty("BrowseParent"), text);
                button.putClientProperty("BrowseFileChooser", fc);
            }

            if (fc.showDialog() == DirChooser.APPROVE_OPTION) {
                if (textCmp instanceof JTextField) {
                    textCmp.setText(fc.getSelectedDirectory().getPath());
                    ((JTextField) textCmp).postActionEvent();
                } else {
                    boolean contains = false;
                    String ndir = fc.getSelectedDirectory().getPath();
                    String[] dirs = textCmp.getText().split(";");
                    String text = "";
                    for (String dir : dirs) {
                        if (dir.length() > 0) {
                            text += dir + ";";
                            if (dir.equals(ndir)) {
                                contains = true;
                            }
                        }
                    }
                    if (!contains) {
                        text += ndir;
                    }
                    textCmp.setText(text);
                }
            }
        });
        return browseButton;
    }

    public static void invertFocusTraversalBehaviour(JTextArea textArea) {
        Set<AWTKeyStroke> forwardKeys = textArea.getFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS);
        Set<AWTKeyStroke> backwardKeys = textArea.getFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS);

        // check that we WANT to modify current focus traversal keystrokes
        if (forwardKeys.size() != 1 || backwardKeys.size() != 1) {
            return;
        }
        final AWTKeyStroke fks = forwardKeys.iterator().next();
        final AWTKeyStroke bks = backwardKeys.iterator().next();
        final int fkm = fks.getModifiers();
        final int bkm = bks.getModifiers();
        final int ctrlMask = KeyEvent.CTRL_MASK + KeyEvent.CTRL_DOWN_MASK;
        final int ctrlShiftMask = KeyEvent.SHIFT_MASK + KeyEvent.SHIFT_DOWN_MASK + ctrlMask;
        if (fks.getKeyCode() != KeyEvent.VK_TAB || (fkm & ctrlMask) == 0 || (fkm & ctrlMask) != fkm) {    // not currently CTRL+TAB for forward focus traversal
            return;
        }
        if (bks.getKeyCode() != KeyEvent.VK_TAB || (bkm & ctrlShiftMask) == 0 || (bkm & ctrlShiftMask) != bkm) {    // not currently CTRL+SHIFT+TAB for backward focus traversal
            return;
        }

        // bind our new forward focus traversal keys
        Set<AWTKeyStroke> newForwardKeys = new HashSet<>(1);
        newForwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, 0));
        textArea.setFocusTraversalKeys(KeyboardFocusManager.FORWARD_TRAVERSAL_KEYS, Collections.unmodifiableSet(newForwardKeys));
        // bind our new backward focus traversal keys
        Set<AWTKeyStroke> newBackwardKeys = new HashSet<>(1);
        newBackwardKeys.add(AWTKeyStroke.getAWTKeyStroke(KeyEvent.VK_TAB, KeyEvent.SHIFT_MASK + KeyEvent.SHIFT_DOWN_MASK));
        textArea.setFocusTraversalKeys(KeyboardFocusManager.BACKWARD_TRAVERSAL_KEYS, Collections.unmodifiableSet(newBackwardKeys));

        // Now, it's still useful to be able to type TABs in some cases.
        // Using this technique assumes that it's rare however (if the user
        // is expected to want to type TAB often, consider leaving text area's
        // behaviour unchanged...).  Let's add some key bindings, inspired
        // from a popular behaviour in instant messaging applications...
        TextInserter.applyTabBinding(textArea);

        // we could do the same stuff for RETURN and CTRL+RETURN for activating
        // the root pane's default button: omitted here for brevity
    }

    public static class TextInserter extends AbstractAction {

        private final JTextArea textArea;
        private final String insertable;

        private TextInserter(JTextArea textArea, String insertable) {
            this.textArea = textArea;
            this.insertable = insertable;
        }

        public static void applyTabBinding(JTextArea textArea) {
            textArea.getInputMap(JComponent.WHEN_FOCUSED).put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, KeyEvent.CTRL_MASK + KeyEvent.CTRL_DOWN_MASK), "tab");
            textArea.getActionMap().put("tab", new TextInserter(textArea, "\t"));
        }

        @Override
        public void actionPerformed(ActionEvent evt) {
            // could be improved to overtype selected range
            textArea.insert(insertable, textArea.getCaretPosition());
        }
    }

    public static String clipStringIfNecessary(FontMetrics fm, String str, int availWidth) {
        if (str == null || str.length() == 0) {
            return "";
        }
        int width = SwingUtilities.computeStringWidth(fm, str);
        return (width > availWidth) ? clipString(fm, str, availWidth) : str;
    }

    public static String clipString(FontMetrics fm, String str, int availWidth) {
        String clipString = "...";
        int width = SwingUtilities.computeStringWidth(fm, clipString);
        int nChars = 0;
        for (int max = str.length(); nChars < max; nChars++) {
            width += fm.charWidth(str.charAt(nChars));
            if (width > availWidth) {
                break;
            }
        }
        return str.substring(0, nChars) + clipString;
    }

    public static ArrayList modelToList(ListModel model) {
        ArrayList list = new ArrayList(model.getSize());
        for (int i = 0; i < model.getSize(); i++) {
            list.add(model.getElementAt(i));
        }
        return list;
    }

}
