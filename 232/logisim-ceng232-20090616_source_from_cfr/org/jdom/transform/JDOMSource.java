/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.transform;

import java.io.Reader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import javax.xml.transform.sax.SAXSource;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.output.SAXOutputter;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotSupportedException;
import org.xml.sax.XMLFilter;
import org.xml.sax.XMLReader;

public class JDOMSource
extends SAXSource {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMSource.java,v $ $Revision: 1.18 $ $Date: 2004/08/31 04:43:48 $ $Name: jdom_1_0 $";
    public static final String JDOM_FEATURE = "http://org.jdom.transform.JDOMSource/feature";
    private XMLReader xmlReader = null;

    public JDOMSource(List source) {
        this.setNodes(source);
    }

    public JDOMSource(Document source) {
        this.setDocument(source);
    }

    public JDOMSource(Element source) {
        ArrayList<Element> nodes = new ArrayList<Element>();
        nodes.add(source);
        this.setNodes(nodes);
    }

    public Document getDocument() {
        Object src = ((JDOMInputSource)this.getInputSource()).getSource();
        Document doc = null;
        if (src instanceof Document) {
            doc = (Document)src;
        }
        return doc;
    }

    public List getNodes() {
        Object src = ((JDOMInputSource)this.getInputSource()).getSource();
        List nodes = null;
        if (src instanceof List) {
            nodes = (List)src;
        }
        return nodes;
    }

    public XMLReader getXMLReader() {
        if (this.xmlReader == null) {
            this.xmlReader = new DocumentReader();
        }
        return this.xmlReader;
    }

    public void setDocument(Document source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public void setInputSource(InputSource inputSource) throws UnsupportedOperationException {
        throw new UnsupportedOperationException();
    }

    public void setNodes(List source) {
        super.setInputSource(new JDOMInputSource(source));
    }

    public void setXMLReader(XMLReader reader) throws UnsupportedOperationException {
        XMLFilter filter;
        if (reader instanceof XMLFilter) {
            filter = (XMLFilter)reader;
            while (filter.getParent() instanceof XMLFilter) {
                filter = (XMLFilter)filter.getParent();
            }
        } else {
            throw new UnsupportedOperationException();
        }
        filter.setParent(new DocumentReader());
        this.xmlReader = reader;
    }

    private static class JDOMInputSource
    extends InputSource {
        private Object source = null;

        public JDOMInputSource(List nodes) {
            this.source = nodes;
        }

        public JDOMInputSource(Document document) {
            this.source = document;
        }

        public Reader getCharacterStream() {
            Object src = this.getSource();
            StringReader reader = null;
            if (src instanceof Document) {
                reader = new StringReader(new XMLOutputter().outputString((Document)src));
            } else if (src instanceof List) {
                reader = new StringReader(new XMLOutputter().outputString((List)src));
            }
            return reader;
        }

        public Object getSource() {
            return this.source;
        }

        public void setCharacterStream(Reader characterStream) throws UnsupportedOperationException {
            throw new UnsupportedOperationException();
        }
    }

    private static class DocumentReader
    extends SAXOutputter
    implements XMLReader {
        public void parse(String systemId) throws SAXNotSupportedException {
            throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
        }

        public void parse(InputSource input) throws SAXException {
            if (input instanceof JDOMInputSource) {
                try {
                    Object source = ((JDOMInputSource)input).getSource();
                    if (source instanceof Document) {
                        this.output((Document)source);
                    }
                    this.output((List)source);
                }
                catch (JDOMException e) {
                    throw new SAXException(e.getMessage(), e);
                }
            } else {
                throw new SAXNotSupportedException("Only JDOM Documents are supported as input");
            }
        }
    }

}

