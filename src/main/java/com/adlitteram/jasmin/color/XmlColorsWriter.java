/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller
 */
package com.adlitteram.jasmin.color;

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import org.apache.commons.io.IOUtils;
import org.znerd.xmlenc.XMLOutputter;

public class XmlColorsWriter {

    private final Object[] colorArray;

    public XmlColorsWriter(Object[] colorArray) {
        this.colorArray = colorArray;
    }

    public boolean write(String filename) {

        String encoding = "UTF-8";
        Writer writer = null;

        try {

            writer = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(filename)), 1024);
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
            writer.close();
            return true;
        } catch (IOException ex) {
            IOUtils.closeQuietly(writer);
            return false;
        }
    }
}
