/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Subcircuit;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Graphics;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

class PropagationPoints {
    private HashSet data = new HashSet();

    PropagationPoints() {
    }

    void add(CircuitState state, Location loc) {
        this.data.add(new Entry(state, loc));
    }

    void clear() {
        this.data.clear();
    }

    boolean isEmpty() {
        return this.data.isEmpty();
    }

    void draw(ComponentDrawContext context) {
        if (this.data.isEmpty()) {
            return;
        }
        CircuitState state = context.getCircuitState();
        HashMap stateMap = new HashMap();
        for (CircuitState s : state.getSubstates()) {
            this.addSubstates(stateMap, s, s);
        }
        Graphics g = context.getGraphics();
        GraphicsUtil.switchToWidth(g, 2);
        for (Entry e : this.data) {
            if (e.state == state) {
                Location p = e.loc;
                g.drawOval(p.getX() - 4, p.getY() - 4, 8, 8);
                continue;
            }
            if (!stateMap.containsKey(e.state)) continue;
            CircuitState substate = (CircuitState)stateMap.get(e.state);
            Subcircuit subcirc = substate.getSubcircuit();
            Bounds b = subcirc.getBounds();
            g.drawRect(b.getX(), b.getY(), b.getWidth(), b.getHeight());
        }
        GraphicsUtil.switchToWidth(g, 1);
    }

    private void addSubstates(HashMap map, CircuitState source, CircuitState value) {
        map.put(source, value);
        for (CircuitState s : source.getSubstates()) {
            this.addSubstates(map, s, value);
        }
    }

    private static class Entry {
        private CircuitState state;
        private Location loc;

        private Entry(CircuitState state, Location loc) {
            this.state = state;
            this.loc = loc;
        }

        public boolean equals(Object other) {
            if (!(other instanceof Entry)) {
                return false;
            }
            Entry o = (Entry)other;
            return this.state.equals(o.state) && this.loc.equals(o.loc);
        }

        public int hashCode() {
            return this.state.hashCode() * 31 + this.loc.hashCode();
        }
    }

}

