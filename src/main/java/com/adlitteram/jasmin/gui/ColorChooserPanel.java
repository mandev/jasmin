package com.adlitteram.jasmin.gui;

import com.adlitteram.jasmin.Message;
import com.adlitteram.jasmin.color.ColorPalette;
import com.adlitteram.jasmin.color.NamedColor;
import com.adlitteram.jasmin.color.XColorsReader;
import com.adlitteram.jasmin.color.XmlColorsWriter;
import com.adlitteram.jasmin.gui.combo.ColorComboRenderer;
import com.adlitteram.jasmin.gui.layout.VerticalLayout;
import com.adlitteram.jasmin.gui.widget.FileChooser;
import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.utils.ExtFilter;
import com.adlitteram.jasmin.utils.GuiUtils;
import com.adlitteram.jasmin.utils.PlatformUtils;
import cz.autel.dmi.HIGConstraints;
import cz.autel.dmi.HIGLayout;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import java.awt.*;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

public class ColorChooserPanel extends JDialog {

    private static int tabIndex;
    private final ColorPalette colorPalette;
    private JColorChooser colorChooser;
    private JList<NamedColor> colorList;
    private DefaultListModel<NamedColor> colorModel;
    private JTextField nameField;
    private NamedColor nccolor;
    private JTabbedPane tabbedPane;

    public ColorChooserPanel(JFrame frame, ColorPalette colorPalette) {
        this(frame, colorPalette, null);
    }

    public ColorChooserPanel(JFrame frame, ColorPalette colorPalette, NamedColor color) {

        super(frame, Message.get("ColorChooserPanel.ColorChooser"), true);
        this.colorPalette = colorPalette;

        setDefaultCloseOperation(WindowConstants.DISPOSE_ON_CLOSE);
        getContentPane().add(buildColorPanel(), BorderLayout.CENTER);
        getContentPane().add(buildButtonPanel(), BorderLayout.SOUTH);

        if (color != null) {
            colorChooser.setColor(color);
            nameField.setText(color.getName());
        }

        colorChooser.getSelectionModel().addChangeListener((ChangeEvent evt) -> {
            Color color1 = colorChooser.getSelectionModel().getSelectedColor();
            nameField.setText("R" + color1.getRed() + "G" + color1.getGreen() + "B" + color1.getBlue());
        });

        pack();
        GuiUtils.loadBounds(this, "ColorChooserPanel");
        setVisible(true);
    }

    private JPanel buildButtonPanel() {

        JButton addButton = new JButton(Message.get("Select"));
        getRootPane().setDefaultButton(addButton);
        addButton.addActionListener(e -> addPressed());

        JButton okButton = new JButton(Message.get("Ok"));
        okButton.addActionListener(e -> okPressed());

        JButton cancelButton = new JButton(Message.get("Cancel"));
        cancelButton.addActionListener(e -> cancelPressed());

        int[] w = {5, 0, 5, 0, -7, 5, -9, 5, -5, 6};
        int[] h = {10, 0, 10};
        HIGLayout l = new HIGLayout(w, h);
        HIGConstraints c = new HIGConstraints();
        l.setColumnWeight(4, 1);

        JPanel panel = new JPanel(l);
        panel.add(addButton, c.xy(5, 2));
        panel.add(okButton, c.xy(7, 2));
        panel.add(cancelButton, c.xy(9, 2));
        return panel;
    }

    private JPanel buildColorPanel() {
        int[] w = {5, 0, 10, 0, 10, 0, 5};
        int[] h = {5, 0, 5};

        HIGLayout l = new HIGLayout(w, h);
        HIGConstraints c = new HIGConstraints();
        l.setColumnWeight(6, 1);
        l.setRowWeight(2, 1);

        JPanel panel = new JPanel(l);

        panel.add(buildChooserPanel(), c.xy(2, 2));
        panel.add(buildAddPanel(), c.xy(4, 2));
        panel.add(buildColorList(), c.xy(6, 2));
        return panel;
    }

    private JColorChooser buildChooserPanel() {
        colorChooser = new JColorChooser();
        colorChooser.addChooserPanel(new CMYKChooserPanel());
        tabbedPane = getTab(colorChooser);
        tabbedPane.setSelectedIndex(tabIndex);
        return colorChooser;
    }

    private int containsColor(NamedColor color) {
        for (int i = 0; i < colorModel.getSize(); i++) {
            NamedColor ncolor = colorModel.getElementAt(i);
            if ((ncolor.getRGB() == color.getRGB()) && (ncolor.getCMYK() == color.getCMYK())) {
                return i;
            }
        }
        return -1;
    }

