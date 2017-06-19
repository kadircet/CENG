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

class Multiplier
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    static final int PER_DELAY = 1;
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr};
    private static final Icon toolIcon = Icons.getIcon("multiplier.gif");
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int OUT = 2;
    private static final int C_IN = 3;
    private static final int C_OUT = 4;

    private Multiplier(Location loc, AttributeSet attrs) {
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
        this.setEnd(3, pt.translate(-20, -20), data, 1);
        this.setEnd(4, pt.translate(-20, 20), data, 2);
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
        Value[] outs = Multiplier.computeProduct(dataWidth, a, b, c_in);
        int delay = dataWidth.getWidth() * (dataWidth.getWidth() + 2) * 1;
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
        g.drawLine(x - 15, y - 5, x - 5, y + 5);
        g.drawLine(x - 15, y + 5, x - 5, y - 5);
        GraphicsUtil.switchToWidth(g, 1);
    }

    static Value[] computeProduct(BitWidth width, Value a, Value b, Value c_in) {
        int w = width.getWidth();
        if (c_in == Value.NIL || c_in.isUnknown()) {
            c_in = Value.createKnown(width, 0);
        }
        if (a.isFullyDefined() && b.isFullyDefined() && c_in.isFullyDefined()) {
            long sum = (long)a.toIntValue() * (long)b.toIntValue() + (long)c_in.toIntValue();
            return new Value[]{Value.createKnown(width, (int)sum), Value.createKnown(width, (int)(sum >> w))};
        }
        Value[] avals = a.getAll();
        int aOk = Multiplier.findUnknown(avals);
        int aErr = Multiplier.findError(avals);
        int ax = Multiplier.getKnown(avals);
        Value[] bvals = b.getAll();
        int bOk = Multiplier.findUnknown(bvals);
        int bErr = Multiplier.findError(bvals);
        int bx = Multiplier.getKnown(bvals);
        Value[] cvals = c_in.getAll();
        int cOk = Multiplier.findUnknown(cvals);
        int cErr = Multiplier.findError(cvals);
        int cx = Multiplier.getKnown(cvals);
        int known = Math.min(Math.min(aOk, bOk), cOk);
        int error = Math.min(Math.min(aErr, bErr), cErr);
        int ret = ax * bx + cx;
        Value[] bits = new Value[w];
        for (int i = 0; i < w; ++i) {
            bits[i] = i < known ? ((ret & 1 << i) != 0 ? Value.TRUE : Value.FALSE) : (i < error ? Value.UNKNOWN : Value.ERROR);
        }
        Value[] arrvalue = new Value[2];
        arrvalue[0] = Value.create(bits);
        arrvalue[1] = error < w ? Value.createError(width) : Value.createUnknown(width);
        return arrvalue;
    }

    private static int findUnknown(Value[] vals) {
        for (int i = 0; i < vals.length; ++i) {
            if (vals[i].isFullyDefined()) continue;
            return i;
        }
        return vals.length;
    }

    private static int findError(Value[] vals) {
        for (int i = 0; i < vals.length; ++i) {
            if (!vals[i].isErrorValue()) continue;
            return i;
        }
        return vals.length;
    }

    private static int getKnown(Value[] vals) {
        int ret = 0;
        for (int i = 0; i < vals.length; ++i) {
            int val = vals[i].toIntValue();
            if (val < 0) {
                return ret;
            }
            ret |= val << i;
        }
        return ret;
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Multiplier";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("multiplierComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Multiplier(loc, attrs);
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

