/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.WireFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.tools.CustomHandles;
import com.cburch.logisim.util.Cache;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.util.AbstractList;
import java.util.Arrays;
import java.util.List;

public final class Wire
implements com.cburch.logisim.comp.Component,
AttributeSet,
CustomHandles {
    public static final int WIDTH = 3;
    public static final Object horz_value = "horizontal";
    public static final Object vert_value = "vertical";
    public static final Attribute dir_attr = Attributes.forOption("direction", Strings.getter("wireDirectionAttr"), new AttributeOption[]{new AttributeOption("horizontal", Strings.getter("wireDirectionHorzOption")), new AttributeOption("vertical", Strings.getter("wireDirectionVertOption"))});
    public static final Attribute len_attr = Attributes.forInteger("length", Strings.getter("wireLengthAttr"));
    private static final List ATTRIBUTES = Arrays.asList(dir_attr, len_attr);
    private static final Cache cache = new Cache();
    final Location e0;
    final Location e1;
    final boolean is_x_equal;

    public static Wire create(Location e0, Location e1) {
        return (Wire)cache.get(new Wire(e0, e1));
    }

    private Wire(Location e0, Location e1) {
        boolean bl = this.is_x_equal = e0.getX() == e1.getX();
        if (this.is_x_equal) {
            if (e0.getY() > e1.getY()) {
                this.e0 = e1;
                this.e1 = e0;
            } else {
                this.e0 = e0;
                this.e1 = e1;
            }
        } else if (e0.getX() > e1.getX()) {
            this.e0 = e1;
            this.e1 = e0;
        } else {
            this.e0 = e0;
            this.e1 = e1;
        }
    }

    public boolean equals(Object other) {
        if (!(other instanceof Wire)) {
            return false;
        }
        Wire w = (Wire)other;
        return w.e0.equals(this.e0) && w.e1.equals(this.e1);
    }

    public int hashCode() {
        return this.e0.hashCode() * 31 + this.e1.hashCode();
    }

    public int getLength() {
        return this.e1.getY() - this.e0.getY() + (this.e1.getX() - this.e0.getX());
    }

    public String toString() {
        return "Wire[" + this.e0 + "-" + this.e1 + "]";
    }

    @Override
    public void addComponentListener(ComponentListener e) {
    }

    @Override
    public void removeComponentListener(ComponentListener e) {
    }

    @Override
    public ComponentFactory getFactory() {
        return WireFactory.instance;
    }

    @Override
    public AttributeSet getAttributeSet() {
        return this;
    }

    @Override
    public Location getLocation() {
        return this.e0;
    }

    @Override
    public Bounds getBounds() {
        int x0 = this.e0.getX();
        int y0 = this.e0.getY();
        return Bounds.create(x0 - 2, y0 - 2, this.e1.getX() - x0 + 5, this.e1.getY() - y0 + 5);
    }

    @Override
    public Bounds getBounds(Graphics g) {
        return this.getBounds();
    }

    @Override
    public boolean contains(Location q) {
        int qx = q.getX();
        int qy = q.getY();
        if (this.is_x_equal) {
            int wx = this.e0.getX();
            return qx >= wx - 2 && qx <= wx + 2 && this.e0.getY() <= qy && qy <= this.e1.getY();
        }
        int wy = this.e0.getY();
        return qy >= wy - 2 && qy <= wy + 2 && this.e0.getX() <= qx && qx <= this.e1.getX();
    }

    @Override
    public boolean contains(Location pt, Graphics g) {
        return this.contains(pt);
    }

    @Override
    public List getEnds() {
        return new EndList();
    }

    @Override
    public EndData getEnd(int index) {
        Location loc = this.getEndLocation(index);
        return new EndData(loc, BitWidth.UNKNOWN, 3);
    }

    @Override
    public boolean endsAt(Location pt) {
        return this.e0.equals(pt) || this.e1.equals(pt);
    }

    @Override
    public void propagate(CircuitState state) {
        state.markPointAsDirty(this.e0);
        state.markPointAsDirty(this.e1);
    }

    @Override
    public void expose(ComponentDrawContext context) {
        Component dest = context.getDestination();
        int x0 = this.e0.getX();
        int y0 = this.e0.getY();
        dest.repaint(x0 - 5, y0 - 5, this.e1.getX() - x0 + 10, this.e1.getY() - y0 + 10);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        CircuitState state = context.getCircuitState();
        Graphics g = context.getGraphics();
        GraphicsUtil.switchToWidth(g, 3);
        g.setColor(state.getValue(this.e0).getColor());
        g.drawLine(this.e0.getX(), this.e0.getY(), this.e1.getX(), this.e1.getY());
    }

    @Override
    public Object getFeature(Object key) {
        if (key == CustomHandles.class) {
            return this;
        }
        return null;
    }

    @Override
    public Object clone() {
        return this;
    }

    @Override
    public void addAttributeListener(AttributeListener l) {
    }

    @Override
    public void removeAttributeListener(AttributeListener l) {
    }

    @Override
    public List getAttributes() {
        return ATTRIBUTES;
    }

    @Override
    public boolean containsAttribute(Attribute attr) {
        return ATTRIBUTES.contains(attr);
    }

    @Override
    public Attribute getAttribute(String name) {
        for (Attribute attr : ATTRIBUTES) {
            if (!name.equals(attr.getName())) continue;
            return attr;
        }
        return null;
    }

    @Override
    public boolean isReadOnly(Attribute attr) {
        return true;
    }

    @Override
    public void setReadOnly(Attribute attr, boolean value) {
        throw new UnsupportedOperationException();
    }

    @Override
    public Object getValue(Attribute attr) {
        if (attr == dir_attr) {
            return this.is_x_equal ? vert_value : horz_value;
        }
        if (attr == len_attr) {
            return IntegerFactory.create(this.getLength());
        }
        return null;
    }

    @Override
    public void setValue(Attribute attr, Object value) {
        throw new IllegalArgumentException("read only attribute");
    }

    public boolean isVertical() {
        return this.is_x_equal;
    }

    public Location getEndLocation(int index) {
        return index == 0 ? this.e0 : this.e1;
    }

    public Location getEnd0() {
        return this.e0;
    }

    public Location getEnd1() {
        return this.e1;
    }

    public Location getOtherEnd(Location loc) {
        return loc.equals(this.e0) ? this.e1 : this.e0;
    }

    public boolean sharesEnd(Wire other) {
        return this.e0.equals(other.e0) || this.e1.equals(other.e0) || this.e0.equals(other.e1) || this.e1.equals(other.e1);
    }

    public boolean overlaps(Wire other) {
        return this.overlaps(other.e0, other.e1);
    }

    private boolean overlaps(Location q0, Location q1) {
        if (this.is_x_equal) {
            int x0 = q0.getX();
            return x0 == q1.getX() && x0 == this.e0.getX() && this.e1.getY() >= q0.getY() && this.e0.getY() <= q1.getY();
        }
        int y0 = q0.getY();
        return y0 == q1.getY() && y0 == this.e0.getY() && this.e1.getX() >= q0.getX() && this.e0.getX() <= q1.getX();
    }

    @Override
    public void drawHandles(ComponentDrawContext context) {
        context.drawHandle(this.e0);
        context.drawHandle(this.e1);
    }

    private class EndList
    extends AbstractList {
        private EndList() {
        }

        @Override
        public Object get(int i) {
            return Wire.this.getEnd(i);
        }

        @Override
        public int size() {
            return 2;
        }
    }

}

