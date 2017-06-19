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
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.arith.Arithmetic;
import com.cburch.logisim.std.arith.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class Adder
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    static final int PER_DELAY = 1;
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr};
    private static final Icon toolIcon = Icons.getIcon("adder.gif");
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int OUT = 2;
    private static final int C_IN = 3;
    private static final int C_OUT = 4;

    private Adder(Location loc, AttributeSet attrs) {
        super(loc, attrs, 5);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Location pt = this.getLocation();
        this.setEnd(0, pt.translate(-40, -10), data, 1);
        this.setEnd(1, pt.translate(-40, 10), data, 1);
        this.setEnd(2, pt, data, 2);
        this.setEnd(3, pt.translate(-20, -20), BitWidth.ONE, 1);
        this.setEnd(4, pt.translate(-20, 20), BitWidth.ONE, 2);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        BitWidth dataWidth = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Value a = state.getValue(this.getEndLocation(0));
        Value b = state.getValue(this.getEndLocation(1));
        Value c_in = state.getValue(this.getEndLocation(3));
        Value[] outs = Adder.computeSum(dataWidth, a, b, c_in);
        int delay = (dataWidth.getWidth() + 2) * 1;
        state.setValue(this.getEndLocation(2), outs[0], this, delay);
        state.setValue(this.getEndLocation(4), outs[1], this, delay);
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
        Graphics g = context.getGraphics();
        context.drawBounds(this);
        g.setColor(Color.GRAY);
        context.drawPin(this, 0);
        context.drawPin(this, 1);
        context.drawPin(this, 2);
        context.drawPin(this, 3, "c in", Direction.NORTH);
        context.drawPin(this, 4, "c out", Direction.SOUTH);
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.BLACK);
        g.drawLine(x - 15, y, x - 5, y);
        g.drawLine(x - 10, y - 5, x - 10, y + 5);
        GraphicsUtil.switchToWidth(g, 1);
    }

    static Value[] computeSum(BitWidth width, Value a, Value b, Value c_in) {
        int w = width.getWidth();
        if (c_in == Value.UNKNOWN || c_in == Value.NIL) {
            c_in = Value.FALSE;
        }
        if (a.isFullyDefined() && b.isFullyDefined() && c_in.isFullyDefined()) {
            if (w >= 32) {
                long mask = (1 << w) - 1;
                long ax = (long)a.toIntValue() & mask;
                long bx = (long)b.toIntValue() & mask;
                long cx = (long)c_in.toIntValue() & mask;
                long sum = ax + bx + cx;
                Value[] arrvalue = new Value[2];
                arrvalue[0] = Value.createKnown(width, (int)sum);
                arrvalue[1] = (sum >> w & 1) == 0 ? Value.FALSE : Value.TRUE;
                return arrvalue;
            }
            int sum = a.toIntValue() + b.toIntValue() + c_in.toIntValue();
            Value[] arrvalue = new Value[2];
            arrvalue[0] = Value.createKnown(width, sum);
            arrvalue[1] = (sum >> w & 1) == 0 ? Value.FALSE : Value.TRUE;
            return arrvalue;
        }
        Value[] bits = new Value[w];
        Value carry = c_in;
        for (int i = 0; i < w; ++i) {
            if (carry == Value.ERROR) {
                bits[i] = Value.ERROR;
                continue;
            }
            if (carry == Value.UNKNOWN) {
                bits[i] = Value.UNKNOWN;
                continue;
            }
            Value ab = a.get(i);
            Value bb = b.get(i);
            if (ab == Value.ERROR || bb == Value.ERROR) {
                bits[i] = Value.ERROR;
                carry = Value.ERROR;
                continue;
            }
            if (ab == Value.UNKNOWN || bb == Value.UNKNOWN) {
                bits[i] = Value.UNKNOWN;
                carry = Value.UNKNOWN;
                continue;
            }
            int sum = (ab == Value.TRUE ? 1 : 0) + (bb == Value.TRUE ? 1 : 0) + (carry == Value.TRUE ? 1 : 0);
            bits[i] = (sum & 1) == 1 ? Value.TRUE : Value.FALSE;
            carry = sum >= 2 ? Value.TRUE : Value.FALSE;
        }
        return new Value[]{Value.create(bits), carry};
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Adder";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("adderComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Adder(loc, attrs);
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

