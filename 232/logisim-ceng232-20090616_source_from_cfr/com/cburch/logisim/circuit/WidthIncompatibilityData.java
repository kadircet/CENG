/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import java.util.ArrayList;

public class WidthIncompatibilityData {
    private ArrayList points = new ArrayList();
    private ArrayList widths = new ArrayList();

    public void add(Location p, BitWidth w) {
        for (int i = 0; i < this.points.size(); ++i) {
            if (!p.equals(this.points.get(i)) || !w.equals(this.widths.get(i))) continue;
            return;
        }
        this.points.add(p);
        this.widths.add(w);
    }

    public int size() {
        return this.points.size();
    }

    public Location getPoint(int i) {
        return (Location)this.points.get(i);
    }

    public BitWidth getBitWidth(int i) {
        return (BitWidth)this.widths.get(i);
    }

    public boolean equals(Object other) {
        if (!(other instanceof WidthIncompatibilityData)) {
            return false;
        }
        if (this == other) {
            return true;
        }
        WidthIncompatibilityData o = (WidthIncompatibilityData)other;
        if (this.size() != o.size()) {
            return false;
        }
        for (int i = 0; i < this.size(); ++i) {
            Location p = this.getPoint(i);
            BitWidth w = this.getBitWidth(i);
            boolean matched = false;
            for (int j = 0; j < o.size(); ++j) {
                Location q = this.getPoint(j);
                BitWidth x = this.getBitWidth(j);
                if (!p.equals(q) || !w.equals(x)) continue;
                matched = true;
                break;
            }
            if (matched) continue;
            return false;
        }
        return true;
    }
}

