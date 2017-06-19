/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.output;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.output.JDOMLocator;
import org.jdom.output.NamespaceStack;
import org.jdom.output.XMLOutputter;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.EntityResolver;
import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.SAXParseException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;
import org.xml.sax.helpers.DefaultHandler;
import org.xml.sax.helpers.XMLReaderFactory;

public class SAXOutputter {
    private static final String CVS_ID = "@(#) $RCSfile: SAXOutputter.java,v $ $Revision: 1.37 $ $Date: 2004/09/03 06:11:00 $ $Name: jdom_1_0 $";
    private static final String NAMESPACES_SAX_FEATURE = "http://xml.org/sax/features/namespaces";
    private static final String NS_PREFIXES_SAX_FEATURE = "http://xml.org/sax/features/namespace-prefixes";
    private static final String VALIDATION_SAX_FEATURE = "http://xml.org/sax/features/validation";
    private static final String LEXICAL_HANDLER_SAX_PROPERTY = "http://xml.org/sax/properties/lexical-handler";
    private static final String DECL_HANDLER_SAX_PROPERTY = "http://xml.org/sax/properties/declaration-handler";
    private static final String LEXICAL_HANDLER_ALT_PROPERTY = "http://xml.org/sax/handlers/LexicalHandler";
    private static final String DECL_HANDLER_ALT_PROPERTY = "http://xml.org/sax/handlers/DeclHandler";
    private static final String[] attrTypeToNameMap = new String[]{"CDATA", "CDATA", "ID", "IDREF", "IDREFS", "ENTITY", "ENTITIES", "NMTOKEN", "NMTOKENS", "NOTATION", "NMTOKEN"};
    private ContentHandler contentHandler;
    private ErrorHandler errorHandler;
    private DTDHandler dtdHandler;
    private EntityResolver entityResolver;
    private LexicalHandler lexicalHandler;
    private DeclHandler declHandler;
    private boolean declareNamespaces = false;
    private boolean reportDtdEvents = true;
    private JDOMLocator locator = null;

    public SAXOutputter() {
    }

