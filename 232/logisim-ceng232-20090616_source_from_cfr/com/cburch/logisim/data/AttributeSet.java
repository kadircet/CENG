/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeListener;
import java.util.List;

public interface AttributeSet {
    public Object clone();

    public void addAttributeListener(AttributeListener var1);

    public void removeAttributeListener(AttributeListener var1);

    public List getAttributes();

    public boolean containsAttribute(Attribute var1);

    public Attribute getAttribute(String var1);

    public boolean isReadOnly(Attribute var1);

    public void setReadOnly(Attribute var1, boolean var2);

    public Object getValue(Attribute var1);

    public void setValue(Attribute var1, Object var2);
}

