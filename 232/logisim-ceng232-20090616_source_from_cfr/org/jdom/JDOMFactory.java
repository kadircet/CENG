/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.util.Map;
import org.jdom.Attribute;
import org.jdom.CDATA;
import org.jdom.Comment;
import org.jdom.Content;
import org.jdom.DocType;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.EntityRef;
import org.jdom.Namespace;
import org.jdom.Parent;
import org.jdom.ProcessingInstruction;
import org.jdom.Text;

public interface JDOMFactory {
    public void addContent(Parent var1, Content var2);

    public void addNamespaceDeclaration(Element var1, Namespace var2);

    public Attribute attribute(String var1, String var2);

    public Attribute attribute(String var1, String var2, int var3);

    public Attribute attribute(String var1, String var2, int var3, Namespace var4);

    public Attribute attribute(String var1, String var2, Namespace var3);

    public CDATA cdata(String var1);

    public Comment comment(String var1);

    public DocType docType(String var1);

    public DocType docType(String var1, String var2);

    public DocType docType(String var1, String var2, String var3);

    public Document document(Element var1);

    public Document document(Element var1, DocType var2);

    public Document document(Element var1, DocType var2, String var3);

    public Element element(String var1);

    public Element element(String var1, String var2);

    public Element element(String var1, String var2, String var3);

    public Element element(String var1, Namespace var2);

    public EntityRef entityRef(String var1);

    public EntityRef entityRef(String var1, String var2);

    public EntityRef entityRef(String var1, String var2, String var3);

    public ProcessingInstruction processingInstruction(String var1, String var2);

    public ProcessingInstruction processingInstruction(String var1, Map var2);

    public void setAttribute(Element var1, Attribute var2);

    public Text text(String var1);
}

