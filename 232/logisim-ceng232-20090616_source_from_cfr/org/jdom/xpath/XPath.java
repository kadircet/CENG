/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.xpath;

import java.io.InvalidObjectException;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Modifier;
import java.util.List;
import org.jdom.JDOMException;
import org.jdom.Namespace;

public abstract class XPath
implements Serializable {
    private static final String CVS_ID = "@(#) $RCSfile: XPath.java,v $ $Revision: 1.15 $ $Date: 2004/02/06 09:28:32 $ $Name: jdom_1_0 $";
    private static final String XPATH_CLASS_PROPERTY = "org.jdom.xpath.class";
    private static final String DEFAULT_XPATH_CLASS = "org.jdom.xpath.JaxenXPath";
    private static Constructor constructor = null;
    static /* synthetic */ Class class$org$jdom$xpath$XPath;
    static /* synthetic */ Class class$java$lang$String;

    public void addNamespace(String prefix, String uri) {
        this.addNamespace(Namespace.getNamespace(prefix, uri));
    }

    public abstract void addNamespace(Namespace var1);

    static /* synthetic */ Class class$(String class$) {
        try {
            return Class.forName(class$);
        }
        catch (ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }

    public abstract String getXPath();

    public static XPath newInstance(String path) throws JDOMException {
        try {
            if (constructor == null) {
                String className;
                try {
                    className = System.getProperty("org.jdom.xpath.class", "org.jdom.xpath.JaxenXPath");
                }
                catch (SecurityException v0) {
                    className = "org.jdom.xpath.JaxenXPath";
                }
                XPath.setXPathClass(Class.forName(className));
            }
            return (XPath)constructor.newInstance(path);
        }
        catch (JDOMException ex1) {
            throw ex1;
        }
        catch (InvocationTargetException ex2) {
            Throwable t = ex2.getTargetException();
            throw t instanceof JDOMException ? (JDOMException)t : new JDOMException(t.toString(), t);
        }
        catch (Exception ex3) {
            throw new JDOMException(ex3.toString(), ex3);
        }
    }

    public abstract Number numberValueOf(Object var1) throws JDOMException;

    public abstract List selectNodes(Object var1) throws JDOMException;

    public static List selectNodes(Object context, String path) throws JDOMException {
        return XPath.newInstance(path).selectNodes(context);
    }

    public abstract Object selectSingleNode(Object var1) throws JDOMException;

    public static Object selectSingleNode(Object context, String path) throws JDOMException {
        return XPath.newInstance(path).selectSingleNode(context);
    }

    public abstract void setVariable(String var1, Object var2);

    public static void setXPathClass(Class aClass) throws JDOMException {
        block5 : {
            if (aClass == null) {
                throw new IllegalArgumentException("aClass");
            }
            try {
                Class class_ = class$org$jdom$xpath$XPath != null ? class$org$jdom$xpath$XPath : (XPath.class$org$jdom$xpath$XPath = XPath.class$("org.jdom.xpath.XPath"));
                if (class_.isAssignableFrom(aClass) && !Modifier.isAbstract(aClass.getModifiers())) {
                    Class[] arrclass = new Class[1];
                    Class class_2 = class$java$lang$String != null ? class$java$lang$String : (XPath.class$java$lang$String = XPath.class$("java.lang.String"));
                    arrclass[0] = class_2;
                    constructor = aClass.getConstructor(arrclass);
                    break block5;
                }
                throw new JDOMException(String.valueOf(aClass.getName()) + " is not a concrete JDOM XPath implementation");
            }
            catch (JDOMException ex1) {
                throw ex1;
            }
            catch (Exception ex2) {
                throw new JDOMException(ex2.toString(), ex2);
            }
        }
    }

    public abstract String valueOf(Object var1) throws JDOMException;

    protected final Object writeReplace() throws ObjectStreamException {
        return new XPathString(this.getXPath());
    }

    private static final class XPathString
    implements Serializable {
        private String xPath = null;

        public XPathString(String xpath) {
            this.xPath = xpath;
        }

        private Object readResolve() throws ObjectStreamException {
            try {
                return XPath.newInstance(this.xPath);
            }
            catch (JDOMException ex1) {
                throw new InvalidObjectException("Can't create XPath object for expression \"" + this.xPath + "\": " + ex1.toString());
            }
        }
    }

}

