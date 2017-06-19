/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.data.Location;

public class WireRepairData {
    private Wire wire;
    private Location point;

    public WireRepairData(Wire wire, Location point) {
        this.wire = wire;
        this.point = point;
    }

    public Location getPoint() {
        return this.point;
    }

    public Wire getWire() {
        return this.wire;
    }
}

