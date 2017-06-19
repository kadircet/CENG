/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitPoints;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Location;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

public class WireUtil {
    private WireUtil() {
    }

    static CircuitPoints computeCircuitPoints(Collection components) {
        CircuitPoints points = new CircuitPoints();
        Iterator it = components.iterator();
        while (it.hasNext()) {
            points.add((Component)it.next());
        }
        return points;
    }

    public static Collection mergeExclusive(Collection toMerge) {
        if (toMerge.size() <= 1) {
            return toMerge;
        }
        HashSet<Wire> ret = new HashSet<Wire>(toMerge);
        CircuitPoints points = WireUtil.computeCircuitPoints(toMerge);
        HashSet<Wire> wires = new HashSet<Wire>();
        for (Location loc : points.getSplitLocations()) {
            Collection at = points.getComponents(loc);
            if (at.size() != 2) continue;
            Iterator atIt = at.iterator();
            Object o0 = atIt.next();
            Object o1 = atIt.next();
            if (!(o0 instanceof Wire) || !(o1 instanceof Wire)) continue;
            Wire w0 = (Wire)o0;
            Wire w1 = (Wire)o1;
            if (w0.is_x_equal != w1.is_x_equal) continue;
            wires.add(w0);
            wires.add(w1);
        }
        points = null;
        ret.removeAll(wires);
        while (!wires.isEmpty()) {
            boolean found;
            Iterator it = wires.iterator();
            Wire w = (Wire)it.next();
            Location e0 = w.e0;
            Location e1 = w.e1;
            it.remove();
            do {
                found = false;
                it = wires.iterator();
                while (it.hasNext()) {
                    Wire cand = (Wire)it.next();
                    if (cand.e0.equals(e1)) {
                        e1 = cand.e1;
                        found = true;
                        it.remove();
                        continue;
                    }
                    if (!cand.e1.equals(e0)) continue;
                    e0 = cand.e0;
                    found = true;
                    it.remove();
                }
            } while (found);
            ret.add(Wire.create(e0, e1));
        }
        return ret;
    }
}

