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
import com.cburch.logisim.std.arith.Adder;
import com.cburch.logisim.std.arith.Arithmetic;
import com.cburch.logisim.std.arith.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class Subtractor
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr};
    private static final Icon toolIcon = Icons.getIcon("subtractor.gif");
    private static final int IN0 = 0;
    private static final int IN1 = 1;
    private static final int OUT = 2;
    private static final int B_IN = 3;
    private static final int B_OUT = 4;

    private Subtractor(Location loc, AttributeSet attrs) {
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
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Value a = state.getValue(this.getEndLocation(0));
        Value b = state.getValue(this.getEndLocation(1));
        Value b_in = state.getValue(this.getEndLocation(3));
        if (b_in == Value.UNKNOWN || b_in == Value.NIL) {
            b_in = Value.FALSE;
        }
        Value[] outs = Adder.computeSum(data, a, b.not(), b_in.not());
        int delay = (data.getWidth() + 4) * 1;
        state.setValue(this.getEndLocation(2), outs[0], this, delay);
        state.setValue(this.getEndLocation(4), outs[1].not(), this, delay);
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
        context.drawPin(this, 3, "b in", Direction.NORTH);
        context.drawPin(this, 4, "b out", Direction.SOUTH);
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.BLACK);
        g.drawLine(x - 15, y, x - 5, y);
        GraphicsUtil.switchToWidth(g, 1);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Subtractor";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("subtractorComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Subtractor(loc, attrs);
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