    private JPanel buildAddPanel() {

        JButton addButton = new JButton(Message.get("Add"));
        addButton.addActionListener(e -> {
            nccolor = new NamedColor(colorChooser.getColor(), nameField.getText());
            int idx = containsColor(nccolor);
            if (idx >= 0) {
                colorModel.set(idx, nccolor);
            } else {
                colorModel.addElement(nccolor);
            }
        });

        JButton removeButton = new JButton(Message.get("Remove"));
        removeButton.addActionListener(e -> {
            int idx = colorList.getSelectedIndex();
            if (idx != -1) {
                colorModel.removeElementAt(idx);
            }
        });

        JButton defaultButton = new JButton(Message.get("Reset"));
        defaultButton.addActionListener(e -> {
            colorModel.clear();
            NamedColor[] ncolors = ColorPalette.getDefaultColors();
            for (NamedColor ncolor : ncolors) {
                colorModel.addElement(ncolor);
            }
        });

        JButton clearButton = new JButton(Message.get("Clear"));
        clearButton.addActionListener(e -> colorModel.clear());

        JCheckBox useProfileCheck = new JCheckBox(Message.get("ColorChooserPanel.UseProfile"));
        useProfileCheck.setSelected(XProp.getBoolean("UseCmykProfile", false));
        useProfileCheck.addActionListener(e -> {
            XProp.put("UseCmykProfile", ((JCheckBox) e.getSource()).isSelected());
            Color color = colorChooser.getColor();
            if (color instanceof NamedColor nc) {
                colorChooser.setColor(NamedColor.buildCmykColor(null, nc.getCyan(), nc.getMagenta(), nc.getYellow(), nc.getBlack()));
            } else {
                colorChooser.setColor(new Color(color.getRGB()));
            }
        });

        JButton exportButton = new JButton(Message.get("ColorChooserPanel.Export"));
        exportButton.addActionListener(e -> {
            String filename = "colors_" + new SimpleDateFormat("yyyyMMdd").format(new Date()) + ".xml";
            FileChooser fc = new FileChooser(ColorChooserPanel.this, Message.get("ColorChooserPanel.ExportTitle"));
            fc.setDirectory(XProp.get("Directory.Color", PlatformUtils.getHomeDir()));
            fc.setFile(filename);
            fc.addFileFilter(ExtFilter.XML);

            if (fc.showSaveDialog() == FileChooser.APPROVE_OPTION) {
                File file = fc.getSelectedFile();
                XProp.put("Directory.Color", file.getParent());
                XmlColorsWriter writer = new XmlColorsWriter(colorModel.toArray());
                if (!writer.write(file.getPath())) {
                    GuiUtils.showError(Message.get("ColorChooserPanel.ExportError"));
                }
            }
        });

        JButton importButton = new JButton(Message.get("ColorChooserPanel.Import"));
        importButton.addActionListener(e -> {
            FileChooser fc = new FileChooser(ColorChooserPanel.this, Message.get("ColorChooserPanel.ImportTitle"));
            fc.setDirectory(XProp.get("Directory.Color", PlatformUtils.getHomeDir()));
            fc.addFileFilter(ExtFilter.XML_);

            if (fc.showOpenDialog() == FileChooser.APPROVE_OPTION) {
                String filename = fc.getSelectedFile().getPath();
                XProp.put("Directory.Color", new File(filename).getParent());
                colorModel.clear();
                colorModel.addAll(new XColorsReader().read(filename));
            }
        });

        nameField = new JTextField(10);

        JPanel panel = new JPanel(new VerticalLayout(5, VerticalLayout.BOTH, VerticalLayout.CENTER));
        panel.add(new JLabel(Message.get("ColorChooserPanel.Name") + " :"));
        panel.add(nameField);
        panel.add(addButton);
        panel.add(javax.swing.Box.createVerticalStrut(10));
        panel.add(removeButton);
        panel.add(defaultButton);

        panel.add(javax.swing.Box.createVerticalStrut(10));
        panel.add(useProfileCheck);
        panel.add(javax.swing.Box.createVerticalStrut(10));
        panel.add(exportButton);
        panel.add(importButton);
        return panel;
    }

    private JComponent buildColorList() {

        colorModel = new DefaultListModel<>();
        colorModel.addAll(colorPalette.getColorList());

        colorList = new JList<>(colorModel);
        colorList.setCellRenderer(new ColorComboRenderer());
        colorList.setVisibleRowCount(6);
        colorList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        colorList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                NamedColor color = colorList.getSelectedValue();
                if (color != null) {
                    colorChooser.setColor(color);
                    nameField.setText(color.getName());
                }
            }
        });

        return new JScrollPane(colorList);
    }

    private void cancelPressed() {
        nccolor = null;
        GuiUtils.saveBounds(this, "ColorChooserPanel");
        tabIndex = tabbedPane.getSelectedIndex();
        this.dispose();
    }

    private void okPressed() {
        colorPalette.setColorList(GuiUtils.modelToList(colorModel));
        GuiUtils.saveBounds(this, "ColorChooserPanel");
        tabIndex = tabbedPane.getSelectedIndex();
        this.dispose();
    }

    public NamedColor getSelectedColor() {
        return nccolor;
    }

    private void addPressed() {

        nccolor = new NamedColor(colorChooser.getColor(), nameField.getText());
        int idx = containsColor(nccolor);
        if (idx >= 0) {
            colorModel.set(idx, nccolor);
        } else {
            colorModel.addElement(nccolor);
        }

        colorPalette.setColorList(new ArrayList(Arrays.asList(colorModel.toArray())));

        GuiUtils.saveBounds(this, "ColorChooserPanel");
        tabIndex = tabbedPane.getSelectedIndex();
        this.dispose();
    }

    private JTabbedPane getTab(Component cmp) {
        if (cmp instanceof JTabbedPane) {
            return (JTabbedPane) cmp;
        }

        if (cmp instanceof Container) {
            Component[] c = ((Container) cmp).getComponents();
            for (Component c1 : c) {
                JTabbedPane tab = getTab(c1);
                if (tab != null) {
                    return tab;
                }
            }
        }
        return null;
    }
}
