/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.gui.main.Selection;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.util.SmallSet;
import com.cburch.logisim.util.StringGetter;
import java.util.Collection;
import java.util.Collections;
import java.util.Iterator;
import java.util.Set;

public class ComponentAction
extends Action
implements Cloneable {
    private static final StringGetter UNKNOWN = Strings.getter("unknownComponentAction");
    private Circuit circuit;
    private StringGetter descriptor;
    private SmallSet toAdd = new SmallSet();
    private SmallSet toRemove = new SmallSet();
    private SmallSet toAddIncidental = new SmallSet();
    private SmallSet toRemoveIncidental = new SmallSet();
    private Set toAddView = null;
    private Set toRemoveView = null;
    private Set toAddIncidentalView = null;
    private Set toRemoveIncidentalView = null;

    public ComponentAction(Circuit circuit) {
        this(circuit, UNKNOWN);
    }

    ComponentAction(Circuit circuit, StringGetter descriptor) {
        this.circuit = circuit;
        this.descriptor = descriptor;
    }

    void addToAdditions(Component comp) {
        this.toAdd.add(comp);
    }

    void addToRemovals(Component comp) {
        this.toRemove.add(comp);
    }

    void addToIncidentalAdditions(Component comp) {
        this.toAddIncidental.add(comp);
    }

    void addToIncidentalRemovals(Component comp) {
        this.toRemoveIncidental.add(comp);
    }

    public Object clone() {
        ComponentAction ret = (ComponentAction)this.clone();
        ret.toAdd = (SmallSet)this.toAdd.clone();
        ret.toRemove = (SmallSet)this.toRemove.clone();
        ret.toAddIncidental = (SmallSet)this.toAddIncidental.clone();
        ret.toRemoveIncidental = (SmallSet)this.toRemoveIncidental.clone();
        ret.toAddView = null;
        ret.toRemoveView = null;
        ret.toAddIncidentalView = null;
        ret.toRemoveIncidentalView = null;
        return ret;
    }

    @Override
    public Action append(Action otherAction) {
        if (!(otherAction instanceof ComponentAction)) {
            return super.append(otherAction);
        }
        ComponentAction other = (ComponentAction)otherAction;
        if (this.descriptor.equals(UNKNOWN)) {
            this.descriptor = other.descriptor;
        }
        this.toAdd.removeAll(other.toRemove);
        this.toAdd.removeAll(other.toRemoveIncidental);
        this.toRemove.removeAll(other.toAdd);
        this.toRemove.removeAll(other.toAddIncidental);
        this.toAddIncidental.removeAll(other.toRemove);
        this.toAddIncidental.removeAll(other.toRemoveIncidental);
        this.toRemoveIncidental.removeAll(other.toAdd);
        this.toRemoveIncidental.removeAll(other.toAddIncidental);
        this.toAdd.addAll(other.toAdd);
        this.toAddIncidental.addAll(other.toAddIncidental);
        this.toRemove.addAll(other.toRemove);
        this.toRemoveIncidental.addAll(other.toRemoveIncidental);
        return this;
    }

    public void setCircuit(Circuit circ) {
        this.circuit = circ;
    }

    public Collection getAdditions() {
        if (this.toAddView == null) {
            this.toAddView = Collections.unmodifiableSet(this.toAdd);
        }
        return this.toAddView;
    }

    public Collection getRemovals() {
        if (this.toRemoveView == null) {
            this.toRemoveView = Collections.unmodifiableSet(this.toRemove);
        }
        return this.toRemoveView;
    }

    public Collection getIncidentalAdditions() {
        if (this.toAddIncidentalView == null) {
            this.toAddIncidentalView = Collections.unmodifiableSet(this.toAddIncidental);
        }
        return this.toAddIncidentalView;
    }

    public Collection getIncidentalRemovals() {
        if (this.toRemoveIncidentalView == null) {
            this.toRemoveIncidentalView = Collections.unmodifiableSet(this.toRemoveIncidental);
        }
        return this.toRemoveIncidentalView;
    }

    @Override
    public String getName() {
        return this.descriptor.get();
    }

    @Override
    public void doIt(Project proj) {
        for (Component c2 : this.toRemove) {
            proj.getSelection().remove(c2);
            this.circuit.remove(c2);
        }
        for (Component c2 : this.toRemoveIncidental) {
            proj.getSelection().remove(c2);
            this.circuit.remove(c2);
        }
        Iterator it = this.toAdd.iterator();
        while (it.hasNext()) {
            this.circuit.add((Component)it.next());
        }
        it = this.toAddIncidental.iterator();
        while (it.hasNext()) {
            this.circuit.add((Component)it.next());
        }
    }

    @Override
    public void undo(Project proj) {
        for (Component c2 : this.toAdd) {
            proj.getSelection().remove(c2);
            this.circuit.remove(c2);
        }
        for (Component c2 : this.toAddIncidental) {
            proj.getSelection().remove(c2);
            this.circuit.remove(c2);
        }
        Iterator it = this.toRemove.iterator();
        while (it.hasNext()) {
            this.circuit.add((Component)it.next());
        }
        it = this.toRemoveIncidental.iterator();
        while (it.hasNext()) {
            this.circuit.add((Component)it.next());
        }
    }
}

