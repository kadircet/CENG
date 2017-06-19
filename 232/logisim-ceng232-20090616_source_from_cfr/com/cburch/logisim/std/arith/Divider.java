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

class Divider
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    static final int PER_DELAY = 1;
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr};
    private static final Icon toolIcon = Icons.getIcon("divider.gif");
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int OUT = 2;
    private static final int UPPER = 3;
    private static final int REM = 4;

    private Divider(Location loc, AttributeSet attrs) {
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
        Value upper = state.getValue(this.getEndLocation(3));
        Value[] outs = Divider.computeResult(dataWidth, a, b, upper);
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
        context.drawPin(this, 3, Strings.get("dividerUpperInput"), Direction.NORTH);
        context.drawPin(this, 4, Strings.get("dividerRemainderOutput"), Direction.SOUTH);
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.BLACK);
        g.fillOval(x - 12, y - 7, 4, 4);
        g.drawLine(x - 15, y, x - 5, y);
        g.fillOval(x - 12, y + 3, 4, 4);
        GraphicsUtil.switchToWidth(g, 1);
    }

    static Value[] computeResult(BitWidth width, Value a, Value b, Value upper) {
        int w = width.getWidth();
        if (upper == Value.NIL || upper.isUnknown()) {
            upper = Value.createKnown(width, 0);
        }
        if (a.isFullyDefined() && b.isFullyDefined() && upper.isFullyDefined()) {
            long num = (long)upper.toIntValue() << w | (long)a.toIntValue() & 0xFFFFFFFFL;
            long den = (long)b.toIntValue() & 0xFFFFFFFFL;
            if (den == 0) {
                den = 1;
            }
            long result = num / den;
            long rem = num % den;
            if (rem < 0) {
                if (den >= 0) {
                    rem += den;
                    --result;
                } else {
                    rem -= den;
                    ++result;
                }
            }
            return new Value[]{Value.createKnown(width, (int)result), Value.createKnown(width, (int)rem)};
        }
        if (a.isErrorValue() || b.isErrorValue() || upper.isErrorValue()) {
            return new Value[]{Value.createError(width), Value.createError(width)};
        }
        return new Value[]{Value.createUnknown(width), Value.createUnknown(width)};
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Divider";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("dividerComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Divider(loc, attrs);
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

