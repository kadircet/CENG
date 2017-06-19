/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponent;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public abstract class ManagedComponent
extends AbstractComponent {
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private Location loc;
    private AttributeSet attrs;
    private ArrayList ends;
    private List ends_view;
    private Bounds bounds = null;

    public ManagedComponent(Location loc, AttributeSet attrs, int num_ends) {
        this.loc = loc;
        this.attrs = attrs;
        this.ends = new ArrayList(num_ends);
        this.ends_view = Collections.unmodifiableList(this.ends);
    }

    @Override
    public abstract ComponentFactory getFactory();

    @Override
    public void addComponentListener(ComponentListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeComponentListener(ComponentListener l) {
        this.listeners.remove(l);
    }

    protected void fireEndChanged(ComponentEvent e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ComponentListener l = (ComponentListener)it.next();
            l.endChanged(e);
        }
    }

    protected void fireComponentInvalidated(ComponentEvent e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ComponentListener l = (ComponentListener)it.next();
            l.componentInvalidated(e);
        }
    }

    @Override
    public Location getLocation() {
        return this.loc;
    }

    @Override
    public AttributeSet getAttributeSet() {
        return this.attrs;
    }

    @Override
    public Bounds getBounds() {
        if (this.bounds == null) {
            Location loc = this.getLocation();
            this.bounds = this.getFactory().getOffsetBounds(this.getAttributeSet()).translate(loc.getX(), loc.getY());
        }
        return this.bounds;
    }

    @Override
    public List getEnds() {
        return this.ends_view;
    }

    @Override
    public abstract void propagate(CircuitState var1);

    public void clearManager() {
        Iterator<E> it = this.ends.iterator();
        while (it.hasNext()) {
            this.fireEndChanged(new ComponentEvent(this, it.next(), null));
        }
        this.ends.clear();
        this.bounds = null;
    }

    public void setBounds(Bounds bounds) {
        this.bounds = bounds;
    }

    public void setAttributeSet(AttributeSet value) {
        this.attrs = value;
    }

    public void setEnd(int i, EndData data) {
        if (i == this.ends.size()) {
            this.ends.add(data);
            this.fireEndChanged(new ComponentEvent(this, null, data));
        } else {
            EndData old = (EndData)this.ends.get(i);
            if (old == null || !old.equals(data)) {
                this.ends.set(i, data);
                this.fireEndChanged(new ComponentEvent(this, old, data));
            }
        }
    }

    public void setEnd(int i, Location end, BitWidth width, int type) {
        this.setEnd(i, new EndData(end, width, type));
    }

    public void setEnd(int i, Location end, BitWidth width, int type, boolean exclusive) {
        this.setEnd(i, new EndData(end, width, type, exclusive));
    }

    public Location getEndLocation(int i) {
        return this.getEnd(i).getLocation();
    }

    @Override
    public void expose(ComponentDrawContext context) {
        Bounds bounds = this.getBounds();
        java.awt.Component dest = context.getDestination();
        if (bounds != null) {
            dest.repaint(bounds.getX() - 5, bounds.getY() - 5, bounds.getWidth() + 10, bounds.getHeight() + 10);
        }
    }

    @Override
    public Object getFeature(Object key) {
        return null;
    }
}

