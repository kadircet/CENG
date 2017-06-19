/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.output;

import java.util.Stack;
import org.jdom.Namespace;

class NamespaceStack {
    private static final String CVS_ID = "@(#) $RCSfile: NamespaceStack.java,v $ $Revision: 1.13 $ $Date: 2004/02/06 09:28:32 $ $Name: jdom_1_0 $";
    private Stack prefixes = new Stack();
    private Stack uris = new Stack();

    NamespaceStack() {
    }

    public String getURI(String prefix) {
        int index = this.prefixes.lastIndexOf(prefix);
        if (index == -1) {
            return null;
        }
        String uri = (String)this.uris.elementAt(index);
        return uri;
    }

    public String pop() {
        String prefix = (String)this.prefixes.pop();
        this.uris.pop();
        return prefix;
    }

    public void push(Namespace ns) {
        this.prefixes.push(ns.getPrefix());
        this.uris.push(ns.getURI());
    }

    public int size() {
        return this.prefixes.size();
    }

    public String toString() {
        StringBuffer buf = new StringBuffer();
        String sep = System.getProperty("line.separator");
        buf.append("Stack: " + this.prefixes.size() + sep);
        int i = 0;
        while (i < this.prefixes.size()) {
            buf.append(String.valueOf(String.valueOf(this.prefixes.elementAt(i))) + "&" + this.uris.elementAt(i) + sep);
            ++i;
        }
        return buf.toString();
    }
}

