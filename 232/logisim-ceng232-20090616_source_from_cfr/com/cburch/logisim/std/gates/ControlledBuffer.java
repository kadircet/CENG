/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.gates;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.EndData;
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
import com.cburch.logisim.tools.WireRepair;
import com.cburch.logisim.tools.WireRepairData;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import javax.swing.Icon;

class ControlledBuffer
extends ManagedComponent
implements AttributeListener,
WireRepair {
    public static ComponentFactory bufferFactory = new Factory(false);
    public static ComponentFactory inverterFactory = new Factory(true);
    private static final Attribute[] ATTRIBUTES = new Attribute[]{GateAttributes.facing_attr, GateAttributes.width_attr};
    private static final Object[] DEFAULTS = new Object[]{Direction.EAST, BitWidth.ONE};
    private static final Icon bufferIcon = Icons.getIcon("controlledBuffer.gif");
    private static final Icon inverterIcon = Icons.getIcon("controlledInverter.gif");
    private Factory src;

    private ControlledBuffer(Location loc, AttributeSet attrs, Factory src) {
        super(loc, attrs, 3);
        this.src = src;
        attrs.setReadOnly(GateAttributes.facing_attr, true);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        AttributeSet attrs = this.getAttributeSet();
        Direction dir = (Direction)attrs.getValue(GateAttributes.facing_attr);
        BitWidth w = (BitWidth)attrs.getValue(GateAttributes.width_attr);
        int d = this.src.isInverter ? 10 : 0;
        Location loc0 = this.getLocation();
        Location loc1 = loc0.translate(dir.reverse(), 20 + d);
        Location loc2 = loc0.translate(dir.reverse(), 10 + d, -10);
        this.setEnd(0, loc0, w, 2);
        this.setEnd(1, loc1, w, 1);
        this.setEnd(2, loc2, BitWidth.ONE, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return this.src;
    }

    @Override
    public void propagate(CircuitState state) {
        Value control = state.getValue(this.getEndLocation(2));
        if (control == Value.TRUE) {
            Value in = state.getValue(this.getEndLocation(1));
            state.setValue(this.getEndLocation(0), this.src.isInverter ? in.not() : in, this, 1);
        } else if (control == Value.ERROR) {
            state.setValue(this.getEndLocation(0), Value.ERROR, this, 1);
        } else {
            state.setValue(this.getEndLocation(0), Value.UNKNOWN, this, 1);
        }
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
        AttributeSet attrs = this.getAttributeSet();
        Graphics g = context.getGraphics();
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        GraphicsUtil.switchToWidth(g, 3);
        EndData e = this.getEnd(2);
        Location pt0 = e.getLocation();
        Direction face = (Direction)attrs.getValue(GateAttributes.facing_attr);
        Location pt1 = pt0.translate(face, 0, -6);
        if (context.getShowState()) {
            CircuitState state = context.getCircuitState();
            g.setColor(state.getValue(pt0).getColor());
        }
        g.drawLine(pt0.getX(), pt0.getY(), pt1.getX(), pt1.getY());
        g.setColor(Color.BLACK);
        ControlledBuffer.drawShape(g, attrs, x, y, this.src.isInverter);
        if (!context.isPrintView()) {
            context.drawPin(this, 0);
            context.drawPin(this, 1);
        }
    }

    @Override
    public Object getFeature(Object key) {
        if (key == WireRepair.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public boolean shouldRepairWire(WireRepairData data) {
        return data.getPoint().equals(this.getEndLocation(2));
    }

    private static void drawShape(Graphics oldG, AttributeSet attrs, int x, int y, boolean isInverter) {
        Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
        Graphics g = oldG;
        if (facing != Direction.EAST && oldG instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.rotate(- facing.toRadians(), x, y);
            g = g2;
        }
        GraphicsUtil.switchToWidth(g, 2);
        int d = isInverter ? 10 : 0;
        int[] xp = new int[4];
        int[] yp = new int[4];
        xp[0] = x - d;
        yp[0] = y;
        xp[1] = x - 19 - d;
        yp[1] = y - 7;
        xp[2] = x - 19 - d;
        yp[2] = y + 7;
        xp[3] = x - d;
        yp[3] = y;
        g.drawPolyline(xp, yp, 4);
        if (isInverter) {
            g.drawOval(x - 9, y - 4, 9, 9);
        }
    }

    private static class Factory
    extends AbstractComponentFactory {
        private boolean isInverter;

        private Factory(boolean isInverter) {
            this.isInverter = isInverter;
        }

        @Override
        public String getName() {
            if (this.isInverter) {
                return "Controlled Inverter";
            }
            return "Controlled Buffer";
        }

        @Override
        public String getDisplayName() {
            if (this.isInverter) {
                return Strings.get("controlledInverterComponent");
            }
            return Strings.get("controlledBufferComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new ControlledBuffer(loc, attrs, this);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            int w = this.isInverter ? 30 : 20;
            Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
            if (facing == Direction.NORTH) {
                return Bounds.create(-10, 0, 20, w);
            }
            if (facing == Direction.SOUTH) {
                return Bounds.create(-10, - w, 20, w);
            }
            if (facing == Direction.WEST) {
                return Bounds.create(0, -10, w, 20);
            }
            return Bounds.create(- w, -10, w, 20);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            g.setColor(color);
            ControlledBuffer.drawShape(g, attrs, x, y, this.isInverter);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            if (this.isInverter) {
                if (inverterIcon != null) {
                    inverterIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
                } else {
                    this.paintHelper(g, x, y);
                }
            } else if (bufferIcon != null) {
                bufferIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                this.paintHelper(g, x + 2, y);
            }
        }

        private void paintHelper(Graphics g, int x, int y) {
            g.setColor(Color.black);
            int[] xp = new int[4];
            int[] yp = new int[4];
            xp[0] = x + 15;
            yp[0] = y + 10;
            xp[1] = x + 1;
            yp[1] = y + 3;
            xp[2] = x + 1;
            yp[2] = y + 17;
            xp[3] = x + 15;
            yp[3] = y + 10;
            g.drawPolyline(xp, yp, 4);
            if (this.isInverter) {
                g.drawOval(x + 13, y + 8, 4, 4);
            }
            g.setColor(Value.FALSE.getColor());
            g.drawLine(x + 8, y + 14, x + 8, y + 18);
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

