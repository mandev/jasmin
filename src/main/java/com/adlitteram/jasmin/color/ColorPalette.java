package com.adlitteram.jasmin.color;

import com.adlitteram.jasmin.property.XProp;
import com.adlitteram.jasmin.utils.NumUtils;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class ColorPalette {

    private List<NamedColor> colorList;
    private final List<DefaultComboBoxModel<NamedColor>> modelList;

    public ColorPalette() {
        colorList = new ArrayList<>();
        modelList = new ArrayList<>();

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
        return new NamedColor[]{
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
    }

    private void add(NamedColor color) {
        if (color != null && !colorList.contains(color)) {
            colorList.add(color);
        }
    }

    public List<NamedColor> getColorList() {
        return colorList;
    }

    public void setColorList(List<NamedColor> list) {
        this.colorList = list;

        modelList.forEach(model -> {
            Object obj = model.getSelectedItem();
            model.removeAllElements();
            list.forEach(model::addElement);
            model.setSelectedItem(obj);
        });
    }

    public DefaultComboBoxModel<NamedColor> getComboBoxModel() {
        DefaultComboBoxModel<NamedColor> model = new DefaultComboBoxModel<>(new Vector<>(colorList));
        modelList.add(model);
        return model;
    }

    public void removeModel(ListModel<NamedColor> model) {
        modelList.remove(model);
    }

    public void setToProperties() {
        for (int i = 0; i < colorList.size(); i++) {
            NamedColor color = colorList.get(i);
            XProp.put("ColorRgb." + i, color.getRGB());
            XProp.put("ColorCmyk." + i, color.getCMYK());
            XProp.put("ColorName." + i, color.getName());
        }

        for (int i = colorList.size(); ; i++) {
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
