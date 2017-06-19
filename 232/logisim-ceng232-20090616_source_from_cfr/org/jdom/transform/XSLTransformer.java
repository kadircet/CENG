/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.transform;

import java.io.File;
import java.io.InputStream;
import java.io.Reader;
import java.util.List;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Templates;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamSource;
import org.jdom.Document;
import org.jdom.transform.JDOMResult;
import org.jdom.transform.JDOMSource;
import org.jdom.transform.XSLTransformException;

public class XSLTransformer {
    private static final String CVS_ID = "@(#) $RCSfile: XSLTransformer.java,v $ $Revision: 1.2 $ $Date: 2004/02/06 09:28:32 $ $Name: jdom_1_0 $";
    private Templates templates;

    public XSLTransformer(File stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(InputStream stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(Reader stylesheet) throws XSLTransformException {
        this(new StreamSource(stylesheet));
    }

    public XSLTransformer(String stylesheetSystemId) throws XSLTransformException {
        this(new StreamSource(stylesheetSystemId));
    }

    private XSLTransformer(Source stylesheet) throws XSLTransformException {
        try {
            this.templates = TransformerFactory.newInstance().newTemplates(stylesheet);
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not construct XSLTransformer", e);
        }
    }

    public XSLTransformer(Document stylesheet) throws XSLTransformException {
        this(new JDOMSource(stylesheet));
    }

    public List transform(List inputNodes) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputNodes);
        JDOMResult result = new JDOMResult();
        try {
            this.templates.newTransformer().transform(source, result);
            return result.getResult();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }

    public Document transform(Document inputDoc) throws XSLTransformException {
        JDOMSource source = new JDOMSource(inputDoc);
        JDOMResult result = new JDOMResult();
        try {
            this.templates.newTransformer().transform(source, result);
            return result.getDocument();
        }
        catch (TransformerException e) {
            throw new XSLTransformException("Could not perform transformation", e);
        }
    }
}

