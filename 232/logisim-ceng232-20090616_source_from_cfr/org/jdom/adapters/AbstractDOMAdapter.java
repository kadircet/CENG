/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.adapters;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Method;
import org.jdom.DocType;
import org.jdom.JDOMException;
import org.jdom.adapters.DOMAdapter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;

public abstract class AbstractDOMAdapter
implements DOMAdapter {
    private static final String CVS_ID = "@(#) $RCSfile: AbstractDOMAdapter.java,v $ $Revision: 1.20 $ $Date: 2004/02/06 09:28:31 $ $Name: jdom_1_0 $";
    static /* synthetic */ Class class$java$lang$String;

    static /* synthetic */ Class class$(String class$) {
        try {
            return Class.forName(class$);
        }
        catch (ClassNotFoundException forName) {
            throw new NoClassDefFoundError(forName.getMessage());
        }
    }

    public abstract Document createDocument() throws JDOMException;

    public Document createDocument(DocType doctype) throws JDOMException {
        if (doctype == null) {
            return this.createDocument();
        }
        DOMImplementation domImpl = this.createDocument().getImplementation();
        DocumentType domDocType = domImpl.createDocumentType(doctype.getElementName(), doctype.getPublicID(), doctype.getSystemID());
        this.setInternalSubset(domDocType, doctype.getInternalSubset());
        return domImpl.createDocument("http://temporary", doctype.getElementName(), domDocType);
    }

    public Document getDocument(File filename, boolean validate) throws IOException, JDOMException {
        return this.getDocument(new FileInputStream(filename), validate);
    }

    public abstract Document getDocument(InputStream var1, boolean var2) throws IOException, JDOMException;

    protected void setInternalSubset(DocumentType dt, String s) {
        if (dt == null || s == null) {
            return;
        }
        try {
            Class<?> dtclass = dt.getClass();
            Class[] arrclass = new Class[1];
            Class class_ = class$java$lang$String != null ? class$java$lang$String : (AbstractDOMAdapter.class$java$lang$String = AbstractDOMAdapter.class$("java.lang.String"));
            arrclass[0] = class_;
            Method setInternalSubset = dtclass.getMethod("setInternalSubset", arrclass);
            setInternalSubset.invoke(dt, s);
        }
        catch (Exception v2) {}
    }
}

