package com.adlitteram.jasmin.property;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParserFactory;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.Properties;

public class XPropertiesReader {
    private XPropertiesReader() {
    }

    private static final Logger logger = LoggerFactory.getLogger(XPropertiesReader.class);

    private static final SAXParserFactory parserFactory = SAXParserFactory.newInstance();

    public static boolean read(Properties props, String filename) {
        return read(props, new File(filename).toURI());
    }

    public static boolean read(Properties props, URI uri) {
        try {
            XMLReader parser = parserFactory.newSAXParser().getXMLReader();
            XPropertiesHandler xh = new XPropertiesHandler(props);
            parser.setContentHandler(xh);
            parser.setErrorHandler(xh);
            parser.setFeature("http://xml.org/sax/features/validation", false);
            parser.setFeature("http://xml.org/sax/features/namespaces", false);
            parser.setFeature("http://apache.org/xml/features/validation/schema", false);
            parser.parse(uri.toString());
        } catch (SAXException | IOException | ParserConfigurationException se) {
            logger.warn("", se);
            return false;
        }
        return true;
    }
}
