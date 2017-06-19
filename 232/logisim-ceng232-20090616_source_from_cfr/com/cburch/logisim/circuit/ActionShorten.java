/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.ComponentAction;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.StringGetter;
import java.util.Collection;
import java.util.Set;

class ActionShorten {
    private ActionShorten() {
    }

    static ComponentAction create(Circuit circuit, Wire wn) {
        if (wn.e0.equals(wn.e1)) {
            return null;
        }
        Wire toShorten = null;
        Wire afterShorten = null;
        Location shortenLoc = null;
        for (Wire w : circuit.getWires()) {
            if (!w.overlaps(wn)) continue;
            boolean match0 = w.e0.equals(wn.e0);
            boolean match1 = w.e1.equals(wn.e1);
            if (match0 && match1) {
                toShorten = w;
                afterShorten = null;
                continue;
            }
            if (match0 && w.contains(wn.e1)) {
                toShorten = w;
                afterShorten = Wire.create(wn.e1, w.e1);
                shortenLoc = wn.e1;
                continue;
            }
            if (!match1 || !w.contains(wn.e0)) continue;
            toShorten = w;
            afterShorten = Wire.create(w.e0, wn.e0);
            shortenLoc = wn.e0;
        }
        if (toShorten == null) {
            return null;
        }
        ComponentAction ret = new ComponentAction(circuit, Strings.getter("shortenWireAction"));
        ret.addToRemovals(toShorten);
        if (afterShorten != null) {
            ret.addToAdditions(afterShorten);
        }
        for (int endIndex = 0; endIndex < 2; ++endIndex) {
            Location shortenEnd;
            Location end = endIndex == 0 ? wn.e0 : wn.e1;
            Location location = shortenEnd = endIndex == 0 ? toShorten.e0 : toShorten.e1;
            if (!end.equals(shortenEnd)) continue;
            Wire perp0 = null;
            Wire perp1 = null;
            boolean doMerge = true;
            for (Object o : circuit.getSplitCauses(end)) {
                if (o instanceof Wire) {
                    Wire w2 = (Wire)o;
                    if (w2.is_x_equal == toShorten.is_x_equal) {
                        if (w2 == toShorten) continue;
                        doMerge = false;
                        continue;
                    }
                    if (perp0 == null) {
                        perp0 = w2;
                        continue;
                    }
                    perp1 = w2;
                    continue;
                }
                doMerge = false;
            }
            if (!doMerge || perp1 == null) continue;
            Location newEnd0 = perp0.e0.compareTo(perp1.e0) < 0 ? perp0.e0 : perp1.e0;
            Location newEnd1 = perp0.e1.compareTo(perp1.e1) > 0 ? perp0.e1 : perp1.e1;
            ret.addToIncidentalRemovals(perp0);
            ret.addToIncidentalRemovals(perp1);
            ret.addToIncidentalAdditions(Wire.create(newEnd0, newEnd1));
        }
        if (shortenLoc != null) {
            for (Wire w3 : circuit.getWires()) {
                if (!w3.contains(shortenLoc) || w3.endsAt(shortenLoc) || w3 == toShorten) continue;
                ret.addToIncidentalRemovals(w3);
                ret.addToIncidentalAdditions(Wire.create(w3.e0, shortenLoc));
                ret.addToIncidentalAdditions(Wire.create(shortenLoc, w3.e1));
            }
        }
        return ret;
    }
}

