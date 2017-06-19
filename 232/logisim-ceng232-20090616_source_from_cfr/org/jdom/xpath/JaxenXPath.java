/*
 * Decompiled with CFR 0_114.
 * 
 * Could not load the following classes:
 *  org.jaxen.JaxenException
 *  org.jaxen.NamespaceContext
 *  org.jaxen.SimpleNamespaceContext
 *  org.jaxen.SimpleVariableContext
 *  org.jaxen.VariableContext
 *  org.jaxen.jdom.JDOMXPath
 */
package org.jdom.xpath;

import java.util.List;
import org.jaxen.JaxenException;
import org.jaxen.NamespaceContext;
import org.jaxen.SimpleNamespaceContext;
import org.jaxen.SimpleVariableContext;
import org.jaxen.VariableContext;
import org.jaxen.jdom.JDOMXPath;
import org.jdom.Attribute;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jdom.Namespace;
import org.jdom.xpath.XPath;

class JaxenXPath
extends XPath {
    private static final String CVS_ID = "@(#) $RCSfile: JaxenXPath.java,v $ $Revision: 1.19 $ $Date: 2004/09/03 07:27:39 $ $Name: jdom_1_0 $";
    private transient JDOMXPath xPath;
    private Object currentContext;

    public JaxenXPath(String expr) throws JDOMException {
        this.setXPath(expr);
    }

    public void addNamespace(Namespace namespace) {
        try {
            this.xPath.addNamespace(namespace.getPrefix(), namespace.getURI());
        }
        catch (JaxenException v0) {}
    }

    public boolean equals(Object o) {
        if (o instanceof JaxenXPath) {
            JaxenXPath x = (JaxenXPath)o;
            return super.equals(o) && this.xPath.toString().equals(x.xPath.toString());
        }
        return false;
    }

    public String getXPath() {
        return this.xPath.toString();
    }

    public int hashCode() {
        return this.xPath.hashCode();
    }

    public Number numberValueOf(Object context) throws JDOMException {
        try {
            Number number;
            try {
                this.currentContext = context;
                number = this.xPath.numberValueOf(context);
                Object var4_3 = null;
            }
            catch (JaxenException ex1) {
                throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), (Throwable)ex1);
            }
            this.currentContext = null;
            return number;
        }
        catch (Throwable var3_6) {
            Object var4_4 = null;
            this.currentContext = null;
            throw var3_6;
        }
    }

    public List selectNodes(Object context) throws JDOMException {
        try {
            List list;
            try {
                this.currentContext = context;
                list = this.xPath.selectNodes(context);
                Object var4_3 = null;
            }
            catch (JaxenException ex1) {
                throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), (Throwable)ex1);
            }
            this.currentContext = null;
            return list;
        }
        catch (Throwable var3_6) {
            Object var4_4 = null;
            this.currentContext = null;
            throw var3_6;
        }
    }

    public Object selectSingleNode(Object context) throws JDOMException {
        try {
            Object object;
            try {
                this.currentContext = context;
                object = this.xPath.selectSingleNode(context);
                Object var4_3 = null;
            }
            catch (JaxenException ex1) {
                throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), (Throwable)ex1);
            }
            this.currentContext = null;
            return object;
        }
        catch (Throwable var3_6) {
            Object var4_4 = null;
            this.currentContext = null;
            throw var3_6;
        }
    }

    public void setVariable(String name, Object value) throws IllegalArgumentException {
        VariableContext o = this.xPath.getVariableContext();
        if (o instanceof SimpleVariableContext) {
            ((SimpleVariableContext)o).setVariableValue(null, name, value);
        }
    }

    private void setXPath(String expr) throws JDOMException {
        try {
            this.xPath = new JDOMXPath(expr);
            this.xPath.setNamespaceContext((NamespaceContext)new NSContext());
        }
        catch (Exception ex1) {
            throw new JDOMException("Invalid XPath expression: \"" + expr + "\"", ex1);
        }
    }

    public String toString() {
        return this.xPath.toString();
    }

    public String valueOf(Object context) throws JDOMException {
        try {
            String string;
            try {
                this.currentContext = context;
                string = this.xPath.stringValueOf(context);
                Object var4_3 = null;
            }
            catch (JaxenException ex1) {
                throw new JDOMException("XPath error while evaluating \"" + this.xPath.toString() + "\": " + ex1.getMessage(), (Throwable)ex1);
            }
            this.currentContext = null;
            return string;
        }
        catch (Throwable var3_6) {
            Object var4_4 = null;
            this.currentContext = null;
            throw var3_6;
        }
    }

    private class NSContext
    extends SimpleNamespaceContext {
        public String translateNamespacePrefixToUri(String prefix) {
            Object ctx;
            if (prefix == null || prefix.length() == 0) {
                return null;
            }
            String uri = super.translateNamespacePrefixToUri(prefix);
            if (uri == null && (ctx = JaxenXPath.this.currentContext) != null) {
                Namespace ns;
                Element elt = null;
                if (ctx instanceof Element) {
                    elt = (Element)ctx;
                } else if (ctx instanceof Attribute) {
                    elt = ((Attribute)ctx).getParent();
                } else if (ctx instanceof Content) {
                    elt = ((Content)ctx).getParentElement();
                } else if (ctx instanceof Document) {
                    elt = ((Document)ctx).getRootElement();
                }
                if (elt != null && (ns = elt.getNamespace(prefix)) != null) {
                    uri = ns.getURI();
                }
            }
            return uri;
        }
    }

}

