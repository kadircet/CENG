/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.io;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.io.Io;
import com.cburch.logisim.std.io.Strings;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.IntegerFactory;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class SevenSegment
extends ManagedComponent {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Io.ATTR_COLOR};
    private static final Object[] DEFAULTS = new Object[]{new Color(240, 0, 0)};
    private static Bounds[] SEGMENTS = null;
    private static Color OFF_COLOR = null;
    private static final Icon toolIcon = Icons.getIcon("7seg.gif");

    private SevenSegment(Location loc, AttributeSet attrs) {
        super(loc, attrs, 8);
        this.setPins();
    }

    private void setPins() {
        Location p = this.getLocation();
        this.setEnd(0, p.translate(20, 0), BitWidth.ONE, 1);
        this.setEnd(1, p.translate(30, 0), BitWidth.ONE, 1);
        this.setEnd(2, p.translate(20, 60), BitWidth.ONE, 1);
        this.setEnd(3, p.translate(10, 60), BitWidth.ONE, 1);
        this.setEnd(4, p.translate(0, 60), BitWidth.ONE, 1);
        this.setEnd(5, p.translate(10, 0), BitWidth.ONE, 1);
        this.setEnd(6, p.translate(0, 0), BitWidth.ONE, 1);
        this.setEnd(7, p.translate(30, 60), BitWidth.ONE, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        int summary = 0;
        for (int i = 0; i < 8; ++i) {
            Value val = state.getValue(this.getEndLocation(i));
            if (val != Value.TRUE) continue;
            summary |= 1 << i;
        }
        state.setData(this, IntegerFactory.create(summary));
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Integer data;
        if (SEGMENTS == null) {
            OFF_COLOR = new Color(220, 220, 220);
            SEGMENTS = new Bounds[]{Bounds.create(3, 8, 19, 4), Bounds.create(23, 10, 4, 19), Bounds.create(23, 30, 4, 19), Bounds.create(3, 47, 19, 4), Bounds.create(-2, 30, 4, 19), Bounds.create(-2, 10, 4, 19), Bounds.create(3, 28, 19, 4)};
        }
        int summ = (data = (Integer)context.getCircuitState().getData(this)) == null ? 0 : data;
        Color color = (Color)this.getAttributeSet().getValue(Io.ATTR_COLOR);
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        Graphics g = context.getGraphics();
        context.drawBounds(this);
        g.setColor(Color.BLACK);
        for (int i = 0; i < 7; ++i) {
            Bounds seg = SEGMENTS[i];
            if (context.getShowState()) {
                g.setColor((summ >> i & 1) == 1 ? color : OFF_COLOR);
            }
            g.fillRect(x + seg.getX(), y + seg.getY(), seg.getWidth(), seg.getHeight());
        }
        if (context.getShowState()) {
            g.setColor((summ >> 7 & 1) == 1 ? color : OFF_COLOR);
        }
        g.fillOval(x + 28, y + 48, 5, 5);
        context.drawPins(this);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "7-Segment Display";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("sevenSegmentComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new SevenSegment(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            return Bounds.create(-5, 0, 40, 60);
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

