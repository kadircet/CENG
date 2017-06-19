/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.Serializable;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Parent;

public abstract class Content
implements Cloneable,
Serializable {
    protected Parent parent = null;

    protected Content() {
    }

    public Object clone() {
        try {
            Content c = (Content)super.clone();
            c.parent = null;
            return c;
        }
        catch (CloneNotSupportedException v0) {
            return null;
        }
    }

    public Content detach() {
        if (this.parent != null) {
            this.parent.removeContent(this);
        }
        return this;
    }

    public final boolean equals(Object ob) {
        return ob == this;
    }

    public Document getDocument() {
        if (this.parent == null) {
            return null;
        }
        return this.parent.getDocument();
    }

    public Parent getParent() {
        return this.parent;
    }

    public Element getParentElement() {
        Parent parent = this.getParent();
        return (Element)(parent instanceof Element ? parent : null);
    }

    public abstract String getValue();

    public final int hashCode() {
        return super.hashCode();
    }

    protected Content setParent(Parent parent) {
        this.parent = parent;
        return this;
    }
}