    public SAXOutputter(ContentHandler contentHandler) {
        this(contentHandler, null, null, null, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver) {
        this(contentHandler, errorHandler, dtdHandler, entityResolver, null);
    }

    public SAXOutputter(ContentHandler contentHandler, ErrorHandler errorHandler, DTDHandler dtdHandler, EntityResolver entityResolver, LexicalHandler lexicalHandler) {
        this.contentHandler = contentHandler;
        this.errorHandler = errorHandler;
        this.dtdHandler = dtdHandler;
        this.entityResolver = entityResolver;
        this.lexicalHandler = lexicalHandler;
    }

    private AttributesImpl addNsAttribute(AttributesImpl atts, Namespace ns) {
        if (this.declareNamespaces) {
            if (atts == null) {
                atts = new AttributesImpl();
            }
            atts.addAttribute("", "", "xmlns:" + ns.getPrefix(), "CDATA", ns.getURI());
        }
        return atts;
    }

    private void cdata(String cdataText) throws JDOMException {
        try {
            if (this.lexicalHandler != null) {
                this.lexicalHandler.startCDATA();
                this.characters(cdataText);
                this.lexicalHandler.endCDATA();
            } else {
                this.characters(cdataText);
            }
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in CDATA", se);
        }
    }

    private void characters(String elementText) throws JDOMException {
        char[] c = elementText.toCharArray();
        try {
            this.contentHandler.characters(c, 0, c.length);
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in characters", se);
        }
    }

    private void comment(String commentText) throws JDOMException {
        if (this.lexicalHandler != null) {
            char[] c = commentText.toCharArray();
            try {
                this.lexicalHandler.comment(c, 0, c.length);
            }
            catch (SAXException se) {
                throw new JDOMException("Exception in comment", se);
            }
        }
    }

    private XMLReader createDTDParser() throws JDOMException {
        XMLReader parser = null;
        try {
            parser = this.createParser();
        }
        catch (Exception ex1) {
            throw new JDOMException("Error in SAX parser allocation", ex1);
        }
        if (this.getDTDHandler() != null) {
            parser.setDTDHandler(this.getDTDHandler());
        }
        if (this.getEntityResolver() != null) {
            parser.setEntityResolver(this.getEntityResolver());
        }
        if (this.getLexicalHandler() != null) {
            try {
                parser.setProperty("http://xml.org/sax/properties/lexical-handler", this.getLexicalHandler());
            }
            catch (SAXException v0) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/LexicalHandler", this.getLexicalHandler());
                }
                catch (SAXException v1) {}
            }
        }
        if (this.getDeclHandler() != null) {
            try {
                parser.setProperty("http://xml.org/sax/properties/declaration-handler", this.getDeclHandler());
            }
            catch (SAXException v2) {
                try {
                    parser.setProperty("http://xml.org/sax/handlers/DeclHandler", this.getDeclHandler());
                }
                catch (SAXException v3) {}
            }
        }
        parser.setErrorHandler(new DefaultHandler());
        return parser;
    }

    protected XMLReader createParser() throws Exception {
        XMLReader parser;
        parser = null;
        try {
            Class factoryClass = Class.forName("javax.xml.parsers.SAXParserFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method newSAXParser = factoryClass.getMethod("newSAXParser", null);
            Object jaxpParser = newSAXParser.invoke(factory, null);
            Class parserClass = jaxpParser.getClass();
            Method getXMLReader = parserClass.getMethod("getXMLReader", null);
            parser = (XMLReader)getXMLReader.invoke(jaxpParser, null);
        }
        catch (ClassNotFoundException v0) {
        }
        catch (InvocationTargetException v1) {
        }
        catch (NoSuchMethodException v2) {
        }
        catch (IllegalAccessException v3) {}
        if (parser == null) {
            parser = XMLReaderFactory.createXMLReader("org.apache.xerces.parsers.SAXParser");
        }
        return parser;
    }

    private void documentLocator(Document document) {
        DocType docType;
        this.locator = new JDOMLocator();
        String publicID = null;
        String systemID = null;
        if (document != null && (docType = document.getDocType()) != null) {
            publicID = docType.getPublicID();
            systemID = docType.getSystemID();
        }
        this.locator.setPublicId(publicID);
        this.locator.setSystemId(systemID);
        this.locator.setLineNumber(-1);
        this.locator.setColumnNumber(-1);
        this.contentHandler.setDocumentLocator(this.locator);
    }

    private void dtdEvents(Document document) throws JDOMException {
        DocType docType = document.getDocType();
        if (docType != null && (this.dtdHandler != null || this.declHandler != null)) {
            String dtdDoc = new XMLOutputter().outputString(docType);
            try {
                this.createDTDParser().parse(new InputSource(new StringReader(dtdDoc)));
            }
            catch (SAXParseException v0) {
            }
            catch (SAXException e) {
                throw new JDOMException("DTD parsing error", e);
            }
            catch (IOException e) {
                throw new JDOMException("DTD parsing error", e);
            }
        }
    }

    private void element(Element element, NamespaceStack namespaces) throws JDOMException {
        int previouslyDeclaredNamespaces = namespaces.size();
        Attributes nsAtts = this.startPrefixMapping(element, namespaces);
        this.startElement(element, nsAtts);
        this.elementContent(element.getContent(), namespaces);
        this.locator.setNode(element);
        this.endElement(element);
        this.endPrefixMapping(namespaces, previouslyDeclaredNamespaces);
    }

    private void elementContent(List content, NamespaceStack namespaces) throws JDOMException {
        Iterator i = content.iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            if (obj instanceof Content) {
                this.elementContent((Content)obj, namespaces);
                continue;
            }
            this.handleError(new JDOMException("Invalid element content: " + obj));
        }
    }

    private void elementContent(Content node, NamespaceStack namespaces) throws JDOMException {
        this.locator.setNode(node);
        if (node instanceof Element) {
            this.element((Element)node, namespaces);
        } else if (node instanceof CDATA) {
            this.cdata(((CDATA)node).getText());
        } else if (node instanceof Text) {
            this.characters(((Text)node).getText());
        } else if (node instanceof ProcessingInstruction) {
            this.processingInstruction((ProcessingInstruction)node);
        } else if (node instanceof Comment) {
            this.comment(((Comment)node).getText());
        } else if (node instanceof EntityRef) {
            this.entityRef((EntityRef)node);
        } else {
            this.handleError(new JDOMException("Invalid element content: " + node));
        }
    }

    private void endDocument() throws JDOMException {
        try {
            this.contentHandler.endDocument();
            this.locator = null;
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in endDocument", se);
        }
    }

    private void endElement(Element element) throws JDOMException {
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getName();
        String rawName = element.getQualifiedName();
        try {
            this.contentHandler.endElement(namespaceURI, localName, rawName);
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in endElement", se);
        }
    }

    private void endPrefixMapping(NamespaceStack namespaces, int previouslyDeclaredNamespaces) throws JDOMException {
        while (namespaces.size() > previouslyDeclaredNamespaces) {
            String prefix = namespaces.pop();
            try {
                this.contentHandler.endPrefixMapping(prefix);
                continue;
            }
            catch (SAXException se) {
                throw new JDOMException("Exception in endPrefixMapping", se);
            }
        }
    }

    private void entityRef(EntityRef entity) throws JDOMException {
        if (entity != null) {
            try {
                this.contentHandler.skippedEntity(entity.getName());
            }
            catch (SAXException se) {
                throw new JDOMException("Exception in entityRef", se);
            }
        }
    }

    private static String getAttributeTypeName(int type) {
        if (type < 0 || type >= attrTypeToNameMap.length) {
            type = 0;
        }
        return attrTypeToNameMap[type];
    }

    public ContentHandler getContentHandler() {
        return this.contentHandler;
    }

    public DTDHandler getDTDHandler() {
        return this.dtdHandler;
    }

    public DeclHandler getDeclHandler() {
        return this.declHandler;
    }

    public EntityResolver getEntityResolver() {
        return this.entityResolver;
    }

    public ErrorHandler getErrorHandler() {
        return this.errorHandler;
    }

    public boolean getFeature(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            return this.declareNamespaces;
        }
        if ("http://xml.org/sax/features/namespaces".equals(name)) {
            return true;
        }
        if ("http://xml.org/sax/features/validation".equals(name)) {
            return this.reportDtdEvents;
        }
        throw new SAXNotRecognizedException(name);
    }

    public LexicalHandler getLexicalHandler() {
        return this.lexicalHandler;
    }

    public JDOMLocator getLocator() {
        return this.locator != null ? new JDOMLocator(this.locator) : null;
    }

    public Object getProperty(String name) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name)) {
            return this.getLexicalHandler();
        }
        if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name)) {
            return this.getDeclHandler();
        }
        throw new SAXNotRecognizedException(name);
    }

    public boolean getReportDTDEvents() {
        return this.reportDtdEvents;
    }

    public boolean getReportNamespaceDeclarations() {
        return this.declareNamespaces;
    }

    private void handleError(JDOMException exception) throws JDOMException {
        if (this.errorHandler != null) {
            try {
                this.errorHandler.error(new SAXParseException(exception.getMessage(), null, exception));
            }
            catch (SAXException se) {
                if (se.getException() instanceof JDOMException) {
                    throw (JDOMException)se.getException();
                }
                throw new JDOMException(se.getMessage(), se);
            }
        } else {
            throw exception;
        }
    }

    public void output(List nodes) throws JDOMException {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        this.documentLocator(null);
        this.startDocument();
        this.elementContent(nodes, new NamespaceStack());
        this.endDocument();
    }

    public void output(Document document) throws JDOMException {
        if (document == null) {
            return;
        }
        this.documentLocator(document);
        this.startDocument();
        if (this.reportDtdEvents) {
            this.dtdEvents(document);
        }
        Iterator i = document.getContent().iterator();
        while (i.hasNext()) {
            Object obj = i.next();
            this.locator.setNode(obj);
            if (obj instanceof Element) {
                this.element(document.getRootElement(), new NamespaceStack());
                continue;
            }
            if (obj instanceof ProcessingInstruction) {
                this.processingInstruction((ProcessingInstruction)obj);
                continue;
            }
            if (!(obj instanceof Comment)) continue;
            this.comment(((Comment)obj).getText());
        }
        this.endDocument();
    }

    public void output(Element node) throws JDOMException {
        if (node == null) {
            return;
        }
        this.documentLocator(null);
        this.startDocument();
        this.elementContent(node, new NamespaceStack());
        this.endDocument();
    }

    public void outputFragment(List nodes) throws JDOMException {
        if (nodes == null || nodes.size() == 0) {
            return;
        }
        this.elementContent(nodes, new NamespaceStack());
    }

    public void outputFragment(Content node) throws JDOMException {
        if (node == null) {
            return;
        }
        this.elementContent(node, new NamespaceStack());
    }

    private void processingInstruction(ProcessingInstruction pi) throws JDOMException {
        if (pi != null) {
            String target = pi.getTarget();
            String data = pi.getData();
            try {
                this.contentHandler.processingInstruction(target, data);
            }
            catch (SAXException se) {
                throw new JDOMException("Exception in processingInstruction", se);
            }
        }
    }

    public void setContentHandler(ContentHandler contentHandler) {
        this.contentHandler = contentHandler;
    }

    public void setDTDHandler(DTDHandler dtdHandler) {
        this.dtdHandler = dtdHandler;
    }

    public void setDeclHandler(DeclHandler declHandler) {
        this.declHandler = declHandler;
    }

    public void setEntityResolver(EntityResolver entityResolver) {
        this.entityResolver = entityResolver;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    public void setFeature(String name, boolean value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/features/namespace-prefixes".equals(name)) {
            this.setReportNamespaceDeclarations(value);
        } else if ("http://xml.org/sax/features/namespaces".equals(name)) {
            if (!value) {
                throw new SAXNotSupportedException(name);
            }
        } else if ("http://xml.org/sax/features/validation".equals(name)) {
            this.setReportDTDEvents(value);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public void setLexicalHandler(LexicalHandler lexicalHandler) {
        this.lexicalHandler = lexicalHandler;
    }

    public void setProperty(String name, Object value) throws SAXNotRecognizedException, SAXNotSupportedException {
        if ("http://xml.org/sax/properties/lexical-handler".equals(name) || "http://xml.org/sax/handlers/LexicalHandler".equals(name)) {
            this.setLexicalHandler((LexicalHandler)value);
        } else if ("http://xml.org/sax/properties/declaration-handler".equals(name) || "http://xml.org/sax/handlers/DeclHandler".equals(name)) {
            this.setDeclHandler((DeclHandler)value);
        } else {
            throw new SAXNotRecognizedException(name);
        }
    }

    public void setReportDTDEvents(boolean reportDtdEvents) {
        this.reportDtdEvents = reportDtdEvents;
    }

    public void setReportNamespaceDeclarations(boolean declareNamespaces) {
        this.declareNamespaces = declareNamespaces;
    }

    private void startDocument() throws JDOMException {
        try {
            this.contentHandler.startDocument();
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in startDocument", se);
        }
    }

    private void startElement(Element element, Attributes nsAtts) throws JDOMException {
        String namespaceURI = element.getNamespaceURI();
        String localName = element.getName();
        String rawName = element.getQualifiedName();
        AttributesImpl atts = nsAtts != null ? new AttributesImpl(nsAtts) : new AttributesImpl();
        List attributes = element.getAttributes();
        Iterator i = attributes.iterator();
        while (i.hasNext()) {
            Attribute a = (Attribute)i.next();
            atts.addAttribute(a.getNamespaceURI(), a.getName(), a.getQualifiedName(), SAXOutputter.getAttributeTypeName(a.getAttributeType()), a.getValue());
        }
        try {
            this.contentHandler.startElement(namespaceURI, localName, rawName, atts);
        }
        catch (SAXException se) {
            throw new JDOMException("Exception in startElement", se);
        }
    }

    private Attributes startPrefixMapping(Element element, NamespaceStack namespaces) throws JDOMException {
        List additionalNamespaces;
        AttributesImpl nsAtts = null;
        Namespace ns = element.getNamespace();
        if (ns != Namespace.XML_NAMESPACE) {
            String prefix = ns.getPrefix();
            String uri = namespaces.getURI(prefix);
            if (!ns.getURI().equals(uri)) {
                namespaces.push(ns);
                nsAtts = this.addNsAttribute(nsAtts, ns);
                try {
                    this.contentHandler.startPrefixMapping(prefix, ns.getURI());
                }
                catch (SAXException se) {
                    throw new JDOMException("Exception in startPrefixMapping", se);
                }
            }
        }
        if ((additionalNamespaces = element.getAdditionalNamespaces()) != null) {
            Iterator itr = additionalNamespaces.iterator();
            while (itr.hasNext()) {
                ns = (Namespace)itr.next();
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (ns.getURI().equals(uri)) continue;
                namespaces.push(ns);
                nsAtts = this.addNsAttribute(nsAtts, ns);
                try {
                    this.contentHandler.startPrefixMapping(prefix, ns.getURI());
                    continue;
                }
                catch (SAXException se) {
                    throw new JDOMException("Exception in startPrefixMapping", se);
                }
            }
        }
        return nsAtts;
    }
}

