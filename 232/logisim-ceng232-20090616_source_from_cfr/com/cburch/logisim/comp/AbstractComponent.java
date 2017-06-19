/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import java.awt.Graphics;
import java.util.List;

public abstract class AbstractComponent
implements Component {
    protected AbstractComponent() {
    }

    @Override
    public abstract ComponentFactory getFactory();

    @Override
    public abstract Location getLocation();

    @Override
    public abstract Bounds getBounds();

    @Override
    public Bounds getBounds(Graphics g) {
        return this.getBounds();
    }

    @Override
    public boolean contains(Location pt) {
        Bounds bds = this.getBounds();
        if (bds == null) {
            return false;
        }
        return bds.contains(pt, 1);
    }

    @Override
    public boolean contains(Location pt, Graphics g) {
        Bounds bds = this.getBounds(g);
        if (bds == null) {
            return false;
        }
        return bds.contains(pt, 1);
    }

    @Override
    public abstract List getEnds();

    @Override
    public EndData getEnd(int index) {
        return (EndData)this.getEnds().get(index);
    }

    @Override
    public boolean endsAt(Location pt) {
        for (EndData data : this.getEnds()) {
            if (!data.getLocation().equals(pt)) continue;
            return true;
        }
        return false;
    }

    @Override
    public abstract void propagate(CircuitState var1);
}

