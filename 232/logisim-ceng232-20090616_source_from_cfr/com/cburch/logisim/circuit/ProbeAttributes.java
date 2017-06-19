/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Probe;
import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Direction;
import java.awt.Font;
import java.util.Arrays;
import java.util.List;

class ProbeAttributes
extends AbstractAttributeSet {
    public static ProbeAttributes instance = new ProbeAttributes();
    static final Font labelfont_dflt = new Font("SansSerif", 0, 12);
    private static final List ATTRIBUTES = Arrays.asList(Pin.facing_attr, Pin.label_attr, Pin.labelloc_attr, Pin.labelfont_attr, RadixOption.ATTRIBUTE);
    Probe component = null;
    Direction facing = Direction.EAST;
    String label = "";
    Direction labelloc = Direction.WEST;
    Font labelfont = labelfont_dflt;
    RadixOption radix = RadixOption.RADIX_2;

    @Override
    protected void copyInto(AbstractAttributeSet destObj) {
        ProbeAttributes dest = (ProbeAttributes)destObj;
        dest.component = null;
    }

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == Pin.facing_attr) {
            return this.facing;
        }
        if (attr == Pin.label_attr) {
            return this.label;
        }
        if (attr == Pin.labelloc_attr) {
            return this.labelloc;
        }
        if (attr == Pin.labelfont_attr) {
            return this.labelfont;
        }
        if (attr == RadixOption.ATTRIBUTE) {
            return this.radix;
        }
        return null;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == Pin.facing_attr) {
            this.facing = (Direction)value;
        } else if (attr == Pin.label_attr) {
            this.label = (String)value;
        } else if (attr == Pin.labelloc_attr) {
            this.labelloc = (Direction)value;
        } else if (attr == Pin.labelfont_attr) {
            this.labelfont = (Font)value;
        } else if (attr == RadixOption.ATTRIBUTE) {
            this.radix = (RadixOption)value;
        } else {
            throw new IllegalArgumentException("unknown attribute");
        }
        if (this.component != null) {
            this.component.attributeValueChanged(this, attr, value);
        }
        this.fireAttributeValueChanged(attr, value);
    }
}

