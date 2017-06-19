/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.ActionAdd;
import com.cburch.logisim.circuit.ActionRemove;
import com.cburch.logisim.circuit.ActionShorten;
import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.ComponentAction;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.proj.Action;
import com.cburch.logisim.proj.Project;
import java.util.Collection;
import java.util.Collections;

public class CircuitActions {
    private CircuitActions() {
    }

    public static Action setCircuitName(Circuit circuit, String name) {
        return new SetCircuitName(circuit, name);
    }

    public static Action setAttributeValue(Circuit circuit, Component comp, Attribute attr, Object value) {
        return new SetAttributeValue(circuit, comp, attr, value);
    }

    public static ComponentAction addComponent(Circuit circuit, Component comp, boolean tryShortening) {
        ComponentAction ret;
        if (tryShortening && comp instanceof Wire && (ret = ActionShorten.create(circuit, (Wire)comp)) != null) {
            return ret;
        }
        return CircuitActions.addComponents(circuit, Collections.singleton(comp));
    }

    public static ComponentAction addComponents(Circuit circuit, Collection comps) {
        return ActionAdd.create(circuit, comps);
    }

    public static ComponentAction removeComponent(Circuit circuit, Component comp) {
        return CircuitActions.removeComponents(circuit, Collections.singleton(comp));
    }

    public static ComponentAction removeComponents(Circuit circuit, Collection comps) {
        return ActionRemove.create(circuit, comps);
    }

    private static class SetAttributeValue
    extends Action {
        private Circuit circuit;
        private Component comp;
        private Attribute attr;
        private Object newval;
        private Object oldval;

        SetAttributeValue(Circuit circuit, Component comp, Attribute attr, Object value) {
            this.circuit = circuit;
            this.comp = comp;
            this.attr = attr;
            this.newval = value;
        }

        @Override
        public String getName() {
            return Strings.get("changeAttributeAction");
        }

        @Override
        public void doIt(Project proj) {
            AttributeSet attrs = this.comp.getAttributeSet();
            this.oldval = attrs.getValue(this.attr);
            attrs.setValue(this.attr, this.newval);
            this.circuit.componentChanged(this.comp);
        }

        @Override
        public void undo(Project proj) {
            AttributeSet attrs = this.comp.getAttributeSet();
            attrs.setValue(this.attr, this.oldval);
            this.circuit.componentChanged(this.comp);
        }
    }

    private static class SetCircuitName
    extends Action {
        private Circuit circuit;
        private String newval;
        private String oldval;

        SetCircuitName(Circuit circuit, String name) {
            this.circuit = circuit;
            this.newval = name;
        }

        @Override
        public String getName() {
            return Strings.get("renameCircuitAction");
        }

        @Override
        public void doIt(Project proj) {
            this.oldval = this.circuit.getName();
            this.circuit.setName(this.newval);
        }

        @Override
        public void undo(Project proj) {
            this.circuit.setName(this.oldval);
        }
    }

}

