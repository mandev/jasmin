package com.adlitteram.jasmin.color;

import org.znerd.xmlenc.XMLOutputter;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class XmlColorsWriter {

    private final Object[] colorArray;

    public XmlColorsWriter(Object[] colorArray) {
        this.colorArray = colorArray;
    }

    public boolean write(String filename) {

        String encoding = "UTF-8";

        try (FileOutputStream fos = new FileOutputStream(filename);
             OutputStreamWriter ows = new OutputStreamWriter(fos);
             BufferedWriter writer = new BufferedWriter(ows, 1024)) {

            XMLOutputter xmlWriter = new XMLOutputter(writer, encoding);
            xmlWriter.declaration();
            xmlWriter.startTag("colors");

            for (Object colorArray1 : colorArray) {
                NamedColor color = (NamedColor) colorArray1;
                xmlWriter.startTag("color");
                xmlWriter.attribute("name", color.getName());
                xmlWriter.attribute("rgb", String.valueOf(color.getRGB()));
                xmlWriter.attribute("cmyk", String.valueOf(color.getCMYK()));
                xmlWriter.endTag();
            }

            xmlWriter.endTag();
            xmlWriter.endDocument();
            return true;
        } catch (IOException ex) {
            return false;
        }
    }
}
