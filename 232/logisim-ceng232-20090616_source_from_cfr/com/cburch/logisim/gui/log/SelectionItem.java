/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.Selection;

class SelectionItem
implements AttributeListener,
CircuitListener {
    private Model model;
    private Subcircuit[] path;
    private Component comp;
    private Object option;
    private int radix = 2;
    private String shortDescriptor;
    private String longDescriptor;

    public SelectionItem(Model model, Subcircuit[] path, Component comp, Object option) {
        this.model = model;
        this.path = path;
        this.comp = comp;
        this.option = option;
        this.computeDescriptors();
        if (path != null) {
            model.getCircuitState().getCircuit().addCircuitListener(this);
            for (int i = 0; i < path.length; ++i) {
                path[i].getAttributeSet().addAttributeListener(this);
                path[i].getSubcircuit().addCircuitListener(this);
            }
        }
        comp.getAttributeSet().addAttributeListener(this);
    }

    private boolean computeDescriptors() {
        boolean changed = false;
        Loggable log = (Loggable)this.comp.getFeature(Loggable.class);
        String newShort = log.getLogName(this.option);
        if (newShort == null || newShort.equals("")) {
            newShort = this.comp.getFactory().getDisplayName() + this.comp.getLocation().toString();
            if (this.option != null) {
                newShort = newShort + "." + this.option.toString();
            }
        }
        if (!newShort.equals(this.shortDescriptor)) {
            changed = true;
            this.shortDescriptor = newShort;
        }
        StringBuffer buf = new StringBuffer();
        for (int i = 0; i < this.path.length; ++i) {
            if (i > 0) {
                buf.append(".");
            }
            buf.append(this.path[i].getFactory().getDisplayName());
            buf.append(this.path[i].getLocation());
            buf.append(".");
        }
        buf.append(this.shortDescriptor);
        String newLong = buf.toString();
        if (!newLong.equals(this.longDescriptor)) {
            changed = true;
            this.longDescriptor = newLong;
        }
        return changed;
    }

    public Component[] getPath() {
        return this.path;
    }

    public Component getComponent() {
        return this.comp;
    }

    public Object getOption() {
        return this.option;
    }

    public int getRadix() {
        return this.radix;
    }

    public void setRadix(int value) {
        this.radix = value;
        this.model.fireSelectionChanged(new ModelEvent());
    }

    public String toShortString() {
        return this.shortDescriptor;
    }

    public String toString() {
        return this.longDescriptor;
    }

    public Value fetchValue(CircuitState root) {
        CircuitState cur = root;
        for (int i = 0; i < this.path.length; ++i) {
            cur = this.path[i].getSubstate(cur);
        }
        Loggable log = (Loggable)this.comp.getFeature(Loggable.class);
        return log == null ? Value.NIL : log.getLogValue(cur, this.option);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        if (this.computeDescriptors()) {
            this.model.fireSelectionChanged(new ModelEvent());
        }
    }

    @Override
    public void circuitChanged(CircuitEvent event) {
        int action = event.getAction();
        if (action == 5 || action == 2) {
            Circuit circ = event.getCircuit();
            Component circComp = null;
            if (circ == this.model.getCircuitState().getCircuit()) {
                circComp = this.path != null && this.path.length > 0 ? this.path[0] : this.comp;
            } else if (this.path != null) {
                for (int i = 0; i < this.path.length; ++i) {
                    if (circ != this.path[i].getSubcircuit()) continue;
                    circComp = i + 1 < this.path.length ? this.path[i + 1] : this.comp;
                }
            }
            if (circComp == null) {
                return;
            }
            if (action == 2 && event.getData() != circComp) {
                return;
            }
            int index = this.model.getSelection().indexOf(this);
            if (index < 0) {
                return;
            }
            this.model.getSelection().remove(index);
        }
    }
}

