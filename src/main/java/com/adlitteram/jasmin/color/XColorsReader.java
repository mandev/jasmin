
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.color;

import java.net.URL;
import java.util.Vector;
import javax.xml.parsers.SAXParserFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.XMLReader;

public class XColorsReader {

    private static final Logger logger = LoggerFactory.getLogger(XColorsReader.class);
    //
    private Vector colorList;

    public XColorsReader() {
        colorList = new Vector();
    }

    public XColorsReader(Vector list) {
        this.colorList = list;
    }

    public Vector read(URL url) {
        return (url == null) ? null : read(url.toString());
    }

    public Vector read(String uri) {

        try {
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            XColorsHandler xh = new XColorsHandler(colorList);
            parser.setContentHandler(xh);
            parser.setErrorHandler(xh);
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);

            // parser.setFeature( "http://apache.org/xml/features/validation/schema", false );
            parser.parse(uri);
        }
        catch (org.xml.sax.SAXParseException spe) {
            logger.warn("", spe);
            return null;
        }
        catch (org.xml.sax.SAXException se) {
            logger.warn("", se);
            return null;
        }
        catch (Exception e) {
            logger.warn("", e);
            return null;
        }
        return colorList;
    }
}
