/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.proj;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitEvent;
import com.cburch.logisim.circuit.CircuitListener;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.file.LibraryEvent;
import com.cburch.logisim.file.LibraryListener;
import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.tools.AddTool;
import com.cburch.logisim.util.Dag;
import java.util.List;
import java.util.Set;

public class Dependencies {
    private MyListener myListener;
    private Dag depends;

    Dependencies(LogisimFile file) {
        this.myListener = new MyListener();
        this.depends = new Dag();
        this.addDependencies(file);
    }

    public boolean canRemove(Circuit circ) {
        return !this.depends.hasPredecessors(circ);
    }

    public boolean canAdd(Circuit circ, Circuit sub) {
        return this.depends.canFollow(sub, circ);
    }

    private void addDependencies(LogisimFile file) {
        file.addLibraryListener(this.myListener);
        for (Object o : file.getTools()) {
            ComponentFactory src;
            if (!(o instanceof AddTool) || !((src = ((AddTool)o).getFactory()) instanceof Circuit)) continue;
            this.processCircuit((Circuit)src);
        }
    }

    private void processCircuit(Circuit circ) {
        circ.addCircuitListener(this.myListener);
        for (Object obj : circ.getNonWires()) {
            if (!(obj instanceof Subcircuit)) continue;
            ComponentFactory sub = ((Subcircuit)obj).getFactory();
            this.depends.addEdge(circ, sub);
        }
    }

    private class MyListener
    implements LibraryListener,
    CircuitListener {
        private MyListener() {
        }

        @Override
        public void libraryChanged(LibraryEvent e) {
            switch (e.getAction()) {
                case 0: {
                    ComponentFactory factory;
                    if (!(e.getData() instanceof AddTool) || !((factory = ((AddTool)e.getData()).getFactory()) instanceof Circuit)) break;
                    Dependencies.this.processCircuit((Circuit)factory);
                    break;
                }
                case 1: {
                    ComponentFactory factory;
                    if (!(e.getData() instanceof AddTool) || !((factory = ((AddTool)e.getData()).getFactory()) instanceof Circuit)) break;
                    Circuit circ = (Circuit)factory;
                    Dependencies.this.depends.removeNode(circ);
                    circ.removeCircuitListener(this);
                }
            }
        }

        @Override
        public void circuitChanged(CircuitEvent e) {
            switch (e.getAction()) {
                case 1: {
                    Component comp = (Component)e.getData();
                    if (!(comp instanceof Subcircuit)) break;
                    Dependencies.this.depends.addEdge(e.getCircuit(), comp.getFactory());
                    break;
                }
                case 2: {
                    Component comp = (Component)e.getData();
                    if (!(comp instanceof Subcircuit)) break;
                    Circuit sub = (Circuit)comp.getFactory();
                    boolean found = false;
                    for (Component o : e.getCircuit().getNonWires()) {
                        if (!(o instanceof Subcircuit) || o.getFactory() != sub) continue;
                        found = true;
                        break;
                    }
                    if (found) break;
                    Dependencies.this.depends.removeEdge(e.getCircuit(), sub);
                    break;
                }
                case 5: {
                    Dependencies.this.depends.removeNode(e.getCircuit());
                }
            }
        }
    }

}

