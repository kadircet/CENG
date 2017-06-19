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
import org.jdom.input.BuilderErrorHandler;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXParseException;

public class XercesDOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: XercesDOMAdapter.java,v $ $Revision: 1.18 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";
    static /* synthetic */ Class class$java$lang$String;
    static /* synthetic */ Class class$org$xml$sax$ErrorHandler;
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
            return (Document)Class.forName("org.apache.xerces.dom.DocumentImpl").newInstance();
        }
        catch (Exception e) {
            throw new JDOMException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage() + " when creating document", e);
        }
    }

    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class parserClass = Class.forName("org.apache.xerces.parsers.DOMParser");
            Object parser = parserClass.newInstance();
            Class[] arrclass = new Class[2];
            Class class_ = class$java$lang$String != null ? class$java$lang$String : (XercesDOMAdapter.class$java$lang$String = XercesDOMAdapter.class$("java.lang.String"));
            arrclass[0] = class_;
            arrclass[1] = Boolean.TYPE;
            Method setFeature = parserClass.getMethod("setFeature", arrclass);
            setFeature.invoke(parser, "http://xml.org/sax/features/validation", new Boolean(validate));
            setFeature.invoke(parser, "http://xml.org/sax/features/namespaces", new Boolean(true));
            if (validate) {
                Class[] arrclass2 = new Class[1];
                Class class_2 = class$org$xml$sax$ErrorHandler != null ? class$org$xml$sax$ErrorHandler : (XercesDOMAdapter.class$org$xml$sax$ErrorHandler = XercesDOMAdapter.class$("org.xml.sax.ErrorHandler"));
                arrclass2[0] = class_2;
                Method setErrorHandler = parserClass.getMethod("setErrorHandler", arrclass2);
                setErrorHandler.invoke(parser, new BuilderErrorHandler());
            }
            Class[] arrclass3 = new Class[1];
            Class class_3 = class$org$xml$sax$InputSource != null ? class$org$xml$sax$InputSource : (XercesDOMAdapter.class$org$xml$sax$InputSource = XercesDOMAdapter.class$("org.xml.sax.InputSource"));
            arrclass3[0] = class_3;
            Method parse = parserClass.getMethod("parse", arrclass3);
            parse.invoke(parser, new InputSource(in));
            Method getDocument = parserClass.getMethod("getDocument", null);
            Document doc = (Document)getDocument.invoke(parser, null);
            return doc;
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof SAXParseException) {
                SAXParseException parseException = (SAXParseException)targetException;
                throw new JDOMException("Error on line " + parseException.getLineNumber() + " of XML document: " + parseException.getMessage(), e);
            }
            if (targetException instanceof IOException) {
                IOException ioException = (IOException)targetException;
                throw ioException;
            }
            throw new JDOMException(targetException.getMessage(), e);
        }
        catch (Exception e) {
            throw new JDOMException(String.valueOf(e.getClass().getName()) + ": " + e.getMessage(), e);
        }
    }
}

