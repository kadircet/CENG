/*
 * Decompiled with CFR 0_114.
 */
package org.jdom.adapters;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.jdom.DocType;
import org.jdom.JDOMException;
import org.w3c.dom.Document;

public interface DOMAdapter {
    public Document createDocument() throws JDOMException;

    public Document createDocument(DocType var1) throws JDOMException;

    public Document getDocument(File var1, boolean var2) throws IOException, JDOMException;

    public Document getDocument(InputStream var1, boolean var2) throws IOException, JDOMException;
}

