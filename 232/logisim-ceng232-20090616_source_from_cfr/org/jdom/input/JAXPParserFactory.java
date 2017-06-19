/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jdom.JDOMException;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLReader;

class JAXPParserFactory {
    private static final String CVS_ID = "@(#) $RCSfile: JAXPParserFactory.java,v $ $Revision: 1.5 $ $Date: 2004/02/27 21:08:47 $ $Name: jdom_1_0 $";
    private static final String JAXP_SCHEMA_LANGUAGE_PROPERTY = "http://java.sun.com/xml/jaxp/properties/schemaLanguage";
    private static final String JAXP_SCHEMA_LOCATION_PROPERTY = "http://java.sun.com/xml/jaxp/properties/schemaSource";

    private JAXPParserFactory() {
    }

    public static XMLReader createParser(boolean validating, Map features, Map properties) throws JDOMException {
        try {
            SAXParser parser = null;
            SAXParserFactory factory = SAXParserFactory.newInstance();
            factory.setValidating(validating);
            factory.setNamespaceAware(true);
            try {
                parser = factory.newSAXParser();
            }
            catch (ParserConfigurationException e) {
                throw new JDOMException("Could not allocate JAXP SAX Parser", e);
            }
            JAXPParserFactory.setProperty(parser, properties, "http://java.sun.com/xml/jaxp/properties/schemaLanguage");
            JAXPParserFactory.setProperty(parser, properties, "http://java.sun.com/xml/jaxp/properties/schemaSource");
            return parser.getXMLReader();
        }
        catch (SAXException e) {
            throw new JDOMException("Could not allocate JAXP SAX Parser", e);
        }
    }

    private static void setProperty(SAXParser parser, Map properties, String name) throws JDOMException {
        try {
            if (properties.containsKey(name)) {
                parser.setProperty(name, properties.get(name));
            }
        }
        catch (SAXNotSupportedException v0) {
            throw new JDOMException(String.valueOf(name) + " property not supported for JAXP parser " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException v1) {
            throw new JDOMException(String.valueOf(name) + " property not recognized for JAXP parser " + parser.getClass().getName());
        }
    }
}

