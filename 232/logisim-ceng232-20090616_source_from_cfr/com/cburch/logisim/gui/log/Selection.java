/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.SelectionItem;
import java.util.ArrayList;

class Selection {
    private CircuitState root;
    private Model model;
    private ArrayList components;

    public Selection(CircuitState root, Model model) {
        this.root = root;
        this.model = model;
        this.components = new ArrayList();
    }

    public void addModelListener(ModelListener l) {
        this.model.addModelListener(l);
    }

    public void removeModelListener(ModelListener l) {
        this.model.removeModelListener(l);
    }

    public CircuitState getCircuitState() {
        return this.root;
    }

    public int size() {
        return this.components.size();
    }

    public SelectionItem get(int index) {
        return (SelectionItem)this.components.get(index);
    }

    public int indexOf(SelectionItem value) {
        return this.components.indexOf(value);
    }

    public void add(SelectionItem item) {
        this.components.add(item);
        this.model.fireSelectionChanged(new ModelEvent());
    }

    public void remove(int index) {
        this.components.remove(index);
        this.model.fireSelectionChanged(new ModelEvent());
    }

    public void move(int fromIndex, int toIndex) {
        if (fromIndex == toIndex) {
            return;
        }
        Object o = this.components.remove(fromIndex);
        this.components.add(toIndex, o);
        this.model.fireSelectionChanged(new ModelEvent());
    }
}

