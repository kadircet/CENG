/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.arith;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.arith.Arithmetic;
import com.cburch.logisim.std.arith.Strings;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringGetter;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class Comparator
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    private static final AttributeOption SIGNED_OPTION = new AttributeOption("twosComplement", "twosComplement", Strings.getter("twosComplementOption"));
    private static final AttributeOption UNSIGNED_OPTION = new AttributeOption("unsigned", "unsigned", Strings.getter("unsignedOption"));
    private static final Attribute MODE_ATTRIBUTE = Attributes.forOption("mode", Strings.getter("comparatorType"), new AttributeOption[]{SIGNED_OPTION, UNSIGNED_OPTION});
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr, MODE_ATTRIBUTE};
    private static final Icon toolIcon = Icons.getIcon("comparator.gif");
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int GT = 2;
    private static final int EQ = 3;
    private static final int LT = 4;

    private Comparator(Location loc, AttributeSet attrs) {
        super(loc, attrs, 5);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        BitWidth one = BitWidth.ONE;
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Location pt = this.getLocation();
        this.setEnd(0, pt.translate(-40, -10), data, 1);
        this.setEnd(1, pt.translate(-40, 10), data, 1);
        this.setEnd(2, pt.translate(0, -10), one, 2);
        this.setEnd(3, pt, one, 2);
        this.setEnd(4, pt.translate(0, 10), one, 2);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        BitWidth dataWidth = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Value gt = Value.FALSE;
        Value eq = Value.TRUE;
        Value lt = Value.FALSE;
        Value a = state.getValue(this.getEndLocation(0));
        Value b = state.getValue(this.getEndLocation(1));
        Value[] ax = a.getAll();
        Value[] bx = b.getAll();
        for (int pos = ax.length - 1; pos >= 0; --pos) {
            Object mode;
            Value ab = ax[pos];
            Value bb = bx[pos];
            if (pos == ax.length - 1 && ab != bb && (mode = this.getAttributeSet().getValue(MODE_ATTRIBUTE)) != UNSIGNED_OPTION) {
                Value t = ab;
                ab = bb;
                bb = t;
            }
            if (ab == Value.ERROR || bb == Value.ERROR) {
                gt = Value.ERROR;
                eq = Value.ERROR;
                lt = Value.ERROR;
                break;
            }
            if (ab == Value.UNKNOWN || bb == Value.UNKNOWN) {
                gt = Value.UNKNOWN;
                eq = Value.UNKNOWN;
                lt = Value.UNKNOWN;
                break;
            }
            if (ab == bb) continue;
            eq = Value.FALSE;
            if (ab == Value.TRUE) {
                gt = Value.TRUE;
                break;
            }
            lt = Value.TRUE;
            break;
        }
        int delay = (dataWidth.getWidth() + 2) * 1;
        state.setValue(this.getEndLocation(2), gt, this, delay);
        state.setValue(this.getEndLocation(3), eq, this, delay);
        state.setValue(this.getEndLocation(4), lt, this, delay);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == Arithmetic.data_attr) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        context.drawBounds(this);
        context.drawPin(this, 0);
        context.drawPin(this, 1);
        context.drawPin(this, 2, ">", Direction.WEST);
        context.drawPin(this, 3, "=", Direction.WEST);
        context.drawPin(this, 4, "<", Direction.WEST);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Comparator";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("comparatorComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt, SIGNED_OPTION});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Comparator(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            return Bounds.create(-40, -20, 40, 40);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            if (toolIcon != null) {
                toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            }
        }
    }

}

