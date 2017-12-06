/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller
 */
package com.adlitteram.jasmin.color;

import com.adlitteram.jasmin.XProp;
import com.adlitteram.jasmin.utils.NumUtils;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Vector;
import javax.swing.DefaultComboBoxModel;
import javax.swing.ListModel;

public class ColorPalette {

    ArrayList colorList;
    ArrayList modelList;

    public ColorPalette() {
        colorList = new ArrayList();
        modelList = new ArrayList();

        for (int i = 0; i < 256; i++) {
            String rgb = XProp.get("ColorRgb." + i);
            String cmyk = XProp.get("ColorCmyk." + i);
            if (rgb == null || cmyk == null) {
                break;
            }
            add(new NamedColor(NumUtils.intValue(rgb), NumUtils.intValue(cmyk), XProp.get("ColorName." + i, "")));
        }

        if (colorList.isEmpty()) {
            for (NamedColor defaultColor : getDefaultColors()) {
                add(defaultColor);
            }
        }
    }

    public static NamedColor[] getDefaultColors() {
        NamedColor[] defaultColors = {
            new NamedColor(new Color(255, 255, 255), "White"),
            new NamedColor(new Color(175, 175, 175), "Medium Gray"),
            new NamedColor(new Color(102, 102, 102), "Dark Gray"),
            new NamedColor(new Color(204, 204, 204), "Light Gray"),
            new NamedColor(new Color(153, 153, 153), "Gray"),
            new NamedColor(new Color(1, 1, 1), "Black"),
            new NamedColor(new Color(227, 225, 201)),
            new NamedColor(new Color(170, 156, 143)),
            new NamedColor(new Color(99, 82, 69)),
            new NamedColor(new Color(244, 210, 187)),
            new NamedColor(new Color(189, 138, 94)),
            new NamedColor(new Color(114, 61, 20)),
            new NamedColor(new Color(252, 209, 157)),
            new NamedColor(new Color(255, 160, 47)),
            new NamedColor(new Color(156, 97, 20)),
            new NamedColor(new Color(255, 245, 185)),
            new NamedColor(new Color(243, 211, 17)),
            new NamedColor(new Color(235, 183, 0)),
            new NamedColor(new Color(200, 229, 154)),
            new NamedColor(new Color(105, 190, 40)),
            new NamedColor(new Color(83, 104, 43)),
            new NamedColor(new Color(219, 247, 227)),
            new NamedColor(new Color(124, 162, 149)),
            new NamedColor(new Color(44, 94, 79)),
            new NamedColor(new Color(237, 247, 245)),
            new NamedColor(new Color(137, 150, 160)),
            new NamedColor(new Color(57, 74, 88)),
            new NamedColor(new Color(228, 246, 251)),
            new NamedColor(new Color(112, 144, 183)),
            new NamedColor(new Color(0, 44, 95)),
            new NamedColor(new Color(242, 235, 250)),
            new NamedColor(new Color(160, 146, 180)),
            new NamedColor(new Color(97, 77, 125)),
            new NamedColor(new Color(241, 219, 223)),
            new NamedColor(new Color(244, 178, 193)),
            new NamedColor(new Color(222, 69, 97)),
            new NamedColor(new Color(233, 197, 203)),
            new NamedColor(new Color(208, 16, 58)),
            new NamedColor(new Color(152, 30, 50))
        };

        return defaultColors;
    }

//    public static NamedColor[] getDefaultColors() {
//        NamedColor[] defaultColors = {
//            new NamedColor(Color.BLACK, "Black"),
//            new NamedColor(Color.white, "White"),
//            new NamedColor(Color.red, "Red"),
//            new NamedColor(Color.green, "Green"),
//            new NamedColor(Color.blue, "Blue"),
//            new NamedColor(Color.cyan, "Cyan"),
//            new NamedColor(Color.magenta, "Magenta"),
//            new NamedColor(Color.yellow, "Yellow"),
//            new NamedColor(Color.orange, "Orange"),
//            new NamedColor(Color.pink, "Pink"),
//            new NamedColor(Color.gray, "Gray"),
//            new NamedColor(Color.darkGray, "Dark Gray"),
//            new NamedColor(Color.lightGray, "Light Gray")};
//
//        return defaultColors;
//    }
    private void add(NamedColor color) {
        if (color == null) {
            return;
        }
        for (Object colorList1 : colorList) {
            if (color.equals(colorList1)) {
                return;
            }
        }
        colorList.add(color);
    }

    public ArrayList getList() {
        return colorList;
    }

    public void setList(ArrayList list) {
        this.colorList = list;

        for (Object modelList1 : modelList) {
            DefaultComboBoxModel model = (DefaultComboBoxModel) modelList1;
            Object obj = model.getSelectedItem();
            model.removeAllElements();
            for (Object value : list) {
                model.addElement(value);
            }
            model.setSelectedItem(obj);
        }
    }

    public DefaultComboBoxModel getComboBoxModel() {
        DefaultComboBoxModel model = new DefaultComboBoxModel(new Vector(colorList));
        modelList.add(model);
        return model;
    }

    public void removeModel(ListModel model) {
        modelList.remove(model);
    }

    public void setToProperties() {
        for (int i = 0; i < colorList.size(); i++) {
            NamedColor color = (NamedColor) colorList.get(i);
            XProp.put("ColorRgb." + i, color.getRGB());
            XProp.put("ColorCmyk." + i, color.getCMYK());
            XProp.put("ColorName." + i, color.getName());
        }

        for (int i = colorList.size();; i++) {
            String colorName = "ColorName." + i;
            if (XProp.get(colorName) == null) {
                break;
            }
            XProp.unsetProperty(colorName);
            XProp.unsetProperty("ColorRgb." + i);
            XProp.unsetProperty("ColorCmyk." + i);
        }
    }
}
