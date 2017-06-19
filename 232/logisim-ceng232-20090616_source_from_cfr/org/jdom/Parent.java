/*
 * Decompiled with CFR 0_114.
 */
package org.jdom;

import java.io.Serializable;
import java.util.Iterator;
import java.util.List;
import org.jdom.Content;
import org.jdom.Document;
import org.jdom.filter.Filter;

public interface Parent
extends Cloneable,
Serializable {
    public Object clone();

    public List cloneContent();

    public List getContent();

    public Content getContent(int var1);

    public List getContent(Filter var1);

    public int getContentSize();

    public Iterator getDescendants();

    public Iterator getDescendants(Filter var1);

    public Document getDocument();

    public Parent getParent();

    public int indexOf(Content var1);

    public List removeContent();

    public Content removeContent(int var1);

    public boolean removeContent(Content var1);

    public List removeContent(Filter var1);
}

