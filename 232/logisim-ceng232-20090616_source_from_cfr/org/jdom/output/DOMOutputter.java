/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.output;

import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.adapters.DOMAdapter;
import org.jdom.output.NamespaceStack;
import org.w3c.dom.Attr;
import org.w3c.dom.CDATASection;
import org.w3c.dom.EntityReference;
import org.w3c.dom.Node;

public class DOMOutputter {
    private static final String CVS_ID = "@(#) $RCSfile: DOMOutputter.java,v $ $Revision: 1.41 $ $Date: 2004/09/03 06:03:42 $ $Name: jdom_1_0 $";
    private static final String DEFAULT_ADAPTER_CLASS = "org.jdom.adapters.XercesDOMAdapter";
    private String adapterClass;

    public DOMOutputter() {
    }

    public DOMOutputter(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    private org.w3c.dom.Document createDOMDocument(DocType dt) throws JDOMException {
        if (this.adapterClass != null) {
            try {
                DOMAdapter adapter = (DOMAdapter)Class.forName(this.adapterClass).newInstance();
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException v0) {
            }
            catch (IllegalAccessException v1) {
            }
            catch (InstantiationException v2) {}
        } else {
            try {
                DOMAdapter adapter = (DOMAdapter)Class.forName("org.jdom.adapters.JAXPDOMAdapter").newInstance();
                return adapter.createDocument(dt);
            }
            catch (ClassNotFoundException v3) {
            }
            catch (IllegalAccessException v4) {
            }
            catch (InstantiationException v5) {}
        }
        try {
            DOMAdapter adapter = (DOMAdapter)Class.forName("org.jdom.adapters.XercesDOMAdapter").newInstance();
            return adapter.createDocument(dt);
        }
        catch (ClassNotFoundException v6) {
        }
        catch (IllegalAccessException v7) {
        }
        catch (InstantiationException v8) {}
        throw new JDOMException("No JAXP or default parser available");
    }

    private static String getXmlnsTagFor(Namespace ns) {
        String attrName = "xmlns";
        if (!ns.getPrefix().equals("")) {
            attrName = String.valueOf(attrName) + ":";
            attrName = String.valueOf(attrName) + ns.getPrefix();
        }
        return attrName;
    }

    private Attr output(Attribute attribute, org.w3c.dom.Document domDoc) throws JDOMException {
        Attr domAttr = null;
        try {
            domAttr = attribute.getNamespace() == Namespace.NO_NAMESPACE ? domDoc.createAttribute(attribute.getQualifiedName()) : domDoc.createAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName());
            domAttr.setValue(attribute.getValue());
        }
        catch (Exception e) {
            throw new JDOMException("Exception outputting Attribute " + attribute.getQualifiedName(), e);
        }
        return domAttr;
    }

