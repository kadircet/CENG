/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;
import org.jdom.Attribute;
import org.jdom.AttributeList;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.ContentList;
import org.jdom.DescendantIterator;
import org.jdom.Document;
import org.jdom.EntityRef;
import org.jdom.FilterIterator;
import org.jdom.IllegalAddException;
import org.jdom.IllegalNameException;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;
import org.jdom.Verifier;
import org.jdom.filter.ElementFilter;
import org.jdom.filter.Filter;

public class Element
extends Content
implements Parent {
    private static final String CVS_ID = "@(#) $RCSfile: Element.java,v $ $Revision: 1.152 $ $Date: 2004/09/03 06:35:39 $ $Name: jdom_1_0 $";
    private static final int INITIAL_ARRAY_SIZE = 5;
    protected String name;
    protected transient Namespace namespace;
    protected transient List additionalNamespaces;
    AttributeList attributes;
    ContentList content;

    protected Element() {
        this.attributes = new AttributeList(this);
        this.content = new ContentList(this);
    }

    public Element(String name) {
        this(name, (Namespace)null);
    }

    public Element(String name, String uri) {
        this(name, Namespace.getNamespace("", uri));
    }

    public Element(String name, String prefix, String uri) {
        this(name, Namespace.getNamespace(prefix, uri));
    }

    public Element(String name, Namespace namespace) {
        this.attributes = new AttributeList(this);
        this.content = new ContentList(this);
        this.setName(name);
        this.setNamespace(namespace);
    }

    public Element addContent(int index, Collection c) {
        this.content.addAll(index, c);
        return this;
    }

    public Element addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    public Element addContent(String str) {
        return this.addContent(new Text(str));
    }

    public Element addContent(Collection collection) {
        this.content.addAll(collection);
        return this;
    }

    public Element addContent(Content child) {
        this.content.add(child);
        return this;
    }

    public void addNamespaceDeclaration(Namespace additional) {
        String reason = Verifier.checkNamespaceCollision(additional, this);
        if (reason != null) {
            throw new IllegalAddException(this, additional, reason);
        }
        if (this.additionalNamespaces == null) {
            this.additionalNamespaces = new ArrayList(5);
        }
        this.additionalNamespaces.add(additional);
    }

    public Object clone() {
        Object obj;
        int i;
        Element element = null;
        element = (Element)super.clone();
        element.content = new ContentList(element);
        element.attributes = new AttributeList(element);
        if (this.attributes != null) {
            i = 0;
            while (i < this.attributes.size()) {
                obj = this.attributes.get(i);
                Attribute attribute = (Attribute)((Attribute)obj).clone();
                element.attributes.add(attribute);
                ++i;
            }
        }
        if (this.additionalNamespaces != null) {
            int additionalSize = this.additionalNamespaces.size();
            element.additionalNamespaces = new ArrayList(additionalSize);
            int i2 = 0;
            while (i2 < additionalSize) {
                Object additional = this.additionalNamespaces.get(i2);
                element.additionalNamespaces.add(additional);
                ++i2;
            }
        }
        if (this.content != null) {
            i = 0;
            while (i < this.content.size()) {
                obj = this.content.get(i);
                if (obj instanceof Element) {
                    Element elt = (Element)((Element)obj).clone();
                    element.content.add(elt);
                } else if (obj instanceof CDATA) {
                    CDATA cdata = (CDATA)((CDATA)obj).clone();
                    element.content.add(cdata);
                } else if (obj instanceof Text) {
                    Text text = (Text)((Text)obj).clone();
                    element.content.add(text);
                } else if (obj instanceof Comment) {
                    Comment comment = (Comment)((Comment)obj).clone();
                    element.content.add(comment);
                } else if (obj instanceof ProcessingInstruction) {
                    ProcessingInstruction pi = (ProcessingInstruction)((ProcessingInstruction)obj).clone();
                    element.content.add(pi);
                } else if (obj instanceof EntityRef) {
                    EntityRef entity = (EntityRef)((EntityRef)obj).clone();
                    element.content.add(entity);
                }
                ++i;
            }
        }
        if (this.additionalNamespaces != null) {
            element.additionalNamespaces = new ArrayList();
            element.additionalNamespaces.addAll(this.additionalNamespaces);
        }
        return element;
    }

    public List cloneContent() {
        int size = this.getContentSize();
        ArrayList<Object> list = new ArrayList<Object>(size);
        int i = 0;
        while (i < size) {
            Content child = this.getContent(i);
            list.add(child.clone());
            ++i;
        }
        return list;
    }

    public List getAdditionalNamespaces() {
        if (this.additionalNamespaces == null) {
            return Collections.EMPTY_LIST;
        }
        return Collections.unmodifiableList(this.additionalNamespaces);
    }

    public Attribute getAttribute(String name) {
        return this.getAttribute(name, Namespace.NO_NAMESPACE);
    }

    public Attribute getAttribute(String name, Namespace ns) {
        return (Attribute)this.attributes.get(name, ns);
    }

    public String getAttributeValue(String name) {
        return this.getAttributeValue(name, Namespace.NO_NAMESPACE);
    }

    public String getAttributeValue(String name, String def) {
        return this.getAttributeValue(name, Namespace.NO_NAMESPACE, def);
    }

    public String getAttributeValue(String name, Namespace ns) {
        return this.getAttributeValue(name, ns, null);
    }

    public String getAttributeValue(String name, Namespace ns, String def) {
        Attribute attribute = (Attribute)this.attributes.get(name, ns);
        return attribute == null ? def : attribute.getValue();
    }

    public List getAttributes() {
        return this.attributes;
    }

    public Element getChild(String name) {
        return this.getChild(name, Namespace.NO_NAMESPACE);
    }

    public Element getChild(String name, Namespace ns) {
        List elements = this.content.getView(new ElementFilter(name, ns));
        Iterator i = elements.iterator();
        if (i.hasNext()) {
            return (Element)i.next();
        }
        return null;
    }

    public String getChildText(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildText(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getText();
    }

    public String getChildTextNormalize(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getTextNormalize();
    }

    public String getChildTextNormalize(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getTextNormalize();
    }

    public String getChildTextTrim(String name) {
        Element child = this.getChild(name);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public String getChildTextTrim(String name, Namespace ns) {
        Element child = this.getChild(name, ns);
        if (child == null) {
            return null;
        }
        return child.getTextTrim();
    }

    public List getChildren() {
        return this.content.getView(new ElementFilter());
    }

    public List getChildren(String name) {
        return this.getChildren(name, Namespace.NO_NAMESPACE);
    }

    public List getChildren(String name, Namespace ns) {
        return this.content.getView(new ElementFilter(name, ns));
    }

    public List getContent() {
        return this.content;
    }

    public Content getContent(int index) {
        return (Content)this.content.get(index);
    }

    public List getContent(Filter filter) {
        return this.content.getView(filter);
    }

    public int getContentSize() {
        return this.content.size();
    }

    public Iterator getDescendants() {
        return new DescendantIterator(this);
    }

    public Iterator getDescendants(Filter filter) {
        return new FilterIterator(new DescendantIterator(this), filter);
    }

    public String getName() {
        return this.name;
    }

    public Namespace getNamespace() {
        return this.namespace;
    }

    public Namespace getNamespace(String prefix) {
        if (prefix == null) {
            return null;
        }
        if (prefix.equals("xml")) {
            return Namespace.XML_NAMESPACE;
        }
        if (prefix.equals(this.getNamespacePrefix())) {
            return this.getNamespace();
        }
        if (this.additionalNamespaces != null) {
            int i = 0;
            while (i < this.additionalNamespaces.size()) {
                Namespace ns = (Namespace)this.additionalNamespaces.get(i);
                if (prefix.equals(ns.getPrefix())) {
                    return ns;
                }
                ++i;
            }
        }
        if (this.parent instanceof Element) {
            return ((Element)this.parent).getNamespace(prefix);
        }
        return null;
    }

    public String getNamespacePrefix() {
        return this.namespace.getPrefix();
    }

    public String getNamespaceURI() {
        return this.namespace.getURI();
    }

    public String getQualifiedName() {
        if (this.namespace.getPrefix().equals("")) {
            return this.getName();
        }
        return this.namespace.getPrefix() + ':' + this.name;
    }

    public String getText() {
        if (this.content.size() == 0) {
            return "";
        }
        if (this.content.size() == 1) {
            Object obj = this.content.get(0);
            if (obj instanceof Text) {
                return ((Text)obj).getText();
            }
            return "";
        }
        StringBuffer textContent = new StringBuffer();
        boolean hasText = false;
        int i = 0;
        while (i < this.content.size()) {
            Object obj = this.content.get(i);
            if (obj instanceof Text) {
                textContent.append(((Text)obj).getText());
                hasText = true;
            }
            ++i;
        }
        if (!hasText) {
            return "";
        }
        return textContent.toString();
    }

    public String getTextNormalize() {
        return Text.normalizeString(this.getText());
    }

    public String getTextTrim() {
        return this.getText().trim();
    }

    public String getValue() {
        StringBuffer buffer = new StringBuffer();
        Iterator itr = this.getContent().iterator();
        while (itr.hasNext()) {
            Content child = (Content)itr.next();
            if (!(child instanceof Element) && !(child instanceof Text)) continue;
            buffer.append(child.getValue());
        }
        return buffer.toString();
    }

    public int indexOf(Content child) {
        return this.content.indexOf(child);
    }

    public boolean isAncestor(Element element) {
        Parent p = element.getParent();
        while (p instanceof Element) {
            if (p == this) {
                return true;
            }
            p = ((Element)p).getParent();
        }
        return false;
    }

    public boolean isRootElement() {
        return this.parent instanceof Document;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
        int size = in.read();
        if (size != 0) {
            this.additionalNamespaces = new ArrayList(size);
            int i = 0;
            while (i < size) {
                Namespace additional = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
                this.additionalNamespaces.add(additional);
                ++i;
            }
        }
    }

    public boolean removeAttribute(String name) {
        return this.removeAttribute(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeAttribute(String name, Namespace ns) {
        return this.attributes.remove(name, ns);
    }

    public boolean removeAttribute(Attribute attribute) {
        return this.attributes.remove(attribute);
    }

    public boolean removeChild(String name) {
        return this.removeChild(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChild(String name, Namespace ns) {
        List old = this.content.getView(new ElementFilter(name, ns));
        Iterator i = old.iterator();
        if (i.hasNext()) {
            i.next();
            i.remove();
            return true;
        }
        return false;
    }

    public boolean removeChildren(String name) {
        return this.removeChildren(name, Namespace.NO_NAMESPACE);
    }

    public boolean removeChildren(String name, Namespace ns) {
        boolean deletedSome = false;
        List old = this.content.getView(new ElementFilter(name, ns));
        Iterator i = old.iterator();
        while (i.hasNext()) {
            i.next();
            i.remove();
            deletedSome = true;
        }
        return deletedSome;
    }

    public List removeContent() {
        ArrayList old = new ArrayList(this.content);
        this.content.clear();
        return old;
    }

    public Content removeContent(int index) {
        return (Content)this.content.remove(index);
    }

    public boolean removeContent(Content child) {
        return this.content.remove(child);
    }

    public List removeContent(Filter filter) {
        ArrayList<Content> old = new ArrayList<Content>();
        Iterator itr = this.content.getView(filter).iterator();
        while (itr.hasNext()) {
            Content child = (Content)itr.next();
            old.add(child);
            itr.remove();
        }
        return old;
    }

    public void removeNamespaceDeclaration(Namespace additionalNamespace) {
        if (this.additionalNamespaces == null) {
            return;
        }
        this.additionalNamespaces.remove(additionalNamespace);
    }

    public Element setAttribute(String name, String value) {
        return this.setAttribute(new Attribute(name, value));
    }

    public Element setAttribute(String name, String value, Namespace ns) {
        return this.setAttribute(new Attribute(name, value, ns));
    }

    public Element setAttribute(Attribute attribute) {
        this.attributes.add(attribute);
        return this;
    }

    public Element setAttributes(List newAttributes) {
        this.attributes.clearAndSet(newAttributes);
        return this;
    }

    public Parent setContent(int index, Collection collection) {
        this.content.remove(index);
        this.content.addAll(index, collection);
        return this;
    }

    public Element setContent(int index, Content child) {
        this.content.set(index, child);
        return this;
    }

    public Element setContent(Collection newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public Element setContent(Content child) {
        this.content.clear();
        this.content.add(child);
        return this;
    }

    public Element setName(String name) {
        String reason = Verifier.checkElementName(name);
        if (reason != null) {
            throw new IllegalNameException(name, "element", reason);
        }
        this.name = name;
        return this;
    }

    public Element setNamespace(Namespace namespace) {
        if (namespace == null) {
            namespace = Namespace.NO_NAMESPACE;
        }
        this.namespace = namespace;
        return this;
    }

    public Element setText(String text) {
        this.content.clear();
        if (text != null) {
            this.addContent(new Text(text));
        }
        return this;
    }

    public String toString() {
        StringBuffer stringForm = new StringBuffer(64).append("[Element: <").append(this.getQualifiedName());
        String nsuri = this.getNamespaceURI();
        if (!nsuri.equals("")) {
            stringForm.append(" [Namespace: ").append(nsuri).append("]");
        }
        stringForm.append("/>]");
        return stringForm.toString();
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
        if (this.additionalNamespaces == null) {
            out.write(0);
        } else {
            int size = this.additionalNamespaces.size();
            out.write(size);
            int i = 0;
            while (i < size) {
                Namespace additional = (Namespace)this.additionalNamespaces.get(i);
                out.writeObject(additional.getPrefix());
                out.writeObject(additional.getURI());
                ++i;
            }
        }
    }
}

