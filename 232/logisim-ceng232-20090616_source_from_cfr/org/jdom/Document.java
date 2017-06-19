/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.ContentList;
import org.jdom.DescendantIterator;
import org.jdom.DocType;
import org.jdom.Element;
import org.jdom.FilterIterator;
import org.jdom.IllegalAddException;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.filter.Filter;

public class Document
implements Parent {
    private static final String CVS_ID = "@(#) $RCSfile: Document.java,v $ $Revision: 1.84 $ $Date: 2004/08/31 21:47:51 $ $Name: jdom_1_0 $";
    ContentList content;
    protected String baseURI;
    private HashMap propertyMap;

    public Document() {
        this.content = new ContentList(this);
        this.baseURI = null;
        this.propertyMap = null;
    }

    public Document(List content) {
        this.content = new ContentList(this);
        this.baseURI = null;
        this.propertyMap = null;
        this.setContent(content);
    }

    public Document(Element rootElement) {
        this(rootElement, null, null);
    }

    public Document(Element rootElement, DocType docType) {
        this(rootElement, docType, null);
    }

    public Document(Element rootElement, DocType docType, String baseURI) {
        this.content = new ContentList(this);
        this.baseURI = null;
        this.propertyMap = null;
        if (rootElement != null) {
            this.setRootElement(rootElement);
        }
        if (docType != null) {
            this.setDocType(docType);
        }
        if (baseURI != null) {
            this.setBaseURI(baseURI);
        }
    }

    public Document addContent(int index, Collection c) {
        this.content.addAll(index, c);
        return this;
    }

    public Document addContent(int index, Content child) {
        this.content.add(index, child);
        return this;
    }

    public Document addContent(Collection c) {
        this.content.addAll(c);
        return this;
    }

    public Document addContent(Content child) {
        this.content.add(child);
        return this;
    }

    public Object clone() {
        Document doc;
        doc = null;
        try {
            doc = (Document)super.clone();
        }
        catch (CloneNotSupportedException v0) {}
        doc.content = new ContentList(doc);
        int i = 0;
        while (i < this.content.size()) {
            Object obj = this.content.get(i);
            if (obj instanceof Element) {
                Element element = (Element)((Element)obj).clone();
                doc.content.add(element);
            } else if (obj instanceof Comment) {
                Comment comment = (Comment)((Comment)obj).clone();
                doc.content.add(comment);
            } else if (obj instanceof ProcessingInstruction) {
                ProcessingInstruction pi = (ProcessingInstruction)((ProcessingInstruction)obj).clone();
                doc.content.add(pi);
            } else if (obj instanceof DocType) {
                DocType dt = (DocType)((DocType)obj).clone();
                doc.content.add(dt);
            }
            ++i;
        }
        return doc;
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

    public Element detachRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            return null;
        }
        return (Element)this.removeContent(index);
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public final String getBaseURI() {
        return this.baseURI;
    }

    public List getContent() {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
        return this.content;
    }

    public Content getContent(int index) {
        return (Content)this.content.get(index);
    }

    public List getContent(Filter filter) {
        if (!this.hasRootElement()) {
            throw new IllegalStateException("Root element not set");
        }
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

    public DocType getDocType() {
        int index = this.content.indexOfDocType();
        if (index < 0) {
            return null;
        }
        return (DocType)this.content.get(index);
    }

    public Document getDocument() {
        return this;
    }

    public Parent getParent() {
        return null;
    }

    public Object getProperty(String id) {
        if (this.propertyMap == null) {
            return null;
        }
        return this.propertyMap.get(id);
    }

    public Element getRootElement() {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            throw new IllegalStateException("Root element not set");
        }
        return (Element)this.content.get(index);
    }

    public boolean hasRootElement() {
        return this.content.indexOfFirstElement() >= 0;
    }

    public final int hashCode() {
        return super.hashCode();
    }

    public int indexOf(Content child) {
        return this.content.indexOf(child);
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

    public final void setBaseURI(String uri) {
        this.baseURI = uri;
    }

    public Document setContent(int index, Collection collection) {
        this.content.remove(index);
        this.content.addAll(index, collection);
        return this;
    }

    public Document setContent(int index, Content child) {
        this.content.set(index, child);
        return this;
    }

    public Document setContent(Collection newContent) {
        this.content.clearAndSet(newContent);
        return this;
    }

    public Document setContent(Content child) {
        this.content.clear();
        this.content.add(child);
        return this;
    }

    public Document setDocType(DocType docType) {
        if (docType == null) {
            int docTypeIndex = this.content.indexOfDocType();
            if (docTypeIndex >= 0) {
                this.content.remove(docTypeIndex);
            }
            return this;
        }
        if (docType.getParent() != null) {
            throw new IllegalAddException(docType, "The DocType already is attached to a document");
        }
        int docTypeIndex = this.content.indexOfDocType();
        if (docTypeIndex < 0) {
            this.content.add(0, docType);
        } else {
            this.content.set(docTypeIndex, docType);
        }
        return this;
    }

    public void setProperty(String id, Object value) {
        if (this.propertyMap == null) {
            this.propertyMap = new HashMap();
        }
        this.propertyMap.put(id, value);
    }

    public Document setRootElement(Element rootElement) {
        int index = this.content.indexOfFirstElement();
        if (index < 0) {
            this.content.add(rootElement);
        } else {
            this.content.set(index, rootElement);
        }
        return this;
    }

    public String toString() {
        StringBuffer stringForm = new StringBuffer().append("[Document: ");
        DocType docType = this.getDocType();
        if (docType != null) {
            stringForm.append(docType.toString()).append(", ");
        } else {
            stringForm.append(" No DOCTYPE declaration, ");
        }
        Element rootElement = this.getRootElement();
        if (rootElement != null) {
            stringForm.append("Root is ").append(rootElement.toString());
        } else {
            stringForm.append(" No root element");
        }
        stringForm.append("]");
        return stringForm.toString();
    }
}

