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

public class JAXPDOMAdapter
extends AbstractDOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: JAXPDOMAdapter.java,v $ $Revision: 1.12 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";
    static /* synthetic */ Class class$org$xml$sax$ErrorHandler;
    static /* synthetic */ Class class$java$io$InputStream;

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
            Class.forName("javax.xml.transform.Transformer");
            Class factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
            Object jaxpParser = newDocBuilder.invoke(factory, null);
            Class parserClass = jaxpParser.getClass();
            Method newDoc = parserClass.getMethod("newDocument", null);
            Document domDoc = (Document)newDoc.invoke(jaxpParser, null);
            return domDoc;
        }
        catch (Exception e) {
            throw new JDOMException("Reflection failed while creating new JAXP document", e);
        }
    }

    public Document getDocument(InputStream in, boolean validate) throws IOException, JDOMException {
        try {
            Class.forName("javax.xml.transform.Transformer");
            Class factoryClass = Class.forName("javax.xml.parsers.DocumentBuilderFactory");
            Method newParserInstance = factoryClass.getMethod("newInstance", null);
            Object factory = newParserInstance.invoke(null, null);
            Method setValidating = factoryClass.getMethod("setValidating", Boolean.TYPE);
            setValidating.invoke(factory, new Boolean(validate));
            Method setNamespaceAware = factoryClass.getMethod("setNamespaceAware", Boolean.TYPE);
            setNamespaceAware.invoke(factory, Boolean.TRUE);
            Method newDocBuilder = factoryClass.getMethod("newDocumentBuilder", null);
            Object jaxpParser = newDocBuilder.invoke(factory, null);
            Class parserClass = jaxpParser.getClass();
            Class[] arrclass = new Class[1];
            Class class_ = class$org$xml$sax$ErrorHandler != null ? class$org$xml$sax$ErrorHandler : (JAXPDOMAdapter.class$org$xml$sax$ErrorHandler = JAXPDOMAdapter.class$("org.xml.sax.ErrorHandler"));
            arrclass[0] = class_;
            Method setErrorHandler = parserClass.getMethod("setErrorHandler", arrclass);
            setErrorHandler.invoke(jaxpParser, new BuilderErrorHandler());
            Class[] arrclass2 = new Class[1];
            Class class_2 = class$java$io$InputStream != null ? class$java$io$InputStream : (JAXPDOMAdapter.class$java$io$InputStream = JAXPDOMAdapter.class$("java.io.InputStream"));
            arrclass2[0] = class_2;
            Method parse = parserClass.getMethod("parse", arrclass2);
            Document domDoc = (Document)parse.invoke(jaxpParser, in);
            return domDoc;
        }
        catch (InvocationTargetException e) {
            Throwable targetException = e.getTargetException();
            if (targetException instanceof IOException) {
                throw (IOException)targetException;
            }
            throw new JDOMException(targetException.getMessage(), targetException);
        }
        catch (Exception e) {
            throw new JDOMException("Reflection failed while parsing a document with JAXP", e);
        }
    }
}

