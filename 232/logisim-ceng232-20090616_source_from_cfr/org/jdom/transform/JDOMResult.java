/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.transform;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import javax.xml.transform.sax.SAXResult;
import org.jdom.DefaultJDOMFactory;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMFactory;
import org.jdom.input.SAXHandler;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class JDOMResult
extends SAXResult {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMResult.java,v $ $Revision: 1.23 $ $Date: 2004/08/31 06:10:38 $ $Name: jdom_1_0 $";
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMResult/feature";
    private Object result = null;
    private boolean queried = false;
    private JDOMFactory factory = null;

    public JDOMResult() {
        DocumentBuilder builder = new DocumentBuilder();
        super.setHandler(builder);
        super.setLexicalHandler(builder);
    }

    public Document getDocument() {
        Document doc;
        doc = null;
        this.retrieveResult();
        if (this.result instanceof Document) {
            doc = (Document)this.result;
        } else if (this.result instanceof List && !this.queried) {
            try {
                JDOMFactory f = this.getFactory();
                if (f == null) {
                    f = new DefaultJDOMFactory();
                }
                doc = f.document(null);
                doc.setContent((List)this.result);
                this.result = doc;
            }
            catch (RuntimeException v0) {}
        }
        this.queried = true;
        return doc;
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public List getResult() {
        ArrayList nodes = Collections.EMPTY_LIST;
        this.retrieveResult();
        if (this.result instanceof List) {
            nodes = (List)this.result;
        } else if (this.result instanceof Document && !this.queried) {
            List content = ((Document)this.result).getContent();
            nodes = new ArrayList(content.size());
            while (content.size() != 0) {
                Object o = content.remove(0);
                nodes.add(o);
            }
            this.result = nodes;
        }
        this.queried = true;
        return nodes;
    }

    private void retrieveResult() {
        if (this.result == null) {
            this.setResult(((DocumentBuilder)this.getHandler()).getResult());
        }
    }

    public void setDocument(Document document) {
        this.result = document;
        this.queried = false;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }

    public void setHandler(ContentHandler handler) {
    }

    public void setLexicalHandler(LexicalHandler handler) {
    }

    public void setResult(List result) {
        this.result = result;
        this.queried = false;
    }

    private static class FragmentHandler
    extends SAXHandler {
        private Element dummyRoot = new Element("root", null, null);

        public FragmentHandler(JDOMFactory factory) {
            super(factory);
            this.pushElement(this.dummyRoot);
        }

        private List getDetachedContent(Element elt) {
            List content = elt.getContent();
            ArrayList nodes = new ArrayList(content.size());
            while (content.size() != 0) {
                Object o = content.remove(0);
                nodes.add(o);
            }
            return nodes;
        }

        public List getResult() {
            try {
                this.flushCharacters();
            }
            catch (SAXException v0) {}
            return this.getDetachedContent(this.dummyRoot);
        }
    }

    private class DocumentBuilder
    extends XMLFilterImpl
    implements LexicalHandler {
        private FragmentHandler saxHandler;
        private boolean startDocumentReceived;

        public DocumentBuilder() {
            this.saxHandler = null;
            this.startDocumentReceived = false;
        }

        public void characters(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            super.characters(ch, start, length);
        }

        public void comment(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.comment(ch, start, length);
        }

        public void endCDATA() throws SAXException {
            this.saxHandler.endCDATA();
        }

        public void endDTD() throws SAXException {
            this.saxHandler.endDTD();
        }

        public void endEntity(String name) throws SAXException {
            this.saxHandler.endEntity(name);
        }

        private void ensureInitialization() throws SAXException {
            if (!this.startDocumentReceived) {
                this.startDocument();
            }
        }

        public List getResult() {
            List result = null;
            if (this.saxHandler != null) {
                result = this.saxHandler.getResult();
                this.saxHandler = null;
                this.startDocumentReceived = false;
            }
            return result;
        }

        public void ignorableWhitespace(char[] ch, int start, int length) throws SAXException {
            this.ensureInitialization();
            super.ignorableWhitespace(ch, start, length);
        }

        public void processingInstruction(String target, String data) throws SAXException {
            this.ensureInitialization();
            super.processingInstruction(target, data);
        }

        public void skippedEntity(String name) throws SAXException {
            this.ensureInitialization();
            super.skippedEntity(name);
        }

        public void startCDATA() throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startCDATA();
        }

        public void startDTD(String name, String publicId, String systemId) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startDTD(name, publicId, systemId);
        }

        public void startDocument() throws SAXException {
            this.startDocumentReceived = true;
            JDOMResult.this.setResult(null);
            this.saxHandler = new FragmentHandler(JDOMResult.this.getFactory());
            super.setContentHandler(this.saxHandler);
            super.startDocument();
        }

        public void startElement(String nsURI, String localName, String qName, Attributes atts) throws SAXException {
            this.ensureInitialization();
            super.startElement(nsURI, localName, qName, atts);
        }

        public void startEntity(String name) throws SAXException {
            this.ensureInitialization();
            this.saxHandler.startEntity(name);
        }

        public void startPrefixMapping(String prefix, String uri) throws SAXException {
            this.ensureInitialization();
            super.startPrefixMapping(prefix, uri);
        }
    }

}

