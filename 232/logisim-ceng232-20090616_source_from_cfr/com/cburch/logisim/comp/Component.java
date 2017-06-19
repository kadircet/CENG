/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentListener;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import java.awt.Graphics;
import java.util.List;

public interface Component {
    public void addComponentListener(ComponentListener var1);

    public void removeComponentListener(ComponentListener var1);

    public ComponentFactory getFactory();

    public AttributeSet getAttributeSet();

    public Location getLocation();

    public Bounds getBounds();

    public Bounds getBounds(Graphics var1);

    public boolean contains(Location var1);

    public boolean contains(Location var1, Graphics var2);

    public void expose(ComponentDrawContext var1);

    public void draw(ComponentDrawContext var1);

    public Object getFeature(Object var1);

    public List getEnds();

    public EndData getEnd(int var1);

    public boolean endsAt(Location var1);

    public void propagate(CircuitState var1);
}

