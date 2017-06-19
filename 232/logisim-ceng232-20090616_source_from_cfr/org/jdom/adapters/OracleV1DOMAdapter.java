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
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class OracleV1DOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: OracleV1DOMAdapter.java,v $ $Revision: 1.19 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";
    static /* synthetic */ Class class$org$xml$sax$InputSource;

    static /* synthetic */ Class class$(String class$) {
        try {
            return Class.forName(class$);
        }
        catch (ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }

    public Document createDocument() throws JDOMException {
        try {
            return (Document)Class.forName("oracle.xml.parser.XMLDocument").newInstance();
        }
        catch (Exception e) {
            throw new JDOMException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage() + " when creating document", e);
        }
    }

    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class parserClass = Class.forName("oracle.xml.parser.XMLParser");
            Object parser = parserClass.newInstance();
            Class[] arrclass = new Class[1];
            Class class_ = class$org$xml$sax$InputSource != null ? class$org$xml$sax$InputSource : (OracleV1DOMAdapter.class$org$xml$sax$InputSource = OracleV1DOMAdapter.class$("org.xml.sax.InputSource"));
            arrclass[0] = class_;
            Method parse = parserClass.getMethod("parse", arrclass);
            parse.invoke(parser, new InputSource(in));
            Method getDocument = parserClass.getMethod("getDocument", null);
            Document doc = (Document)getDocument.invoke(parser, null);
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

