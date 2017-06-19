/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.AttributeOptionInterface;
import com.cburch.logisim.util.StringGetter;

public class AttributeOption
implements AttributeOptionInterface {
    private Object value;
    private String name;
    private StringGetter desc;

    public AttributeOption(Object value, StringGetter desc) {
        this.value = value;
        this.name = value.toString();
        this.desc = desc;
    }

    public AttributeOption(Object value, String name, StringGetter desc) {
        this.value = value;
        this.name = name;
        this.desc = desc;
    }

    @Override
    public Object getValue() {
        return this.value;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String toDisplayString() {
        return this.desc.get();
    }
}

