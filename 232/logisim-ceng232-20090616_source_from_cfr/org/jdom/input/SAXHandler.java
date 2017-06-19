/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.DefaultJDOMFactory;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.input.TextBuffer;
import org.xml.sax.Attributes;
import org.xml.sax.DTDHandler;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.DeclHandler;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class SAXHandler
extends DefaultHandler
implements LexicalHandler,
DeclHandler,
DTDHandler {
    private static final String CVS_ID = "@(#) $RCSfile: SAXHandler.java,v $ $Revision: 1.68 $ $Date: 2004/08/31 06:14:05 $ $Name: jdom_1_0 $";
    private static final Map attrNameToTypeMap = new HashMap(13);
    private Document document;
    private Element currentElement;
    private boolean atRoot;
    private boolean inDTD = false;
    private boolean inInternalSubset = false;
    private boolean previousCDATA = false;
    private boolean inCDATA = false;
    private boolean expand = true;
    private boolean suppress = false;
    private int entityDepth = 0;
    private List declaredNamespaces;
    private StringBuffer internalSubset = new StringBuffer();
    private TextBuffer textBuffer = new TextBuffer();
    private Map externalEntities;
    private JDOMFactory factory;
    private boolean ignoringWhite = false;
    private Locator locator;

    static {
        attrNameToTypeMap.put("CDATA", new Integer(1));
        attrNameToTypeMap.put("ID", new Integer(2));
        attrNameToTypeMap.put("IDREF", new Integer(3));
        attrNameToTypeMap.put("IDREFS", new Integer(4));
        attrNameToTypeMap.put("ENTITY", new Integer(5));
        attrNameToTypeMap.put("ENTITIES", new Integer(6));
        attrNameToTypeMap.put("NMTOKEN", new Integer(7));
        attrNameToTypeMap.put("NMTOKENS", new Integer(8));
        attrNameToTypeMap.put("NOTATION", new Integer(9));
        attrNameToTypeMap.put("ENUMERATION", new Integer(10));
    }

    public SAXHandler() {
        this(null);
    }

    public SAXHandler(JDOMFactory factory) {
        this.factory = factory != null ? factory : new DefaultJDOMFactory();
        this.atRoot = true;
        this.declaredNamespaces = new ArrayList();
        this.externalEntities = new HashMap();
        this.document = this.factory.document(null);
    }

    private void appendExternalId(String publicID, String systemID) {
        if (publicID != null) {
            this.internalSubset.append(" PUBLIC \"").append(publicID).append('\"');
        }
        if (systemID != null) {
            if (publicID == null) {
                this.internalSubset.append(" SYSTEM ");
            } else {
                this.internalSubset.append(' ');
            }
            this.internalSubset.append('\"').append(systemID).append('\"');
        }
    }

    public void attributeDecl(String eName, String aName, String type, String valueDefault, String value) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ATTLIST ").append(eName).append(' ').append(aName).append(' ').append(type).append(' ');
        if (valueDefault != null) {
            this.internalSubset.append(valueDefault);
        } else {
            this.internalSubset.append('\"').append(value).append('\"');
        }
        if (valueDefault != null && valueDefault.equals("#FIXED")) {
            this.internalSubset.append(" \"").append(value).append('\"');
        }
        this.internalSubset.append(">\n");
    }

    public void characters(char[] ch, int start, int length) throws SAXException {
        if (this.suppress || length == 0) {
            return;
        }
        if (this.previousCDATA != this.inCDATA) {
            this.flushCharacters();
        }
        this.textBuffer.append(ch, start, length);
    }

    public void comment(char[] ch, int start, int length) throws SAXException {
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        String commentText = new String(ch, start, length);
        if (this.inDTD && this.inInternalSubset && !this.expand) {
            this.internalSubset.append("  <!--").append(commentText).append("-->\n");
            return;
        }
        if (!this.inDTD && !commentText.equals("")) {
            if (this.atRoot) {
                this.factory.addContent(this.document, this.factory.comment(commentText));
            } else {
                this.factory.addContent(this.getCurrentElement(), this.factory.comment(commentText));
            }
        }
    }

    public void elementDecl(String name, String model) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ELEMENT ").append(name).append(' ').append(model).append(">\n");
    }

    public void endCDATA() throws SAXException {
        if (this.suppress) {
            return;
        }
        this.previousCDATA = true;
        this.inCDATA = false;
    }

    public void endDTD() throws SAXException {
        this.document.getDocType().setInternalSubset(this.internalSubset.toString());
        this.inDTD = false;
        this.inInternalSubset = false;
    }

    public void endElement(String namespaceURI, String localName, String qName) throws SAXException {
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        if (!this.atRoot) {
            Parent p = this.currentElement.getParent();
            if (p instanceof Document) {
                this.atRoot = true;
            } else {
                this.currentElement = (Element)p;
            }
        } else {
            throw new SAXException("Ill-formed XML document (missing opening tag for " + localName + ")");
        }
    }

    public void endEntity(String name) throws SAXException {
        --this.entityDepth;
        if (this.entityDepth == 0) {
            this.suppress = false;
        }
        if (name.equals("[dtd]")) {
            this.inInternalSubset = true;
        }
    }

    public void externalEntityDecl(String name, String publicID, String systemID) throws SAXException {
        this.externalEntities.put(name, new String[]{publicID, systemID});
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(">\n");
    }

    protected void flushCharacters() throws SAXException {
        this.flushCharacters(this.textBuffer.toString());
        this.textBuffer.clear();
    }

    protected void flushCharacters(String data) throws SAXException {
        if (data.length() == 0) {
            this.previousCDATA = this.inCDATA;
            return;
        }
        if (this.previousCDATA) {
            this.factory.addContent(this.getCurrentElement(), this.factory.cdata(data));
        } else {
            this.factory.addContent(this.getCurrentElement(), this.factory.text(data));
        }
        this.previousCDATA = this.inCDATA;
    }

    private static int getAttributeType(String typeName) {
        Integer type = (Integer)attrNameToTypeMap.get(typeName);
        if (type == null) {
            if (typeName != null && typeName.length() > 0 && typeName.charAt(0) == '(') {
                return 10;
            }
            return 0;
        }
        return type;
    }

    public Element getCurrentElement() throws SAXException {
        if (this.currentElement == null) {
            throw new SAXException("Ill-formed XML document (multiple root elements detected)");
        }
        return this.currentElement;
    }

    public Document getDocument() {
        return this.document;
    }

    public Locator getDocumentLocator() {
        return this.locator;
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

    public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
        if (!this.ignoringWhite) {
            this.characters(ch, start, length);
        }
    }

    public void internalEntityDecl(String name, String value) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ");
        if (name.startsWith("%")) {
            this.internalSubset.append("% ").append(name.substring(1));
        } else {
            this.internalSubset.append(name);
        }
        this.internalSubset.append(" \"").append(value).append("\">\n");
    }

    public void notationDecl(String name, String publicID, String systemID) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!NOTATION ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(">\n");
    }

    public void processingInstruction(String target, String data) throws SAXException {
        if (this.suppress) {
            return;
        }
        this.flushCharacters();
        if (this.atRoot) {
            this.factory.addContent(this.document, this.factory.processingInstruction(target, data));
        } else {
            this.factory.addContent(this.getCurrentElement(), this.factory.processingInstruction(target, data));
        }
    }

    protected void pushElement(Element element) {
        if (this.atRoot) {
            this.document.setRootElement(element);
            this.atRoot = false;
        } else {
            this.factory.addContent(this.currentElement, element);
        }
        this.currentElement = element;
    }

    public void setDocumentLocator(Locator locator) {
        this.locator = locator;
    }

    public void setExpandEntities(boolean expand) {
        this.expand = expand;
    }

    public void setIgnoringElementContentWhitespace(boolean ignoringWhite) {
        this.ignoringWhite = ignoringWhite;
    }

    public void skippedEntity(String name) throws SAXException {
        if (name.startsWith("%")) {
            return;
        }
        this.flushCharacters();
        this.factory.addContent(this.getCurrentElement(), this.factory.entityRef(name));
    }

    public void startCDATA() throws SAXException {
        if (this.suppress) {
            return;
        }
        this.inCDATA = true;
    }

    public void startDTD(String name, String publicID, String systemID) throws SAXException {
        this.flushCharacters();
        this.factory.addContent(this.document, this.factory.docType(name, publicID, systemID));
        this.inDTD = true;
        this.inInternalSubset = true;
    }

    public void startDocument() {
        if (this.locator != null) {
            this.document.setBaseURI(this.locator.getSystemId());
        }
    }

    public void startElement(String namespaceURI, String localName, String qName, Attributes atts) throws SAXException {
        if (this.suppress) {
            return;
        }
        Element element = null;
        if (namespaceURI != null && !namespaceURI.equals("")) {
            String prefix = "";
            if (!qName.equals(localName)) {
                int split = qName.indexOf(":");
                prefix = qName.substring(0, split);
            }
            Namespace elementNamespace = Namespace.getNamespace(prefix, namespaceURI);
            element = this.factory.element(localName, elementNamespace);
        } else {
            element = this.factory.element(localName);
        }
        if (this.declaredNamespaces.size() > 0) {
            this.transferNamespaces(element);
        }
        int i = 0;
        int len = atts.getLength();
        while (i < len) {
            Attribute attribute = null;
            String attLocalName = atts.getLocalName(i);
            String attQName = atts.getQName(i);
            int attType = SAXHandler.getAttributeType(atts.getType(i));
            if (!attQName.startsWith("xmlns:") && !attQName.equals("xmlns")) {
                if (!attQName.equals(attLocalName)) {
                    String attPrefix = attQName.substring(0, attQName.indexOf(":"));
                    Namespace attNs = Namespace.getNamespace(attPrefix, atts.getURI(i));
                    attribute = this.factory.attribute(attLocalName, atts.getValue(i), attType, attNs);
                } else {
                    attribute = this.factory.attribute(attLocalName, atts.getValue(i), attType);
                }
                this.factory.setAttribute(element, attribute);
            }
            ++i;
        }
        this.flushCharacters();
        if (this.atRoot) {
            this.document.setRootElement(element);
            this.atRoot = false;
        } else {
            this.factory.addContent(this.getCurrentElement(), element);
        }
        this.currentElement = element;
    }

    public void startEntity(String name) throws SAXException {
        ++this.entityDepth;
        if (this.expand || this.entityDepth > 1) {
            return;
        }
        if (name.equals("[dtd]")) {
            this.inInternalSubset = false;
            return;
        }
        if (!(this.inDTD || name.equals("amp") || name.equals("lt") || name.equals("gt") || name.equals("apos") || name.equals("quot") || this.expand)) {
            String pub = null;
            String sys = null;
            String[] ids = (String[])this.externalEntities.get(name);
            if (ids != null) {
                pub = ids[0];
                sys = ids[1];
            }
            if (!this.atRoot) {
                this.flushCharacters();
                EntityRef entity = this.factory.entityRef(name, pub, sys);
                this.factory.addContent(this.getCurrentElement(), entity);
            }
            this.suppress = true;
        }
    }

    public void startPrefixMapping(String prefix, String uri) throws SAXException {
        if (this.suppress) {
            return;
        }
        Namespace ns = Namespace.getNamespace(prefix, uri);
        this.declaredNamespaces.add(ns);
    }

    private void transferNamespaces(Element element) {
        Iterator i = this.declaredNamespaces.iterator();
        while (i.hasNext()) {
            Namespace ns = (Namespace)i.next();
            if (ns == element.getNamespace()) continue;
            element.addNamespaceDeclaration(ns);
        }
        this.declaredNamespaces.clear();
    }

    public void unparsedEntityDecl(String name, String publicID, String systemID, String notationName) throws SAXException {
        if (!this.inInternalSubset) {
            return;
        }
        this.internalSubset.append("  <!ENTITY ").append(name);
        this.appendExternalId(publicID, systemID);
        this.internalSubset.append(" NDATA ").append(notationName);
        this.internalSubset.append(">\n");
    }
}

