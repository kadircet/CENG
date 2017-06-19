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
import java.util.TreeSet;

class ActionAdd {
    private ActionAdd() {
    }

    static ComponentAction create(Circuit circuit, Collection toAddBase) {
        StringGetter descriptor;
        if (toAddBase.isEmpty()) {
            return new ComponentAction(circuit);
        }
        Set curSplitLocs = circuit.getSplitLocations();
        HashSet<Component> toAdd = new HashSet<Component>();
        int wireCount = 0;
        int nonWireCount = 0;
        for (Component comp2 : toAddBase) {
            if (comp2 instanceof Wire) {
                ++wireCount;
                Wire w = (Wire)comp2;
                TreeSet<Location> endPoints = null;
                for (Location loc : curSplitLocs) {
                    if (!w.contains(loc)) continue;
                    if (endPoints == null) {
                        endPoints = new TreeSet<Location>();
                        endPoints.add(w.e0);
                        endPoints.add(w.e1);
                    }
                    endPoints.add(loc);
                }
                if (endPoints == null || endPoints.size() == 2) {
                    toAdd.add(w);
                    continue;
                }
                Iterator it2 = endPoints.iterator();
                Location last = (Location)it2.next();
                while (it2.hasNext()) {
                    Location here = (Location)it2.next();
                    toAdd.add(Wire.create(last, here));
                    last = here;
                }
                continue;
            }
            ++nonWireCount;
            toAdd.add(comp2);
        }
        if (nonWireCount == 0) {
            descriptor = wireCount == 1 ? Strings.getter("addWireAction") : Strings.getter("addWiresAction");
        } else if (nonWireCount == 1 && wireCount == 0) {
            Component comp2;
            comp2 = (Component)toAdd.iterator().next();
            descriptor = Strings.getter("addComponentAction", comp2.getFactory().getDisplayName());
        } else {
            descriptor = Strings.getter("addComponentsAction");
        }
        ComponentAction ret = new ComponentAction(circuit, descriptor);
        CircuitPoints addSplitLocs = WireUtil.computeCircuitPoints(toAdd);
        for (Wire w : circuit.getWires()) {
            int segCount;
            Location[] segEnd;
            int i;
            TreeSet<Location> endPoints = null;
            for (Location split : addSplitLocs.getSplitLocations()) {
                if (!w.contains(split)) continue;
                boolean doSplit = false;
                for (Object o : addSplitLocs.getSplitCauses(split)) {
                    if (o instanceof Wire) {
                        Wire splitCause = (Wire)o;
                        if (splitCause.is_x_equal == w.is_x_equal) continue;
                        doSplit = true;
                        continue;
                    }
                    doSplit = true;
                }
                if (!doSplit) continue;
                if (endPoints == null) {
                    endPoints = new TreeSet<Location>();
                }
                endPoints.add(split);
            }
            boolean canExtend0 = true;
            boolean canExtend1 = true;
            if (endPoints == null) {
                segCount = 1;
                segEnd = new Location[]{w.e0, w.e1};
            } else {
                canExtend0 = endPoints.add(w.e0);
                canExtend1 = endPoints.add(w.e1);
                segCount = endPoints.size() - 1;
                segEnd = new Location[segCount + 1];
                Iterator pIt = endPoints.iterator();
                i = 0;
                while (pIt.hasNext()) {
                    Location p;
                    segEnd[i] = p = (Location)pIt.next();
                    ++i;
                }
            }
            if (canExtend0) {
                for (Object w2 : circuit.getComponents(w.e0)) {
                    if (w2 == w) continue;
                    canExtend0 = false;
                }
            }
            if (canExtend1) {
                for (Object w2 : circuit.getComponents(w.e1)) {
                    if (w2 == w) continue;
                    canExtend1 = false;
                }
            }
            boolean[] segSelected = new boolean[segCount];
            if (wireCount > 0) {
                Iterator it2 = toAdd.iterator();
                while (it2.hasNext()) {
                    Location e1;
                    Object comp3 = it2.next();
                    if (!(comp3 instanceof Wire) || !w.overlaps((Wire)comp3)) continue;
                    Wire wNew = (Wire)comp3;
                    Location e0 = wNew.e0;
                    if (e0.compareTo(segEnd[0]) < 0) {
                        if (canExtend0) {
                            segEnd[0] = e0;
                            canExtend0 = false;
                        } else {
                            e0 = segEnd[0];
                        }
                    }
                    if ((e1 = wNew.e1).compareTo(segEnd[segCount]) > 0) {
                        if (canExtend1) {
                            segEnd[segCount] = e1;
                            canExtend1 = false;
                        } else {
                            e1 = segEnd[segCount];
                        }
                    }
                    if (e0.compareTo(e1) >= 0) continue;
                    it2.remove();
                    for (int i2 = 0; i2 < segCount; ++i2) {
                        Location seg0 = segEnd[i2];
                        Location seg1 = segEnd[i2 + 1];
                        if (seg1.compareTo(e0) < 0 || seg0.compareTo(e1) > 0) continue;
                        segSelected[i2] = true;
                    }
                }
            }
            if (segCount == 1 && segEnd[0].equals(w.e0) && segEnd[1].equals(w.e1) && !segSelected[0]) continue;
            ret.addToIncidentalRemovals(w);
            for (i = 0; i < segCount; ++i) {
                Wire wNew = Wire.create(segEnd[i], segEnd[i + 1]);
                if (segSelected[i]) {
                    toAdd.add(wNew);
                    continue;
                }
                ret.addToIncidentalAdditions(wNew);
            }
        }
        for (Component comp4 : toAdd) {
            ret.addToAdditions(comp4);
        }
        return ret;
    }
}

