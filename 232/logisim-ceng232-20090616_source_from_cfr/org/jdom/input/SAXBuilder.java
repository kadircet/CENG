/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Reader;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.JDOMException;
import org.jdom.JDOMFactory;
import org.jdom.input.BuilderErrorHandler;
import org.jdom.input.JDOMParseException;
import org.jdom.input.SAXHandler;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: SAXBuilder.java,v $ $Revision: 1.89 $ $Date: 2004/09/03 18:24:28 $ $Name: jdom_1_0 $";
    private static final String DEFAULT_SAX_DRIVER = "org.apache.xerces.parsers.SAXParser";
    private boolean validate;
    private boolean expand = true;
    private String saxDriverClass;
    private ErrorHandler saxErrorHandler = null;
    private EntityResolver saxEntityResolver = null;
    private DTDHandler saxDTDHandler = null;
    private XMLFilter saxXMLFilter = null;
    private JDOMFactory factory = new DefaultJDOMFactory();
    private boolean ignoringWhite = false;
    private HashMap features = new HashMap(5);
    private HashMap properties = new HashMap(5);
    private boolean reuseParser = true;
    private XMLReader saxParser = null;
    static /* synthetic */ Class class$java$util$Map;

    public SAXBuilder() {
        this(false);
    }

    public SAXBuilder(String saxDriverClass) {
        this(saxDriverClass, false);
    }

    public SAXBuilder(String saxDriverClass, boolean validate) {
        this.saxDriverClass = saxDriverClass;
        this.validate = validate;
    }

    public SAXBuilder(boolean validate) {
        this.validate = validate;
    }

    public Document build(File file) throws JDOMException, IOException {
        try {
            URL url = SAXBuilder.fileToURL(file);
            return this.build(url);
        }
        catch (MalformedURLException e) {
            throw new JDOMException("Error in building", e);
        }
    }

    public Document build(InputStream in) throws JDOMException, IOException {
        return this.build(new InputSource(in));
    }

    public Document build(InputStream in, String systemId) throws JDOMException, IOException {
        InputSource src = new InputSource(in);
        src.setSystemId(systemId);
        return this.build(src);
    }

    public Document build(Reader characterStream) throws JDOMException, IOException {
        return this.build(new InputSource(characterStream));
    }

    public Document build(Reader characterStream, String systemId) throws JDOMException, IOException {
        InputSource src = new InputSource(characterStream);
        src.setSystemId(systemId);
        return this.build(src);
    }

    public Document build(String systemId) throws JDOMException, IOException {
        return this.build(new InputSource(systemId));
    }

    public Document build(URL url) throws JDOMException, IOException {
        String systemID = url.toExternalForm();
        return this.build(new InputSource(systemID));
    }

    public Document build(InputSource in) throws JDOMException, IOException {
        SAXHandler contentHandler = null;
        try {
            try {
                contentHandler = this.createContentHandler();
                this.configureContentHandler(contentHandler);
                XMLReader parser = this.saxParser;
                if (parser == null) {
                    parser = this.createParser();
                    if (this.saxXMLFilter != null) {
                        XMLFilter root = this.saxXMLFilter;
                        while (root.getParent() instanceof XMLFilter) {
                            root = (XMLFilter)root.getParent();
                        }
                        root.setParent(parser);
                        parser = this.saxXMLFilter;
                    }
                    this.configureParser(parser, contentHandler);
                    if (this.reuseParser) {
                        this.saxParser = parser;
                    }
                } else {
                    this.configureParser(parser, contentHandler);
                }
                parser.parse(in);
                Document document = contentHandler.getDocument();
                Object var5_9 = null;
                contentHandler = null;
                return document;
            }
            catch (SAXParseException e) {
                String systemId;
                Document doc = contentHandler.getDocument();
                if (!doc.hasRootElement()) {
                    doc = null;
                }
                if ((systemId = e.getSystemId()) != null) {
                    throw new JDOMParseException("Error on line " + e.getLineNumber() + " of document " + systemId, e, doc);
                }
                throw new JDOMParseException("Error on line " + e.getLineNumber(), e, doc);
            }
            catch (SAXException e) {
                throw new JDOMParseException("Error in building: " + e.getMessage(), e, contentHandler.getDocument());
            }
        }
        catch (Throwable var4_12) {
            Object var5_10 = null;
            contentHandler = null;
            throw var4_12;
        }
    }

    static /* synthetic */ Class class$(String class$) {
        try {
            return Class.forName(class$);
        }
        catch (ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }

    protected void configureContentHandler(SAXHandler contentHandler) {
        contentHandler.setExpandEntities(this.expand);
        contentHandler.setIgnoringElementContentWhitespace(this.ignoringWhite);
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    protected void configureParser(XMLReader parser, SAXHandler contentHandler) throws JDOMException {
        boolean lexicalReporting;
        parser.setContentHandler(contentHandler);
        if (this.saxEntityResolver != null) {
            parser.setEntityResolver(this.saxEntityResolver);
        }
        if (this.saxDTDHandler != null) {
            parser.setDTDHandler(this.saxDTDHandler);
        } else {
            parser.setDTDHandler(contentHandler);
        }
        if (this.saxErrorHandler != null) {
            parser.setErrorHandler(this.saxErrorHandler);
        } else {
            parser.setErrorHandler(new BuilderErrorHandler());
        }
        lexicalReporting = false;
        try {
            parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", contentHandler);
            lexicalReporting = true;
        }
        catch (SAXNotSupportedException v0) {
        }
        catch (SAXNotRecognizedException v1) {}
        if (!lexicalReporting) {
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", contentHandler);
                lexicalReporting = true;
            }
            catch (SAXNotSupportedException v2) {
            }
            catch (SAXNotRecognizedException v3) {}
        }
        if (this.expand) return;
        try {
            parser.setProperty("http://xml.org/sax/properties/declaration-handler", contentHandler);
            return;
        }
        catch (SAXNotSupportedException v4) {
            return;
        }
        catch (SAXNotRecognizedException v5) {}
    }

    protected SAXHandler createContentHandler() {
        SAXHandler contentHandler = new SAXHandler(this.factory);
        return contentHandler;
    }

    protected XMLReader createParser() throws JDOMException {
        XMLReader parser;
        parser = null;
        if (this.saxDriverClass != null) {
            try {
                parser = XMLReaderFactory.createXMLReader(this.saxDriverClass);
                this.setFeaturesAndProperties(parser, true);
            }
            catch (SAXException e) {
                throw new JDOMException("Could not load " + this.saxDriverClass, e);
            }
        }
        try {
            Class factoryClass = Class.forName("org.jdom.input.JAXPParserFactory");
            Class[] arrclass = new Class[3];
            arrclass[0] = Boolean.TYPE;
            Class class_ = class$java$util$Map != null ? class$java$util$Map : (SAXBuilder.class$java$util$Map = SAXBuilder.class$("java.util.Map"));
            arrclass[1] = class_;
            arrclass[2] = class$java$util$Map != null ? class$java$util$Map : (SAXBuilder.class$java$util$Map = SAXBuilder.class$("java.util.Map"));
            Method createParser = factoryClass.getMethod("createParser", arrclass);
            parser = (XMLReader)createParser.invoke(null, new Boolean(this.validate), this.features, this.properties);
            this.setFeaturesAndProperties(parser, false);
        }
        catch (JDOMException e) {
            throw e;
        }
        catch (NoClassDefFoundError v2) {
        }
        catch (Exception v3) {}
        if (parser == null) {
            try {
                parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
                this.saxDriverClass = parser.getClass().getName();
                this.setFeaturesAndProperties(parser, true);
            }
            catch (SAXException e) {
                throw new JDOMException("Could not load default SAX parser: org.apache.xerces.parsers.SAXParser", e);
            }
        }
        return parser;
    }

    private static URL fileToURL(File file) throws MalformedURLException {
        StringBuffer buffer = new StringBuffer();
        String path = file.getAbsolutePath();
        if (File.separatorChar != '/') {
            path = path.replace(File.separatorChar, '/');
        }
        if (!path.startsWith("/")) {
            buffer.append('/');
        }
        int len = path.length();
        int i = 0;
        while (i < len) {
            char c = path.charAt(i);
            if (c == ' ') {
                buffer.append("%20");
            } else if (c == '#') {
                buffer.append("%23");
            } else if (c == '%') {
                buffer.append("%25");
            } else if (c == '&') {
                buffer.append("%26");
            } else if (c == ';') {
                buffer.append("%3B");
            } else if (c == '<') {
                buffer.append("%3C");
            } else if (c == '=') {
                buffer.append("%3D");
            } else if (c == '>') {
                buffer.append("%3E");
            } else if (c == '?') {
                buffer.append("%3F");
            } else if (c == '~') {
                buffer.append("%7E");
            } else {
                buffer.append(c);
            }
            ++i;
        }
        if (!path.endsWith("/") && file.isDirectory()) {
            buffer.append('/');
        }
        return new URL("file", "", buffer.toString());
    }

    public DTDHandler getDTDHandler() {
        return this.saxDTDHandler;
    }

    public String getDriverClass() {
        return this.saxDriverClass;
    }

    public EntityResolver getEntityResolver() {
        return this.saxEntityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return this.saxErrorHandler;
    }

    public boolean getExpandEntities() {
        return this.expand;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public boolean getIgnoringElementContentWhitespace() {
        return this.ignoringWhite;
    }

    public boolean getReuseParser() {
        return this.reuseParser;
    }

    public boolean getValidation() {
        return this.validate;
    }

    public XMLFilter getXMLFilter() {
        return this.saxXMLFilter;
    }

    private void internalSetFeature(XMLReader parser, String feature, boolean value, String displayName) throws JDOMException {
        try {
            parser.setFeature(feature, value);
        }
        catch (SAXNotSupportedException v0) {
            throw new JDOMException(String.valueOf(displayName) + " feature not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException v1) {
            throw new JDOMException(String.valueOf(displayName) + " feature not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    private void internalSetProperty(XMLReader parser, String property, Object value, String displayName) throws JDOMException {
        try {
            parser.setProperty(property, value);
        }
        catch (SAXNotSupportedException v0) {
            throw new JDOMException(String.valueOf(displayName) + " property not supported for SAX driver " + parser.getClass().getName());
        }
        catch (SAXNotRecognizedException v1) {
            throw new JDOMException(String.valueOf(displayName) + " property not recognized for SAX driver " + parser.getClass().getName());
        }
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.saxDTDHandler = dtdHandler;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.saxEntityResolver = entityResolver;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.saxErrorHandler = errorHandler;
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public void setFeature(String name, boolean value) {
        this.features.put(name, new Boolean(value));
    }

    private void setFeaturesAndProperties(XMLReader parser, boolean coreFeatures) throws JDOMException {
        String name;
        Iterator iter = this.features.keySet().iterator();
        while (iter.hasNext()) {
            name = (String)iter.next();
            Boolean value = (Boolean)this.features.get(name);
            this.internalSetFeature(parser, name, value, name);
        }
        iter = this.properties.keySet().iterator();
        while (iter.hasNext()) {
            name = (String)iter.next();
            this.internalSetProperty(parser, name, this.properties.get(name), name);
        }
        if (coreFeatures) {
            block9 : {
                try {
                    this.internalSetFeature(parser, "http://xml.org/sax/features/validation", this.validate, "Validation");
                }
                catch (JDOMException e) {
                    if (!this.validate) break block9;
                    throw e;
                }
            }
            this.internalSetFeature(parser, "http://xml.org/sax/features/namespaces", true, "Namespaces");
            this.internalSetFeature(parser, "http://xml.org/sax/features/namespace-prefixes", true, "Namespace prefixes");
        }
        try {
            if (parser.getFeature("http://xml.org/sax/features/external-general-entities") != this.expand) {
                parser.setFeature("http://xml.org/sax/features/external-general-entities", this.expand);
            }
        }
        catch (SAXNotRecognizedException v0) {
        }
        catch (SAXNotSupportedException v1) {}
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public void setProperty(String name, Object value) {
        this.properties.put(name, value);
    }

    public void setReuseParser(boolean reuseParser) {
        this.reuseParser = reuseParser;
        this.saxParser = null;
    }

    public void setValidation(boolean validate) {
        this.validate = validate;
    }

    public void setXMLFilter(XMLFilter xmlFilter) {
        this.saxXMLFilter = xmlFilter;
    }
}

