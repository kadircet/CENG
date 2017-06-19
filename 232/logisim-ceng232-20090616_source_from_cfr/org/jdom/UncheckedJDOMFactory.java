/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.jdom.Attribute;
import org.jdom.AttributeList;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.ContentList;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMFactory;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public class UncheckedJDOMFactory
implements JDOMFactory {
    public void addContent(Parent parent, Content child) {
        if (parent instanceof Element) {
            Element elt = (Element)parent;
            elt.content.uncheckedAddContent(child);
        } else {
            Document doc = (Document)parent;
            doc.content.uncheckedAddContent(child);
        }
    }

    public void addNamespaceDeclaration(Element parent, Namespace additional) {
        if (parent.additionalNamespaces == null) {
            parent.additionalNamespaces = new ArrayList(5);
        }
        parent.additionalNamespaces.add(additional);
    }

    public Attribute attribute(String name, String value) {
        Attribute a = new Attribute();
        a.name = name;
        a.value = value;
        a.namespace = Namespace.NO_NAMESPACE;
        return a;
    }

    public Attribute attribute(String name, String value, int type) {
        Attribute a = new Attribute();
        a.name = name;
        a.type = type;
        a.value = value;
        a.namespace = Namespace.NO_NAMESPACE;
        return a;
    }

    public Attribute attribute(String name, String value, int type, Namespace namespace) {
        Attribute a = new Attribute();
        a.name = name;
        a.type = type;
        a.value = value;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        a.namespace = namespace;
        return a;
    }

    public Attribute attribute(String name, String value, Namespace namespace) {
        Attribute a = new Attribute();
        a.name = name;
        a.value = value;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        a.namespace = namespace;
        return a;
    }

    public CDATA cdata(String str) {
        CDATA c = new CDATA();
        c.value = str;
        return c;
    }

    public Comment comment(String str) {
        Comment c = new Comment();
        c.text = str;
        return c;
    }

    public DocType docType(String elementName) {
        return this.docType(elementName, null, null);
    }

    public DocType docType(String elementName, String systemID) {
        return this.docType(elementName, null, systemID);
    }

    public DocType docType(String elementName, String publicID, String systemID) {
        DocType d = new DocType();
        d.elementName = elementName;
        d.publicID = publicID;
        d.systemID = systemID;
        return d;
    }

    public Document document(Element rootElement) {
        return this.document(rootElement, null, null);
    }

    public Document document(Element rootElement, DocType docType) {
        return this.document(rootElement, docType, null);
    }

    public Document document(Element rootElement, DocType docType, String baseURI) {
        Document d = new Document();
        if (docType != null) {
            this.addContent(d, docType);
        }
        if (rootElement != null) {
            this.addContent(d, rootElement);
        }
        if (baseURI != null) {
            d.baseURI = baseURI;
        }
        return d;
    }

    public Element element(String name) {
        Element e = new Element();
        e.name = name;
        e.namespace = Namespace.NO_NAMESPACE;
        return e;
    }

    public Element element(String name, String uri) {
        return this.element(name, Namespace.getNamespace("", uri));
    }

    public Element element(String name, String prefix, String uri) {
        return this.element(name, Namespace.getNamespace(prefix, uri));
    }

    public Element element(String name, Namespace namespace) {
        Element e = new Element();
        e.name = name;
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        e.namespace = namespace;
        return e;
    }

    public EntityRef entityRef(String name) {
        EntityRef e = new EntityRef();
        e.name = name;
        return e;
    }

    public EntityRef entityRef(String name, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.systemID = systemID;
        return e;
    }

    public EntityRef entityRef(String name, String publicID, String systemID) {
        EntityRef e = new EntityRef();
        e.name = name;
        e.publicID = publicID;
        e.systemID = systemID;
        return e;
    }

    public ProcessingInstruction processingInstruction(String target, String data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    public ProcessingInstruction processingInstruction(String target, Map data) {
        ProcessingInstruction p = new ProcessingInstruction();
        p.target = target;
        p.setData(data);
        return p;
    }

    public void setAttribute(Element parent, Attribute a) {
        parent.attributes.uncheckedAddAttribute(a);
    }

    public Text text(String str) {
        Text t = new Text();
        t.value = str;
        return t;
    }
}

