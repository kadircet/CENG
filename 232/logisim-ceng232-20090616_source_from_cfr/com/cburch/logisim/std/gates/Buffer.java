/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ExpressionComputer;
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
import com.cburch.logisim.std.gates.GateAttributes;
import com.cburch.logisim.std.gates.Strings;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;
import javax.swing.Icon;

class Buffer
extends ManagedComponent
implements AttributeListener,
ExpressionComputer {
    public static ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{GateAttributes.facing_attr, GateAttributes.width_attr};
    private static final Object[] DEFAULTS = new Object[]{Direction.EAST, BitWidth.ONE};
    private static final Icon toolIcon = Icons.getIcon("bufferGate.gif");

    public Buffer(Location loc, AttributeSet attrs) {
        super(loc, attrs, 2);
        attrs.addAttributeListener(this);
        attrs.setReadOnly(GateAttributes.facing_attr, true);
        this.setPins();
    }

    private void setPins() {
        AttributeSet attrs = this.getAttributeSet();
        Direction dir = (Direction)attrs.getValue(GateAttributes.facing_attr);
        BitWidth w = (BitWidth)attrs.getValue(GateAttributes.width_attr);
        Location loc0 = this.getLocation();
        Location loc1 = loc0.translate(dir.reverse(), 20);
        this.setEnd(0, loc0, w, 2);
        this.setEnd(1, loc1, w, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value in = state.getValue(this.getEndLocation(1));
        state.setValue(this.getEndLocation(0), in, this, 1);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == GateAttributes.width_attr) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        g.setColor(Color.BLACK);
        Buffer.drawBase(g, this.getAttributeSet(), x, y);
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == ExpressionComputer.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public void computeExpression(Map expressionMap) {
        Expression e = (Expression)expressionMap.get(this.getEndLocation(1));
        if (e != null) {
            expressionMap.put(this.getEndLocation(0), e);
        }
    }

    private static void drawBase(Graphics oldG, AttributeSet attrs, int x, int y) {
        Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
        Graphics g = oldG;
        if (facing != Direction.EAST && oldG instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.rotate(- facing.toRadians(), x, y);
            g = g2;
        }
        GraphicsUtil.switchToWidth(g, 2);
        int[] xp = new int[4];
        int[] yp = new int[4];
        xp[0] = x;
        yp[0] = y;
        xp[1] = x - 19;
        yp[1] = y - 7;
        xp[2] = x - 19;
        yp[2] = y + 7;
        xp[3] = x;
        yp[3] = y;
        g.drawPolyline(xp, yp, 4);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Buffer";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("bufferComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Buffer(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
            if (facing == Direction.SOUTH) {
                return Bounds.create(-9, -20, 18, 20);
            }
            if (facing == Direction.NORTH) {
                return Bounds.create(-9, 0, 18, 20);
            }
            if (facing == Direction.WEST) {
                return Bounds.create(0, -9, 20, 18);
            }
            return Bounds.create(-20, -9, 20, 18);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            g.setColor(color);
            Buffer.drawBase(g, attrs, x, y);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            g.setColor(Color.black);
            if (toolIcon != null) {
                toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                g.setColor(Color.black);
                int[] xp = new int[4];
                int[] yp = new int[4];
                xp[0] = x + 17;
                yp[0] = y + 10;
                xp[1] = x + 3;
                yp[1] = y + 3;
                xp[2] = x + 3;
                yp[2] = y + 17;
                xp[3] = x + 17;
                yp[3] = y + 10;
                g.drawPolyline(xp, yp, 4);
            }
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return GateAttributes.facing_attr;
            }
            return super.getFeature(key, attrs);
        }
    }

}

