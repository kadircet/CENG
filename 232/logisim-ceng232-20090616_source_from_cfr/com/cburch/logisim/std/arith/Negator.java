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
import com.cburch.logisim.util.Icons;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class Negator
extends ManagedComponent
implements AttributeListener {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Arithmetic.data_attr};
    private static final Icon toolIcon = Icons.getIcon("negator.gif");
    private static final int IN = 0;
    private static final int OUT = 1;

    private Negator(Location loc, AttributeSet attrs) {
        super(loc, attrs, 2);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Location pt = this.getLocation();
        this.setEnd(0, pt.translate(-40, 0), data, 1);
        this.setEnd(1, pt, data, 2);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value out;
        BitWidth dataWidth = (BitWidth)this.getAttributeSet().getValue(Arithmetic.data_attr);
        Value in = state.getValue(this.getEndLocation(0));
        if (in.isFullyDefined()) {
            out = Value.createKnown(in.getBitWidth(), - in.toIntValue());
        } else {
            int pos;
            Value[] bits = in.getAll();
            Value fill = Value.FALSE;
            for (pos = 0; pos < bits.length; ++pos) {
                if (bits[pos] == Value.FALSE) {
                    bits[pos] = fill;
                    continue;
                }
                if (bits[pos] == Value.TRUE) {
                    if (fill != Value.FALSE) {
                        bits[pos] = fill;
                    }
                    ++pos;
                    break;
                }
                if (bits[pos] == Value.ERROR) {
                    fill = Value.ERROR;
                    continue;
                }
                if (fill == Value.FALSE) {
                    fill = bits[pos];
                    continue;
                }
                bits[pos] = fill;
            }
            while (pos < bits.length) {
                if (bits[pos] == Value.TRUE) {
                    bits[pos] = Value.FALSE;
                } else if (bits[pos] == Value.FALSE) {
                    bits[pos] = Value.TRUE;
                }
                ++pos;
            }
            out = Value.create(bits);
        }
        int delay = (dataWidth.getWidth() + 2) * 1;
        state.setValue(this.getEndLocation(1), out, this, delay);
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
        context.drawPin(this, 1, "-x", Direction.WEST);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Negator";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("negatorComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, new Object[]{Arithmetic.data_dflt});
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Negator(loc, attrs);
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

