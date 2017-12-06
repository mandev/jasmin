/**
 * Copyright (C) 1999-2002 Emmanuel Deviller
 *
 * @version 1.0
 * @author Emmanuel Deviller
 */
package com.adlitteram.jasmin.property;

import org.slf4j.LoggerFactory;
import java.io.File;
import java.net.URI;
import java.util.Properties;
import org.slf4j.Logger;
import javax.xml.parsers.SAXParserFactory;
import org.xml.sax.XMLReader;

//static final String DEFAULT_PARSER_NAME = "javax.xml.parsers.SAXParser";
//XMLReader parser = (XMLReader)Class.forName(DEFAULT_PARSER_NAME).newInstance();
//static final String DEFAULT_PARSER_NAME = "org.apache.xerces.parsers.SAXParser";
//parser.setFeature("http://apache.org/xml/features/continue-after-fatal-error", false);
public class XPropertiesReader {

    private static final Logger logger = LoggerFactory.getLogger(XPropertiesReader.class);

    public static boolean read(Properties props, String filename) {
        return read(props, new File(filename).toURI());
    }

    public static boolean read(Properties props, URI uri) {
        try {
            XMLReader parser = SAXParserFactory.newInstance().newSAXParser().getXMLReader();
            XPropertiesHandler xh = new XPropertiesHandler(props);
            parser.setContentHandler(xh);
            parser.setErrorHandler(xh);
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://apache.org/xml/features/validation/schema", false);
            parser.parse(uri.toString());
        }
        catch (org.xml.sax.SAXParseException spe) {
            logger.warn("", spe);
            return false;
        }
        catch (org.xml.sax.SAXException se) {
            logger.warn("", se);
            return false;
        }
        catch (Exception e) {
            logger.warn("", e);
            return false;
        }
        return true;
    }
}