    public org.w3c.dom.Document output(Document document) throws JDOMException {
        NamespaceStack namespaces = new NamespaceStack();
        org.w3c.dom.Document domDoc = null;
        try {
            DocType dt = document.getDocType();
            domDoc = this.createDOMDocument(dt);
            Iterator itr = document.getContent().iterator();
            while (itr.hasNext()) {
                Object node = itr.next();
                if (node instanceof Element) {
                    Element element = (Element)node;
                    org.w3c.dom.Element domElement = this.output(element, domDoc, namespaces);
                    org.w3c.dom.Element root = domDoc.getDocumentElement();
                    if (root == null) {
                        domDoc.appendChild(domElement);
                        continue;
                    }
                    domDoc.replaceChild(domElement, root);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment)node;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domDoc.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)node;
                    org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domDoc.appendChild(domPI);
                    continue;
                }
                if (node instanceof DocType) continue;
                throw new JDOMException("Document contained top-level content with type:" + node.getClass().getName());
            }
        }
        catch (Throwable e) {
            throw new JDOMException("Exception outputting Document", e);
        }
        return domDoc;
    }

    private org.w3c.dom.Element output(Element element, org.w3c.dom.Document domDoc, NamespaceStack namespaces) throws JDOMException {
        try {
            int previouslyDeclaredNamespaces = namespaces.size();
            org.w3c.dom.Element domElement = null;
            domElement = element.getNamespace() == Namespace.NO_NAMESPACE ? domDoc.createElement(element.getQualifiedName()) : domDoc.createElementNS(element.getNamespaceURI(), element.getQualifiedName());
            Namespace ns = element.getNamespace();
            if (ns != Namespace.XML_NAMESPACE && (ns != Namespace.NO_NAMESPACE || namespaces.getURI("") != null)) {
                String prefix = ns.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (!ns.getURI().equals(uri)) {
                    namespaces.push(ns);
                    String attrName = DOMOutputter.getXmlnsTagFor(ns);
                    domElement.setAttribute(attrName, ns.getURI());
                }
            }
            Iterator itr = element.getAdditionalNamespaces().iterator();
            while (itr.hasNext()) {
                Namespace additional = (Namespace)itr.next();
                String prefix = additional.getPrefix();
                String uri = namespaces.getURI(prefix);
                if (additional.getURI().equals(uri)) continue;
                String attrName = DOMOutputter.getXmlnsTagFor(additional);
                domElement.setAttribute(attrName, additional.getURI());
                namespaces.push(additional);
            }
            itr = element.getAttributes().iterator();
            while (itr.hasNext()) {
                Attribute attribute = (Attribute)itr.next();
                domElement.setAttributeNode(this.output(attribute, domDoc));
                Namespace ns1 = attribute.getNamespace();
                if (ns1 != Namespace.NO_NAMESPACE && ns1 != Namespace.XML_NAMESPACE) {
                    String prefix = ns1.getPrefix();
                    String uri = namespaces.getURI(prefix);
                    if (!ns1.getURI().equals(uri)) {
                        String attrName = DOMOutputter.getXmlnsTagFor(ns1);
                        domElement.setAttribute(attrName, ns1.getURI());
                        namespaces.push(ns1);
                    }
                }
                if (attribute.getNamespace() == Namespace.NO_NAMESPACE) {
                    domElement.setAttribute(attribute.getQualifiedName(), attribute.getValue());
                    continue;
                }
                domElement.setAttributeNS(attribute.getNamespaceURI(), attribute.getQualifiedName(), attribute.getValue());
            }
            itr = element.getContent().iterator();
            while (itr.hasNext()) {
                org.w3c.dom.Text domText;
                Object node = itr.next();
                if (node instanceof Element) {
                    Element e = (Element)node;
                    org.w3c.dom.Element domElt = this.output(e, domDoc, namespaces);
                    domElement.appendChild(domElt);
                    continue;
                }
                if (node instanceof String) {
                    String str = (String)node;
                    domText = domDoc.createTextNode(str);
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof CDATA) {
                    CDATA cdata = (CDATA)node;
                    CDATASection domCdata = domDoc.createCDATASection(cdata.getText());
                    domElement.appendChild(domCdata);
                    continue;
                }
                if (node instanceof Text) {
                    Text text = (Text)node;
                    domText = domDoc.createTextNode(text.getText());
                    domElement.appendChild(domText);
                    continue;
                }
                if (node instanceof Comment) {
                    Comment comment = (Comment)node;
                    org.w3c.dom.Comment domComment = domDoc.createComment(comment.getText());
                    domElement.appendChild(domComment);
                    continue;
                }
                if (node instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)node;
                    org.w3c.dom.ProcessingInstruction domPI = domDoc.createProcessingInstruction(pi.getTarget(), pi.getData());
                    domElement.appendChild(domPI);
                    continue;
                }
                if (node instanceof EntityRef) {
                    EntityRef entity = (EntityRef)node;
                    EntityReference domEntity = domDoc.createEntityReference(entity.getName());
                    domElement.appendChild(domEntity);
                    continue;
                }
                throw new JDOMException("Element contained content with type:" + node.getClass().getName());
            }
            while (namespaces.size() > previouslyDeclaredNamespaces) {
                namespaces.pop();
            }
            return domElement;
        }
        catch (Exception e) {
            throw new JDOMException("Exception outputting Element " + element.getQualifiedName(), e);
        }
    }
}

