
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.color;

import org.slf4j.LoggerFactory;
import java.util.Vector;
import java.util.logging.Level;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XColorsHandler extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(XColorsHandler.class);
    //
    private Vector colorList;

    public XColorsHandler(Vector colorList) {
        this.colorList = colorList;
    }

    @Override
    public void startElement(String uri, String local, String raw, Attributes attrs) {
        if (raw.equalsIgnoreCase("color")) {
            int rgb = Integer.parseInt(attrs.getValue("rgb"));
            int cmyk = Integer.parseInt(attrs.getValue("cmyk"));
            colorList.add(new NamedColor(rgb, cmyk, attrs.getValue("name")));
        }
    }

    @Override
    public void warning(SAXParseException ex) {
        logger.warn( getLocationString(ex), ex);
    }

    @Override
    public void error(SAXParseException ex) {
        logger.warn( getLocationString(ex), ex);
    }

    @Override
    public void fatalError(SAXParseException ex) throws SAXException {
        logger.warn( getLocationString(ex), ex);
    }

    // Returns a string of the location.
    private String getLocationString(SAXParseException ex) {
        StringBuffer str = new StringBuffer();

        String systemId = ex.getSystemId();
        if (systemId != null) {
            int index = systemId.lastIndexOf('/');
            if (index != -1) systemId = systemId.substring(index + 1);
            str.append(systemId);
        }
        str.append(':').append(ex.getLineNumber());
        str.append(':').append(ex.getColumnNumber());
        return str.toString();
    }
}
