/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.CircuitWires;
import com.cburch.logisim.circuit.Clock;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Propagator;
import com.cburch.logisim.circuit.Splitter;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentState;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.ArraySet;
import com.cburch.logisim.util.SmallSet;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class CircuitState {
    private MyCircuitListener myCircuitListener;
    private Propagator base;
    private Project proj;
    private Circuit circuit;
    private CircuitState parentState;
    private Subcircuit parentComp;
    private ArraySet substates;
    private CircuitWires.State wireData;
    private HashMap componentData;
    private HashMap values;
    private SmallSet dirtyComponents;
    private SmallSet dirtyPoints;
    HashMap causes;
    private static int lastId = 0;
    private int id;

    public CircuitState(Project proj, Circuit circuit) {
        this.myCircuitListener = new MyCircuitListener();
        this.base = null;
        this.parentState = null;
        this.parentComp = null;
        this.substates = new ArraySet();
        this.wireData = null;
        this.componentData = new HashMap();
        this.values = new HashMap();
        this.dirtyComponents = new SmallSet();
        this.dirtyPoints = new SmallSet();
        this.causes = new HashMap();
        this.id = lastId++;
        this.proj = proj;
        this.circuit = circuit;
        circuit.addCircuitListener(this.myCircuitListener);
    }

    Project getProject() {
        return this.proj;
    }

    Subcircuit getSubcircuit() {
        return this.parentComp;
    }

    public CircuitState cloneState() {
        CircuitState ret = new CircuitState(this.proj, this.circuit);
        ret.copyFrom(this, new Propagator(ret));
        ret.parentComp = null;
        ret.parentState = null;
        return ret;
    }

    private void copyFrom(CircuitState src, Propagator base) {
        Object newValue;
        Object oldValue;
        this.base = base;
        this.parentComp = src.parentComp;
        this.parentState = src.parentState;
        HashMap<CircuitState, CircuitState> substateData = new HashMap<CircuitState, CircuitState>();
        for (CircuitState oldSub : src.substates) {
            CircuitState newSub = new CircuitState(src.proj, oldSub.circuit);
            newSub.copyFrom(oldSub, base);
            this.substates.add(newSub);
            substateData.put(oldSub, newSub);
        }
        for (Object key2 : src.componentData.keySet()) {
            oldValue = src.componentData.get(key2);
            if (oldValue instanceof CircuitState) {
                newValue = substateData.get(oldValue);
                if (newValue != null) {
                    this.componentData.put(key2, newValue);
                    continue;
                }
                this.componentData.remove(key2);
                continue;
            }
            newValue = oldValue instanceof ComponentState ? ((ComponentState)oldValue).clone() : oldValue;
            this.componentData.put(key2, newValue);
        }
        for (Object key2 : src.causes.keySet()) {
            oldValue = (Propagator.SetData)src.causes.get(key2);
            newValue = oldValue.cloneFor(this);
            this.causes.put(key2, newValue);
        }
        if (src.wireData != null) {
            this.wireData = (CircuitWires.State)src.wireData.clone();
        }
        this.values.putAll(src.values);
        this.dirtyComponents.addAll(src.dirtyComponents);
        this.dirtyPoints.addAll(src.dirtyPoints);
    }

    public String toString() {
        return "State" + this.id + "[" + this.circuit.getName() + "]";
    }

    public Circuit getCircuit() {
        return this.circuit;
    }

    public CircuitState getParentState() {
        return this.parentState;
    }

    public Set getSubstates() {
        return this.substates;
    }

    Propagator getPropagator() {
        if (this.base == null) {
            this.base = new Propagator(this);
            this.markAllComponentsDirty();
        }
        return this.base;
    }

    public void drawOscillatingPoints(ComponentDrawContext context) {
        if (this.base != null) {
            this.base.drawOscillatingPoints(context);
        }
    }

    public Object getData(Component comp) {
        return this.componentData.get(comp);
    }

    public void setData(Component comp, Object data) {
        CircuitState newState;
        CircuitState oldState;
        if (comp instanceof Subcircuit && (oldState = (CircuitState)this.componentData.get(comp)) != (newState = (CircuitState)data)) {
            if (oldState != null && oldState.parentComp == comp) {
                this.substates.remove(oldState);
                oldState.parentState = null;
                oldState.parentComp = null;
            }
            if (newState != null && newState.parentState != this) {
                this.substates.add(newState);
                newState.base = this.base;
                newState.parentState = this;
                newState.parentComp = (Subcircuit)comp;
                newState.markAllComponentsDirty();
            }
        }
        this.componentData.put(comp, data);
    }

    public Value getValue(Location pt) {
        Value ret = (Value)this.values.get(pt);
        if (ret != null) {
            return ret;
        }
        BitWidth wid = this.circuit.getWidth(pt);
        return Value.createUnknown(wid);
    }

    public void setValue(Location pt, Value val, Component cause, int delay) {
        if (this.base != null) {
            this.base.setValue(this, pt, val, cause, delay);
        }
    }

    public void markComponentAsDirty(Component comp) {
        this.dirtyComponents.add(comp);
    }

    public void markPointAsDirty(Location pt) {
        this.dirtyPoints.add(pt);
    }

    boolean isSubstate() {
        return this.parentState != null;
    }

    void processDirtyComponents() {
        int i;
        if (!this.dirtyComponents.isEmpty()) {
            Object[] toProcess = this.dirtyComponents.toArray();
            this.dirtyComponents.clear();
            for (i = 0; i < toProcess.length; ++i) {
                Component comp = (Component)toProcess[i];
                comp.propagate(this);
                if (!(comp instanceof Pin) || this.parentState == null) continue;
                this.parentComp.propagate(this.parentState);
            }
        }
        Object[] subs = this.substates.toArray();
        int n = subs.length;
        for (i = 0; i < n; ++i) {
            CircuitState substate = (CircuitState)subs[i];
            substate.processDirtyComponents();
        }
    }

    void processDirtyPoints() {
        if (!this.dirtyPoints.isEmpty()) {
            this.circuit.wires.propagate(this, this.dirtyPoints);
            this.dirtyPoints.clear();
        }
        Object[] subs = this.substates.toArray();
        int n = subs.length;
        for (int i = 0; i < n; ++i) {
            CircuitState substate = (CircuitState)subs[i];
            substate.processDirtyPoints();
        }
    }

    void reset() {
        this.wireData = null;
        Iterator it = this.componentData.keySet().iterator();
        while (it.hasNext()) {
            Object comp = it.next();
            if (comp instanceof Subcircuit) continue;
            it.remove();
        }
        this.values.clear();
        this.dirtyComponents.clear();
        this.dirtyPoints.clear();
        this.causes.clear();
        this.markAllComponentsDirty();
        for (CircuitState sub : this.substates) {
            sub.reset();
        }
    }

    boolean tick(int ticks) {
        boolean ret = false;
        ArrayList clocks = this.circuit.getClocks();
        for (int i = 0; i < clocks.size(); ++i) {
            Clock clock = (Clock)clocks.get(i);
            ret |= clock.tick(this, ticks);
        }
        Object[] subs = this.substates.toArray();
        int n = subs.length;
        for (int i2 = 0; i2 < n; ++i2) {
            CircuitState substate = (CircuitState)subs[i2];
            ret |= substate.tick(ticks);
        }
        return ret;
    }

    CircuitWires.State getWireData() {
        return this.wireData;
    }

    void setWireData(CircuitWires.State data) {
        this.wireData = data;
    }

    Value getComponentOutputAt(Location p) {
        Propagator.SetData cause_list = (Propagator.SetData)this.causes.get(p);
        return Propagator.computeValue(cause_list);
    }

    Value getValueByWire(Location p) {
        return (Value)this.values.get(p);
    }

    void setValueByWire(Location p, Value v) {
        Object old;
        boolean changed;
        if (v == Value.NIL) {
            old = this.values.remove(p);
            changed = old != null && old != Value.NIL;
        } else {
            old = this.values.put(p, v);
            boolean bl = changed = !v.equals(old);
        }
        if (changed) {
            boolean found = false;
            for (Object obj : this.circuit.getComponents(p)) {
                if (obj instanceof Wire || obj instanceof Splitter) continue;
                found = true;
                Component c = (Component)obj;
                this.markComponentAsDirty(c);
            }
            if (found && this.base != null) {
                this.base.locationTouched(this, p);
            }
        }
    }

    private void markAllComponentsDirty() {
        this.dirtyComponents.addAll(this.circuit.getNonWires());
    }

    private class MyCircuitListener
    implements CircuitListener {
        private MyCircuitListener() {
        }

        @Override
        public void circuitChanged(CircuitEvent event) {
            int action = event.getAction();
            if (action == 1) {
                Component comp = (Component)event.getData();
                if (comp instanceof Wire) {
                    Wire w = (Wire)comp;
                    CircuitState.this.markPointAsDirty(w.getEnd0());
                    CircuitState.this.markPointAsDirty(w.getEnd1());
                } else {
                    CircuitState.this.markComponentAsDirty(comp);
                    if (CircuitState.this.base != null) {
                        CircuitState.this.base.checkComponentEnds(CircuitState.this, comp);
                    }
                }
            } else if (action == 2) {
                CircuitState substate;
                Component comp = (Component)event.getData();
                if (comp instanceof Subcircuit && (substate = (CircuitState)CircuitState.this.getData(comp)) != null && substate.parentComp == comp) {
                    CircuitState.this.substates.remove(substate);
                    substate.parentState = null;
                    substate.parentComp = null;
                }
                if (comp instanceof Wire) {
                    Wire w = (Wire)comp;
                    CircuitState.this.markPointAsDirty(w.getEnd0());
                    CircuitState.this.markPointAsDirty(w.getEnd1());
                } else {
                    if (CircuitState.this.base != null) {
                        CircuitState.this.base.checkComponentEnds(CircuitState.this, comp);
                    }
                    CircuitState.this.dirtyComponents.remove(comp);
                }
            } else if (action == 5) {
                CircuitState.this.substates.clear();
                CircuitState.this.wireData = null;
                CircuitState.this.componentData.clear();
                CircuitState.this.values.clear();
                CircuitState.this.dirtyComponents.clear();
                CircuitState.this.dirtyPoints.clear();
                CircuitState.this.causes.clear();
            } else if (action == 3) {
                Component comp = (Component)event.getData();
                CircuitState.this.markComponentAsDirty(comp);
                if (CircuitState.this.base != null) {
                    CircuitState.this.base.checkComponentEnds(CircuitState.this, comp);
                }
            } else if (action == 4) {
                Component comp = (Component)event.getData();
                CircuitState.this.markComponentAsDirty(comp);
                if (CircuitState.this.base != null) {
                    CircuitState.this.base.checkComponentEnds(CircuitState.this, comp);
                }
            }
        }
    }

}

