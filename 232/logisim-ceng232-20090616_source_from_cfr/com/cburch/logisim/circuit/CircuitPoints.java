/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.WidthIncompatibilityData;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

class CircuitPoints {
    private HashMap map = new HashMap();
    private HashMap incompatibilityData = new HashMap();

    Set getSplitLocations() {
        return this.map.keySet();
    }

    BitWidth getWidth(Location loc) {
        LocationData locData = (LocationData)this.map.get(loc);
        return locData == null ? BitWidth.UNKNOWN : locData.width;
    }

    int getComponentCount(Location loc) {
        LocationData locData = (LocationData)this.map.get(loc);
        return locData == null ? 0 : locData.components.size();
    }

    Component getExclusive(Location loc) {
        LocationData locData = (LocationData)this.map.get(loc);
        if (locData == null) {
            return null;
        }
        int n = locData.ends.size();
        for (int i = 0; i < n; ++i) {
            EndData endData = (EndData)locData.ends.get(i);
            if (endData == null || !endData.isExclusive()) continue;
            return (Component)locData.components.get(i);
        }
        return null;
    }

    Collection getComponents(Location loc) {
        LocationData locData = (LocationData)this.map.get(loc);
        if (locData == null) {
            return Collections.EMPTY_SET;
        }
        return locData.components;
    }

    Collection getSplitCauses(Location loc) {
        return this.getComponents(loc);
    }

    Collection getWires(Location loc) {
        return this.find(loc, true);
    }

    Collection getNonWires(Location loc) {
        return this.find(loc, false);
    }

    private Collection find(Location loc, boolean isWire) {
        LocationData locData = (LocationData)this.map.get(loc);
        if (locData == null) {
            return Collections.EMPTY_SET;
        }
        ArrayList list = locData.components;
        int retSize = 0;
        Object retValue = null;
        int n = list.size();
        for (int i = 0; i < n; ++i) {
            Object o = list.get(i);
            if (o instanceof Wire != isWire) continue;
            retValue = o;
            ++retSize;
        }
        if (retSize == n) {
            return locData.components;
        }
        if (retSize == 0) {
            return Collections.EMPTY_SET;
        }
        if (retSize == 1) {
            return Collections.singleton(retValue);
        }
        Object[] ret = new Object[retSize];
        int retPos = 0;
        for (int i2 = 0; i2 < n; ++i2) {
            Object o = list.get(i2);
            if (o instanceof Wire != isWire) continue;
            ret[retPos] = o;
            ++retPos;
        }
        return Arrays.asList(ret);
    }

    Collection getWidthIncompatibilityData() {
        return this.incompatibilityData.values();
    }

    boolean hasConflict(Component comp) {
        if (comp instanceof Wire) {
            return false;
        }
        for (EndData endData : comp.getEnds()) {
            if (endData == null || !endData.isExclusive() || this.getExclusive(endData.getLocation()) == null) continue;
            return true;
        }
        return false;
    }

    void add(Component comp) {
        if (comp instanceof Wire) {
            Wire w = (Wire)comp;
            this.addSub(w.getEnd0(), w, null);
            this.addSub(w.getEnd1(), w, null);
        } else {
            for (EndData endData : comp.getEnds()) {
                if (endData == null) continue;
                this.addSub(endData.getLocation(), comp, endData);
            }
        }
    }

    void add(Component comp, EndData endData) {
        if (endData != null) {
            this.addSub(endData.getLocation(), comp, endData);
        }
    }

    void remove(Component comp) {
        if (comp instanceof Wire) {
            Wire w = (Wire)comp;
            this.removeSub(w.getEnd0(), w);
            this.removeSub(w.getEnd1(), w);
        } else {
            for (EndData endData : comp.getEnds()) {
                if (endData == null) continue;
                this.removeSub(endData.getLocation(), comp);
            }
        }
    }

    void remove(Component comp, EndData endData) {
        if (endData != null) {
            this.removeSub(endData.getLocation(), comp);
        }
    }

    private void addSub(Location loc, Component comp, EndData endData) {
        LocationData locData = (LocationData)this.map.get(loc);
        if (locData == null) {
            locData = new LocationData();
            this.map.put(loc, locData);
        }
        locData.components.add(comp);
        locData.ends.add(endData);
        this.computeIncompatibilityData(loc, locData);
    }

    private void removeSub(Location loc, Component comp) {
        LocationData locData = (LocationData)this.map.get(loc);
        if (locData == null) {
            return;
        }
        int index = locData.components.indexOf(comp);
        if (index < 0) {
            return;
        }
        if (locData.components.size() == 1) {
            this.map.remove(loc);
            this.incompatibilityData.remove(loc);
        } else {
            locData.components.remove(index);
            locData.ends.remove(index);
            this.computeIncompatibilityData(loc, locData);
        }
    }

    private void computeIncompatibilityData(Location loc, LocationData locData) {
        WidthIncompatibilityData error = null;
        if (locData != null) {
            BitWidth width = BitWidth.UNKNOWN;
            int n = locData.ends.size();
            for (int i = 0; i < n; ++i) {
                EndData endData = (EndData)locData.ends.get(i);
                if (endData == null) continue;
                BitWidth endWidth = endData.getWidth();
                if (width == BitWidth.UNKNOWN) {
                    width = endWidth;
                    continue;
                }
                if (width == endWidth || endWidth == BitWidth.UNKNOWN) continue;
                if (error == null) {
                    error = new WidthIncompatibilityData();
                    error.add(loc, width);
                }
                error.add(loc, endWidth);
            }
            locData.width = width;
        }
        if (error == null) {
            this.incompatibilityData.remove(loc);
        } else {
            this.incompatibilityData.put(loc, error);
        }
    }

    private static class LocationData {
        BitWidth width = BitWidth.UNKNOWN;
        ArrayList components = new ArrayList(4);
        ArrayList ends = new ArrayList(4);

        private LocationData() {
        }
    }

}

