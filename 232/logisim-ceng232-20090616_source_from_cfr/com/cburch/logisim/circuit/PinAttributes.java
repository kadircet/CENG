/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Probe;
import com.cburch.logisim.circuit.ProbeAttributes;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.BitWidth;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

class PinAttributes
extends ProbeAttributes {
    public static PinAttributes instance = new PinAttributes();
    static final Font labelfont_dflt = new Font("SansSerif", 0, 12);
    private static final List ATTRIBUTES = Arrays.asList(Pin.facing_attr, Pin.type_attr, Pin.width_attr, Pin.threeState_attr, Pin.pull_attr, Pin.label_attr, Pin.labelloc_attr, Pin.labelfont_attr);
    BitWidth width = BitWidth.ONE;
    boolean threeState = true;
    int type = 1;
    Object pull = Pin.pull_none;

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == Pin.width_attr) {
            return this.width;
        }
        if (attr == Pin.threeState_attr) {
            return this.threeState ? Boolean.TRUE : Boolean.FALSE;
        }
        if (attr == Pin.type_attr) {
            return this.type == 2 ? Boolean.TRUE : Boolean.FALSE;
        }
        if (attr == Pin.pull_attr) {
            return this.pull;
        }
        return super.getValue(attr);
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == Pin.width_attr) {
            this.width = (BitWidth)value;
        } else if (attr == Pin.threeState_attr) {
            this.threeState = (Boolean)value;
        } else if (attr == Pin.type_attr) {
            this.type = (Boolean)value != false ? 2 : 1;
        } else if (attr == Pin.pull_attr) {
            this.pull = value;
        } else {
            super.setValue(attr, value);
            return;
        }
        if (this.component != null) {
            this.component.attributeValueChanged(this, attr, value);
        }
        this.fireAttributeValueChanged(attr, value);
    }
}

