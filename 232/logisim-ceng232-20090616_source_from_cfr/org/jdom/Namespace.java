/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.HashMap;
import org.jdom.IllegalNameException;
import org.jdom.Verifier;

public final class Namespace {
    private static final String CVS_ID = "@(#) $RCSfile: Namespace.java,v $ $Revision: 1.41 $ $Date: 2004/02/27 11:32:57 $ $Name: jdom_1_0 $";
    private static HashMap namespaces;
    public static final Namespace NO_NAMESPACE;
    public static final Namespace XML_NAMESPACE;
    private String prefix;
    private String uri;

    static {
        NO_NAMESPACE = new Namespace("", "");
        XML_NAMESPACE = new Namespace("xml", "http://www.w3.org/XML/1998/namespace");
        namespaces = new HashMap();
        namespaces.put("&", NO_NAMESPACE);
        namespaces.put("xml&http://www.w3.org/XML/1998/namespace", XML_NAMESPACE);
    }

    private Namespace(String prefix, String uri) {
        this.prefix = prefix;
        this.uri = uri;
    }

    public boolean equals(Object ob) {
        if (this == ob) {
            return true;
        }
        if (ob instanceof Namespace) {
            return this.uri.equals(((Namespace)ob).uri);
        }
        return false;
    }

    public static Namespace getNamespace(String uri) {
        return Namespace.getNamespace("", uri);
    }

    public static Namespace getNamespace(String prefix, String uri) {
        String lookup;
        Namespace preexisting;
        if (prefix == null || prefix.trim().equals("")) {
            prefix = "";
        }
        if (uri == null || uri.trim().equals("")) {
            uri = "";
        }
        if ((preexisting = (Namespace)namespaces.get(lookup = new StringBuffer(64).append(prefix).append('&').append(uri).toString())) != null) {
            return preexisting;
        }
        String reason = Verifier.checkNamespacePrefix(prefix);
        if (reason != null) {
            throw new IllegalNameException(prefix, "Namespace prefix", reason);
        }
        reason = Verifier.checkNamespaceURI(uri);
        if (reason != null) {
            throw new IllegalNameException(uri, "Namespace URI", reason);
        }
        if (!prefix.equals("") && uri.equals("")) {
            throw new IllegalNameException("", "namespace", "Namespace URIs must be non-null and non-empty Strings");
        }
        if (prefix.equals("xml")) {
            throw new IllegalNameException(prefix, "Namespace prefix", "The xml prefix can only be bound to http://www.w3.org/XML/1998/namespace");
        }
        if (uri.equals("http://www.w3.org/XML/1998/namespace")) {
            throw new IllegalNameException(uri, "Namespace URI", "The http://www.w3.org/XML/1998/namespace must be bound to the xml prefix.");
        }
        Namespace ns = new Namespace(prefix, uri);
        namespaces.put(lookup, ns);
        return ns;
    }

    public String getPrefix() {
        return this.prefix;
    }

    public String getURI() {
        return this.uri;
    }

    public int hashCode() {
        return this.uri.hashCode();
    }

    public String toString() {
        return "[Namespace: prefix \"" + this.prefix + "\" is mapped to URI \"" + this.uri + "\"]";
    }
}

