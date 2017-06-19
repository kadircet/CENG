/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.adapters;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.jdom.JDOMException;
import org.jdom.adapters.AbstractDOMAdapter;
import org.w3c.dom.Document;
import org.xml.sax.SAXParseException;

public class CrimsonDOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: CrimsonDOMAdapter.java,v $ $Revision: 1.16 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";

    public Document createDocument() throws JDOMException {
        try {
            return (Document)Class.forName("org.apache.crimson.tree.XmlDocument").newInstance();
        }
        catch (Exception e) {
            throw new JDOMException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage() + " when creating document", e);
        }
    }

    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class[] parameterTypes = new Class[]{Class.forName("java.io.InputStream"), Boolean.TYPE};
            Object[] args = new Object[]{in, new Boolean(false)};
            Class parserClass = Class.forName("org.apache.crimson.tree.XmlDocument");
            Method createXmlDocument = parserClass.getMethod("createXmlDocument", parameterTypes);
            Document doc = (Document)createXmlDocument.invoke(null, args);
            return doc;
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException)targetException;
                throw new JDOMException("Error on line " + parseException.getLineNumber() + " of XML document: " + parseException.getMessage(), parseException);
            }
            if (targetException instanceof IOException) {
                IOException ioException = (IOException)targetException;
                throw ioException;
            }
            throw new JDOMException(targetException.getMessage(), targetException);
        }
        catch (Exception e) {
            throw new JDOMException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage(), e);
        }
    }
}

