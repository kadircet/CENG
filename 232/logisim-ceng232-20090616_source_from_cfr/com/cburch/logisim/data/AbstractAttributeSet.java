/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import java.util.ArrayList;
import java.util.List;

public abstract class AbstractAttributeSet
implements Cloneable,
AttributeSet {
    private ArrayList listeners = null;

    @Override
    public Object clone() {
        AbstractAttributeSet ret;
        try {
            ret = (AbstractAttributeSet)super.clone();
        }
        catch (CloneNotSupportedException ex) {
            throw new UnsupportedOperationException();
        }
        ret.listeners = new ArrayList();
        this.copyInto(ret);
        return ret;
    }

    @Override
    public void addAttributeListener(AttributeListener l) {
        if (this.listeners == null) {
            this.listeners = new ArrayList();
        }
        this.listeners.add(l);
    }

    @Override
    public void removeAttributeListener(AttributeListener l) {
        this.listeners.remove(l);
        if (this.listeners.isEmpty()) {
            this.listeners = null;
        }
    }

    protected void fireAttributeValueChanged(Attribute attr, Object value) {
        if (this.listeners != null) {
            AttributeEvent event = new AttributeEvent(this, attr, value);
            int n = this.listeners.size();
            for (int i = 0; i < n; ++i) {
                AttributeListener l = (AttributeListener)this.listeners.get(i);
                l.attributeValueChanged(event);
            }
        }
    }

    protected void fireAttributeListChanged() {
        if (this.listeners != null) {
            AttributeEvent event = new AttributeEvent(this);
            int n = this.listeners.size();
            for (int i = 0; i < n; ++i) {
                AttributeListener l = (AttributeListener)this.listeners.get(i);
                l.attributeListChanged(event);
            }
        }
    }

    @Override
    public boolean containsAttribute(Attribute attr) {
        return this.getAttributes().contains(attr);
    }

    @Override
    public Attribute getAttribute(String name) {
        for (Attribute attr : this.getAttributes()) {
            if (!attr.getName().equals(name)) continue;
            return attr;
        }
        return null;
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        return false;
    }

    @Override
    public void setReadOnly(Attribute attr, boolean value) {
        throw new UnsupportedOperationException();
    }

    protected abstract void copyInto(AbstractAttributeSet var1);

    @Override
    public abstract List getAttributes();

    @Override
    public abstract Object getValue(Attribute var1);

    @Override
    public abstract void setValue(Attribute var1, Object var2);
}

