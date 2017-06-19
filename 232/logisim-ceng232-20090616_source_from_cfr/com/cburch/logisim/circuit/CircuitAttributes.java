/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.Direction;
import java.util.Arrays;
import java.util.List;

class CircuitAttributes
extends AbstractAttributeSet {
    public static final Attribute FACING_ATTR = Pin.facing_attr;
    public static final Attribute NAME_ATTR = Attributes.forString("circuit", Strings.getter("circuitName"));
    private static final List ATTRIBUTES = Arrays.asList(FACING_ATTR, NAME_ATTR);
    private Circuit source;
    private Subcircuit comp;
    private Direction facing = Direction.EAST;

    public CircuitAttributes(Circuit source) {
        this.source = source;
    }

    void setSubcircuit(Subcircuit value) {
        this.comp = value;
    }

    public Direction getFacing() {
        return this.facing;
    }

    @Override
    protected void copyInto(AbstractAttributeSet dest) {
        CircuitAttributes other = (CircuitAttributes)dest;
        other.comp = null;
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        return this.comp != null;
    }

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == FACING_ATTR) {
            return this.facing;
        }
        if (attr == NAME_ATTR) {
            return this.source.getName();
        }
        return null;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        if (attr == FACING_ATTR) {
            this.facing = (Direction)value;
            this.fireAttributeValueChanged(FACING_ATTR, value);
        }
    }
}

