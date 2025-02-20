package com.adlitteram.jasmin.color;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.IOException;
import java.net.URL;
import java.util.Vector;

public class XColorsReader {

    private static final Logger logger = LoggerFactory.getLogger(XColorsReader.class);

    private final Vector<NamedColor> colorList;

    public XColorsReader() {
        colorList = new Vector<>();
    }

    public XColorsReader(Vector<NamedColor> list) {
        this.colorList = list;
    }

    public Vector<NamedColor> read(URL url) {
        return (url == null) ? null : read(url.toString());
    }

    public Vector<NamedColor> read(String uri) {

        try {
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            XColorsHandler xh = new XColorsHandler(colorList);
            parser.setContentHandler(xh);
            parser.setErrorHandler(xh);
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.parse(uri);
        } catch (SAXException | IOException | ParserConfigurationException se) {
            logger.warn("", se);
        }
        return colorList;
    }
}
