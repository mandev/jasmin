
/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller  */
package com.adlitteram.jasmin.property;

import org.slf4j.LoggerFactory;
import java.util.Properties;
import org.slf4j.Logger;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class XPropertiesHandler extends DefaultHandler {

    private static final Logger logger = LoggerFactory.getLogger(XPropertiesHandler.class);
    //
    private Properties props;

    public XPropertiesHandler(Properties props) {
        this.props = props;
    }

    @Override
    public void startElement(String uri, String local, String raw, Attributes attrs) {
        if ("property".equalsIgnoreCase(raw)) {
            props.put(attrs.getValue("key"), attrs.getValue("value"));
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
        StringBuilder str = new StringBuilder();

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
