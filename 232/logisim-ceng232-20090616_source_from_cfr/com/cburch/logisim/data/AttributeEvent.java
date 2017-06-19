/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;

public class AttributeEvent {
    private AttributeSet source;
    private Attribute attr;
    private Object value;

    public AttributeEvent(AttributeSet source, Attribute attr, Object value) {
        this.source = source;
        this.attr = attr;
        this.value = value;
    }

    public AttributeEvent(AttributeSet source) {
        this(source, null, null);
    }

    public Attribute getAttribute() {
        return this.attr;
    }

    public AttributeSet getSource() {
        return this.source;
    }

    public Object getValue() {
        return this.value;
    }
}

