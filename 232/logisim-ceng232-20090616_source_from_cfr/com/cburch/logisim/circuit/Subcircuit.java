/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitAttributes;
import com.cburch.logisim.circuit.CircuitPinListener;
import com.cburch.logisim.circuit.CircuitPins;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.MenuExtender;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

public class Subcircuit
extends ManagedComponent
implements MenuExtender,
ToolTipMaker {
    static int lastId = 0;
    int id = lastId++;
    private Listener listener;
    private Circuit source;

    public Subcircuit(Location loc, Circuit source, AttributeSet attrs) {
        super(loc, attrs, source.pins.getPins().size());
        this.listener = new Listener();
        this.source = source;
        CircuitAttributes circAttrs = (CircuitAttributes)attrs;
        circAttrs.setSubcircuit(this);
        source.configureComponent(this);
        source.addPinListener(this.listener);
    }

    public String toString() {
        return "Subcircuit" + this.id + "[" + this.source.getName() + "]";
    }

    public Circuit getSubcircuit() {
        return this.source;
    }

    public CircuitState getSubstate(CircuitState superState) {
        CircuitState subState = (CircuitState)superState.getData(this);
        if (subState == null) {
            subState = new CircuitState(superState.getProject(), this.source);
            superState.setData(this, subState);
        }
        return subState;
    }

    @Override
    public ComponentFactory getFactory() {
        return this.source;
    }

    @Override
    public void propagate(CircuitState superState) {
        CircuitState subState = this.getSubstate(superState);
        int i = 0;
        for (EndData end : this.getEnds()) {
            Pin pin = this.source.pins.getSubcircuitPin(i);
            Location super_loc = end.getLocation();
            Location sub_loc = pin.getLocation();
            if (pin.isInputPin()) {
                Value old_val;
                Value new_val = superState.getValue(super_loc);
                if (!new_val.equals(old_val = pin.getValue(subState))) {
                    pin.setValue(subState, new_val);
                    pin.propagate(subState);
                }
            } else {
                Value val = subState.getValue(sub_loc);
                superState.setValue(super_loc, val, this, 1);
            }
            ++i;
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        int ax;
        int an;
        int ay;
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        g.setColor(Color.GRAY);
        GraphicsUtil.switchToWidth(g, 2);
        Direction facing = ((CircuitAttributes)this.getAttributeSet()).getFacing();
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
        context.drawBounds(this);
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == MenuExtender.class) {
            return this;
        }
        if (key == ToolTipMaker.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public void configureMenu(JPopupMenu menu, Project proj) {
        menu.add(new ViewItem(this, proj));
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        for (int i = this.getEnds().size() - 1; i >= 0; --i) {
            Pin pin;
            String label;
            if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10 || (label = (pin = this.source.pins.getSubcircuitPin(i)).getLabel()) == null || label.length() <= 0) continue;
            return label;
        }
        return StringUtil.format(Strings.get("subcircuitCircuitTip"), this.source.getDisplayName());
    }

    private class Listener
    implements CircuitPinListener {
        private Listener() {
        }

        @Override
        public void pinAdded() {
            Subcircuit.this.source.configureComponent(Subcircuit.this);
        }

        @Override
        public void pinRemoved() {
            Subcircuit.this.source.configureComponent(Subcircuit.this);
        }

        @Override
        public void pinChanged() {
            Subcircuit.this.source.configureComponent(Subcircuit.this);
        }
    }

    private class ViewItem
    extends JMenuItem
    implements ActionListener {
        Subcircuit comp;
        Project proj;

        ViewItem(Subcircuit comp, Project proj) {
            super(StringUtil.format(Strings.get("subcircuitViewItem"), comp.source.getName()));
            this.comp = comp;
            this.proj = proj;
            this.addActionListener(this);
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            CircuitState superState = this.proj.getCircuitState();
            if (superState == null) {
                return;
            }
            CircuitState subState = this.comp.getSubstate(superState);
            if (subState == null) {
                return;
            }
            this.proj.setCircuitState(subState);
        }
    }

}

