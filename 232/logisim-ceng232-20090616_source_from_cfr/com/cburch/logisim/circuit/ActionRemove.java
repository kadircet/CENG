/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitPoints;
import com.cburch.logisim.circuit.ComponentAction;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.circuit.WireUtil;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.StringGetter;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

class ActionRemove {
    private ActionRemove() {
    }

    static ComponentAction create(Circuit circuit, Collection toRemove) {
        StringGetter descriptor;
        if (toRemove.isEmpty()) {
            return new ComponentAction(circuit);
        }
        int wireCount = 0;
        int nonWireCount = 0;
        for (Object obj : toRemove) {
            if (obj instanceof Wire) {
                ++wireCount;
                continue;
            }
            ++nonWireCount;
        }
        if (nonWireCount == 0) {
            descriptor = wireCount == 1 ? Strings.getter("removeWireAction") : Strings.getter("removeWiresAction");
        } else if (nonWireCount == 1 && wireCount == 0) {
            Component comp = (Component)toRemove.iterator().next();
            descriptor = Strings.getter("removeComponentAction", comp.getFactory().getDisplayName());
        } else {
            descriptor = Strings.getter("removeComponentsAction");
        }
        ComponentAction ret = new ComponentAction(circuit, descriptor);
        CircuitPoints removeSplitLocs = WireUtil.computeCircuitPoints(toRemove);
        if (!removeSplitLocs.getSplitLocations().isEmpty()) {
            HashSet<Object> toMerge = new HashSet<Object>();
            for (Location loc : removeSplitLocs.getSplitLocations()) {
                boolean doMerge = true;
                Wire x0 = null;
                Wire x1 = null;
                Wire y0 = null;
                Wire y1 = null;
                Iterator it2 = circuit.getSplitCauses(loc).iterator();
                while (doMerge && it2.hasNext()) {
                    Object comp = it2.next();
                    if (toRemove.contains(comp)) continue;
                    if (comp instanceof Wire) {
                        Wire w = (Wire)comp;
                        if (w.is_x_equal) {
                            if (y0 != null) {
                                doMerge = false;
                            }
                            if (x0 == null) {
                                x0 = w;
                                continue;
                            }
                            x1 = w;
                            continue;
                        }
                        if (x0 != null) {
                            doMerge = false;
                        }
                        if (y0 == null) {
                            y0 = w;
                            continue;
                        }
                        y1 = w;
                        continue;
                    }
                    doMerge = false;
                }
                if (!doMerge) continue;
                if (x1 != null) {
                    toMerge.add(x0);
                    toMerge.add(x1);
                }
                if (y1 == null) continue;
                toMerge.add(y0);
                toMerge.add(y1);
            }
            if (!toMerge.isEmpty()) {
                Iterator it = toMerge.iterator();
                while (it.hasNext()) {
                    ret.addToIncidentalRemovals((Wire)it.next());
                }
                it = WireUtil.mergeExclusive(toMerge).iterator();
                while (it.hasNext()) {
                    ret.addToIncidentalAdditions((Wire)it.next());
                }
            }
        }
        Iterator it = toRemove.iterator();
        while (it.hasNext()) {
            ret.addToRemovals((Component)it.next());
        }
        return ret;
    }
}

