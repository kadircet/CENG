/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

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
import org.w3c.dom.Attr;
import org.w3c.dom.DocumentType;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

public class DOMBuilder {
    private static final String CVS_ID = "@(#) $RCSfile: DOMBuilder.java,v $ $Revision: 1.59 $ $Date: 2004/09/03 06:03:41 $ $Name: jdom_1_0 $";
    private String adapterClass;
    private JDOMFactory factory = new DefaultJDOMFactory();

    public DOMBuilder() {
    }

    public DOMBuilder(String adapterClass) {
        this.adapterClass = adapterClass;
    }

    public Document build(org.w3c.dom.Document domDocument) {
        Document doc = this.factory.document(null);
        this.buildTree(domDocument, doc, null, true);
        return doc;
    }

    public Element build(org.w3c.dom.Element domElement) {
        Document doc = this.factory.document(null);
        this.buildTree(domElement, doc, null, true);
        return doc.getRootElement();
    }

    private void buildTree(Node node, Document doc, Element current, boolean atRoot) {
        switch (node.getNodeType()) {
            case 9: {
                NodeList nodes = node.getChildNodes();
                int i = 0;
                int size = nodes.getLength();
                while (i < size) {
                    this.buildTree(nodes.item(i), doc, current, true);
                    ++i;
                }
                break;
            }
            case 1: {
                String attPrefix;
                String attname;
                Attr att;
                String nodeName = node.getNodeName();
                String prefix = "";
                String localName = nodeName;
                int colon = nodeName.indexOf(58);
                if (colon >= 0) {
                    prefix = nodeName.substring(0, colon);
                    localName = nodeName.substring(colon + 1);
                }
                Namespace ns = null;
                String uri = node.getNamespaceURI();
                ns = uri == null ? (current == null ? Namespace.NO_NAMESPACE : current.getNamespace(prefix)) : Namespace.getNamespace(prefix, uri);
                Element element = this.factory.element(localName, ns);
                if (atRoot) {
                    doc.setRootElement(element);
                } else {
                    this.factory.addContent(current, element);
                }
                NamedNodeMap attributeList = node.getAttributes();
                int attsize = attributeList.getLength();
                int i = 0;
                while (i < attsize) {
                    att = (Attr)attributeList.item(i);
                    attname = att.getName();
                    if (attname.startsWith("xmlns")) {
                        attPrefix = "";
                        colon = attname.indexOf(58);
                        if (colon >= 0) {
                            attPrefix = attname.substring(colon + 1);
                        }
                        String attvalue = att.getValue();
                        Namespace declaredNS = Namespace.getNamespace(attPrefix, attvalue);
                        if (prefix.equals(attPrefix)) {
                            element.setNamespace(declaredNS);
                        } else {
                            this.factory.addNamespaceDeclaration(element, declaredNS);
                        }
                    }
                    ++i;
                }
                i = 0;
                while (i < attsize) {
                    att = (Attr)attributeList.item(i);
                    attname = att.getName();
                    if (!attname.startsWith("xmlns")) {
                        attPrefix = "";
                        String attLocalName = attname;
                        colon = attname.indexOf(58);
                        if (colon >= 0) {
                            attPrefix = attname.substring(0, colon);
                            attLocalName = attname.substring(colon + 1);
                        }
                        String attvalue = att.getValue();
                        Namespace attns = null;
                        attns = "".equals(attPrefix) ? Namespace.NO_NAMESPACE : element.getNamespace(attPrefix);
                        Attribute attribute = this.factory.attribute(attLocalName, attvalue, attns);
                        this.factory.setAttribute(element, attribute);
                    }
                    ++i;
                }
                NodeList children = node.getChildNodes();
                if (children == null) break;
                int size = children.getLength();
                int i2 = 0;
                while (i2 < size) {
                    Node item = children.item(i2);
                    if (item != null) {
                        this.buildTree(item, doc, element, false);
                    }
                    ++i2;
                }
                break;
            }
            case 3: {
                String data = node.getNodeValue();
                this.factory.addContent(current, this.factory.text(data));
                break;
            }
            case 4: {
                String cdata = node.getNodeValue();
                this.factory.addContent(current, this.factory.cdata(cdata));
                break;
            }
            case 7: {
                if (atRoot) {
                    this.factory.addContent(doc, this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                    break;
                }
                this.factory.addContent(current, this.factory.processingInstruction(node.getNodeName(), node.getNodeValue()));
                break;
            }
            case 8: {
                if (atRoot) {
                    this.factory.addContent(doc, this.factory.comment(node.getNodeValue()));
                    break;
                }
                this.factory.addContent(current, this.factory.comment(node.getNodeValue()));
                break;
            }
            case 5: {
                EntityRef entity = this.factory.entityRef(node.getNodeName());
                this.factory.addContent(current, entity);
                break;
            }
            case 10: {
                DocumentType domDocType = (DocumentType)node;
                String publicID = domDocType.getPublicId();
                String systemID = domDocType.getSystemId();
                String internalDTD = domDocType.getInternalSubset();
                DocType docType = this.factory.docType(domDocType.getName());
                docType.setPublicID(publicID);
                docType.setSystemID(systemID);
                docType.setInternalSubset(internalDTD);
                this.factory.addContent(doc, docType);
                break;
            }
        }
    }

    public JDOMFactory getFactory() {
        return this.factory;
    }

    public void setFactory(JDOMFactory factory) {
        this.factory = factory;
    }
}

