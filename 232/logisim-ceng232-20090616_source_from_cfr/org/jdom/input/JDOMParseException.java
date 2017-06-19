/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.input;

import org.jdom.Document;
import org.jdom.JDOMException;
import org.xml.sax.SAXParseException;

public class JDOMParseException
extends JDOMException {
    private static final String CVS_ID = "@(#) $RCSfile: JDOMParseException.java,v $ $Revision: 1.7 $ $Date: 2004/02/17 02:29:24 $ $Name: jdom_1_0 $";
    private final Document partialDocument;

    public JDOMParseException(String message, Throwable cause) {
        this(message, cause, null);
    }

    public JDOMParseException(String message, Throwable cause, Document partialDocument) {
        super(message, cause);
        this.partialDocument = partialDocument;
    }

    public int getColumnNumber() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getColumnNumber() : -1;
    }

    public int getLineNumber() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getLineNumber() : -1;
    }

    public Document getPartialDocument() {
        return this.partialDocument;
    }

    public String getPublicId() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getPublicId() : null;
    }

    public String getSystemId() {
        return this.getCause() instanceof SAXParseException ? ((SAXParseException)this.getCause()).getSystemId() : null;
    }
}

