/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.filter;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.filter.AbstractFilter;

public class ElementFilter
extends AbstractFilter {
    private static final String CVS_ID = "@(#) $RCSfile: ElementFilter.java,v $ $Revision: 1.18 $ $Date: 2004/09/07 06:37:20 $ $Name: jdom_1_0 $";
    private String name;
    private transient Namespace namespace;

    public ElementFilter() {
    }

    public ElementFilter(String name) {
        this.name = name;
    }

    public ElementFilter(String name, Namespace namespace) {
        this.name = name;
        this.namespace = namespace;
    }

    public ElementFilter(Namespace namespace) {
        this.namespace = namespace;
    }

    /*
     * Enabled force condition propagation
     * Lifted jumps to return sites
     */
    public boolean equals(Object obj) {
        boolean bl;
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof ElementFilter)) {
            return false;
        }
        ElementFilter filter = (ElementFilter)obj;
        if (this.name != null) {
            bl = this.name.equals(filter.name) ^ true;
        } else {
            if (filter.name != null) return false;
            bl = false;
        }
        if (bl) {
            return false;
        }
        if (this.namespace == null) {
            if (filter.namespace != null) return false;
            return true;
        }
        boolean bl2 = this.namespace.equals(filter.namespace) ^ true;
        if (!bl2) return true;
        return false;
    }

    public int hashCode() {
        int result = this.name != null ? this.name.hashCode() : 0;
        result = 29 * result + (this.namespace != null ? this.namespace.hashCode() : 0);
        return result;
    }

    public boolean matches(Object obj) {
        if (obj instanceof Element) {
            Element el = (Element)obj;
            return !(this.name != null && !this.name.equals(el.getName()) || this.namespace != null && !this.namespace.equals(el.getNamespace()));
        }
        return false;
    }

    private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException {
        in.defaultReadObject();
        this.namespace = Namespace.getNamespace((String)in.readObject(), (String)in.readObject());
    }

    private void writeObject(ObjectOutputStream out) throws IOException {
        out.defaultWriteObject();
        out.writeObject(this.namespace.getPrefix());
        out.writeObject(this.namespace.getURI());
    }
}

