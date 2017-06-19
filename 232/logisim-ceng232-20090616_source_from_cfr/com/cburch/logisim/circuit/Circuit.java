/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitAttributes;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitPinListener;
import com.cburch.logisim.circuit.CircuitPins;
import com.cburch.logisim.circuit.CircuitPoints;
import com.cburch.logisim.circuit.CircuitWires;
import com.cburch.logisim.circuit.Clock;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.CollectionUtil;
import com.cburch.logisim.util.EventSourceWeakSupport;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class Circuit
extends AbstractComponentFactory {
    private MyComponentListener myComponentListener;
    private String name;
    private EventSourceWeakSupport listeners;
    private HashSet comps;
    CircuitPins pins;
    CircuitWires wires;
    private ArrayList clocks;

    public Circuit(String name) {
        this.myComponentListener = new MyComponentListener();
        this.listeners = new EventSourceWeakSupport();
        this.comps = new HashSet();
        this.pins = new CircuitPins();
        this.wires = new CircuitWires();
        this.clocks = new ArrayList();
        this.name = name;
    }

    public void clear() {
        HashSet oldComps = this.comps;
        this.comps = new HashSet();
        this.pins = new CircuitPins();
        this.wires = new CircuitWires();
        this.clocks.clear();
        this.fireEvent(5, oldComps);
    }

    @Override
    public String toString() {
        return this.name;
    }

    public void addCircuitListener(CircuitListener what) {
        this.listeners.add(what);
    }

    public void removeCircuitListener(CircuitListener what) {
        this.listeners.remove(what);
    }

    private void fireEvent(int action, Object data) {
        this.fireEvent(new CircuitEvent(action, this, data));
    }

    private void fireEvent(CircuitEvent event) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            CircuitListener what = (CircuitListener)it.next();
            what.circuitChanged(event);
        }
    }

    void addPinListener(CircuitPinListener l) {
        this.pins.addPinListener(l);
    }

    void removePinListener(CircuitPinListener l) {
        this.pins.removePinListener(l);
    }

    public Set getWidthIncompatibilityData() {
        return this.wires.getWidthIncompatibilityData();
    }

    public BitWidth getWidth(Location p) {
        return this.wires.getWidth(p);
    }

    public Location getWidthDeterminant(Location p) {
        return this.wires.getWidthDeterminant(p);
    }

    public boolean hasConflict(Component comp) {
        return this.wires.points.hasConflict(comp);
    }

    public Component getExclusive(Location loc) {
        return this.wires.points.getExclusive(loc);
    }

    private Set getComponents() {
        return CollectionUtil.createUnmodifiableSetUnion(this.comps, this.wires.getWires());
    }

    public Set getWires() {
        return this.wires.getWires();
    }

    public Set getNonWires() {
        return this.comps;
    }

    public Collection getComponents(Location loc) {
        return this.wires.points.getComponents(loc);
    }

    public Collection getSplitCauses(Location loc) {
        return this.wires.points.getSplitCauses(loc);
    }

    public Collection getWires(Location loc) {
        return this.wires.points.getWires(loc);
    }

    public Collection getNonWires(Location loc) {
        return this.wires.points.getNonWires(loc);
    }

    public Set getSplitLocations() {
        return this.wires.points.getSplitLocations();
    }

    public Collection getAllContaining(Location pt) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.getComponents()) {
            if (!comp.contains(pt)) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getAllContaining(Location pt, Graphics g) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.getComponents()) {
            if (!comp.contains(pt, g)) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getAllWithin(Bounds bds) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.getComponents()) {
            if (!bds.contains(comp.getBounds())) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Collection getAllWithin(Bounds bds, Graphics g) {
        HashSet<Component> ret = new HashSet<Component>();
        for (Component comp : this.getComponents()) {
            if (!bds.contains(comp.getBounds(g))) continue;
            ret.add(comp);
        }
        return ret;
    }

    public Bounds getBounds() {
        Iterator it = this.comps.iterator();
        if (!it.hasNext()) {
            return this.wires.getWireBounds();
        }
        Component first = (Component)it.next();
        Bounds firstBounds = first.getBounds();
        int xMin = firstBounds.getX();
        int yMin = firstBounds.getY();
        int xMax = xMin + firstBounds.getWidth();
        int yMax = yMin + firstBounds.getHeight();
        while (it.hasNext()) {
            Component c = (Component)it.next();
            Bounds bds = c.getBounds();
            int x0 = bds.getX();
            int x1 = x0 + bds.getWidth();
            int y0 = bds.getY();
            int y1 = y0 + bds.getHeight();
            if (x0 < xMin) {
                xMin = x0;
            }
            if (x1 > xMax) {
                xMax = x1;
            }
            if (y0 < yMin) {
                yMin = y0;
            }
            if (y1 <= yMax) continue;
            yMax = y1;
        }
        return Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin).add(this.wires.getWireBounds());
    }

    public Bounds getBounds(Graphics g) {
        Bounds ret = this.wires.getWireBounds();
        int xMin = ret.getX();
        int yMin = ret.getY();
        int xMax = xMin + ret.getWidth();
        int yMax = yMin + ret.getHeight();
        for (Component c : this.comps) {
            Bounds bds = c.getBounds(g);
            int x0 = bds.getX();
            int x1 = x0 + bds.getWidth();
            int y0 = bds.getY();
            int y1 = y0 + bds.getHeight();
            if (x0 < xMin) {
                xMin = x0;
            }
            if (x1 > xMax) {
                xMax = x1;
            }
            if (y0 < yMin) {
                yMin = y0;
            }
            if (y1 <= yMax) continue;
            yMax = y1;
        }
        return Bounds.create(xMin, yMin, xMax - xMin, yMax - yMin);
    }

    ArrayList getClocks() {
        return this.clocks;
    }

    public void setName(String name) {
        this.name = name;
        this.fireEvent(0, name);
    }

    public void add(Component c) {
        if (c instanceof Wire) {
            Wire w = (Wire)c;
            if (w.getEnd0().equals(w.getEnd1())) {
                return;
            }
            this.wires.add(w);
        } else {
            this.wires.add(c);
            this.comps.add(c);
            if (c instanceof Pin) {
                this.pins.addPin((Pin)c);
            } else if (c instanceof Clock) {
                this.clocks.add(c);
            }
            c.addComponentListener(this.myComponentListener);
        }
        this.fireEvent(1, c);
    }

    public void remove(Component c) {
        if (c instanceof Wire) {
            this.wires.remove(c);
        } else {
            this.wires.remove(c);
            this.comps.remove(c);
            if (c instanceof Pin) {
                this.pins.removePin((Pin)c);
            } else if (c instanceof Clock) {
                this.clocks.remove(c);
            }
            c.removeComponentListener(this.myComponentListener);
        }
        this.fireEvent(2, c);
    }

    public void componentChanged(Component c) {
        this.fireEvent(3, c);
    }

    public void draw(ComponentDrawContext context, Collection hidden) {
        Graphics g = context.getGraphics();
        Graphics g_copy = g.create();
        context.setGraphics(g_copy);
        this.wires.draw(context, hidden);
        if (hidden == null || hidden.size() == 0) {
            for (Component c : this.comps) {
                Graphics g_new = g.create();
                context.setGraphics(g_new);
                g_copy.dispose();
                g_copy = g_new;
                c.draw(context);
            }
        } else {
            for (Component c : this.comps) {
                if (hidden.contains(c)) continue;
                Graphics g_new = g.create();
                context.setGraphics(g_new);
                g_copy.dispose();
                g_copy = g_new;
                c.draw(context);
            }
        }
        context.setGraphics(g);
        g_copy.dispose();
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.name;
    }

    @Override
    public Component createComponent(Location loc, AttributeSet attrs) {
        return new Subcircuit(loc, this, attrs);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return this.pins.getOffsetBounds((CircuitAttributes)attrs);
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new CircuitAttributes(this);
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        if (key == FACING_ATTRIBUTE_KEY) {
            return CircuitAttributes.FACING_ATTR;
        }
        return super.getFeature(key, attrs);
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        int ax;
        int an;
        int ay;
        super.drawGhost(context, color, x, y, attrs);
        Graphics g = context.getGraphics();
        Bounds bds = this.getOffsetBounds(attrs).translate(x, y);
        GraphicsUtil.switchToWidth(g, 2);
        Direction facing = ((CircuitAttributes)attrs).getFacing();
        if (facing == Direction.SOUTH) {
            ax = bds.getX() + bds.getWidth() - 1;
            ay = bds.getY() + bds.getHeight() / 2;
            an = 90;
        } else if (facing == Direction.NORTH) {
            ax = bds.getX() + 1;
            ay = bds.getY() + bds.getHeight() / 2;
            an = -90;
        } else if (facing == Direction.WEST) {
            ax = bds.getX() + bds.getWidth() / 2;
            ay = bds.getY() + bds.getHeight() - 1;
            an = 0;
        } else {
            ax = bds.getX() + bds.getWidth() / 2;
            ay = bds.getY() + 1;
            an = 180;
        }
        g.drawArc(ax - 4, ay - 4, 8, 8, an, 180);
        g.setColor(Color.BLACK);
    }

    void configureComponent(Subcircuit comp) {
        this.pins.configureComponent(comp);
    }

    private class MyComponentListener
    implements ComponentListener {
        private MyComponentListener() {
        }

        @Override
        public void endChanged(ComponentEvent e) {
            Component comp = e.getSource();
            EndData oldEnd = (EndData)e.getOldData();
            EndData newEnd = (EndData)e.getData();
            Circuit.this.wires.remove(comp, oldEnd);
            Circuit.this.wires.add(comp, newEnd);
            Circuit.this.fireEvent(4, comp);
        }

        @Override
        public void componentInvalidated(ComponentEvent e) {
            Circuit.this.fireEvent(4, e.getSource());
        }
    }

}

