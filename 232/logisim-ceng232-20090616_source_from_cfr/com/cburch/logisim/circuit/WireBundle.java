/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.WidthIncompatibilityData;
import com.cburch.logisim.circuit.WireThread;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.SmallSet;

class WireBundle {
    private BitWidth width = BitWidth.UNKNOWN;
    private WireBundle parent;
    private Location widthDeterminant = null;
    WireThread[] threads = null;
    SmallSet points = new SmallSet();
    private WidthIncompatibilityData incompatibilityData = null;

    WireBundle() {
        this.parent = this;
    }

    boolean isValid() {
        return this.incompatibilityData == null;
    }

    void setWidth(BitWidth width, Location det) {
        if (width == BitWidth.UNKNOWN) {
            return;
        }
        if (this.incompatibilityData != null) {
            this.incompatibilityData.add(det, width);
            return;
        }
        if (this.width != BitWidth.UNKNOWN) {
            if (width.equals(this.width)) {
                return;
            }
            this.incompatibilityData = new WidthIncompatibilityData();
            this.incompatibilityData.add(this.widthDeterminant, this.width);
            this.incompatibilityData.add(det, width);
            return;
        }
        this.width = width;
        this.widthDeterminant = det;
        this.threads = new WireThread[width.getWidth()];
        for (int i = 0; i < this.threads.length; ++i) {
            this.threads[i] = new WireThread();
        }
    }

    BitWidth getWidth() {
        if (this.incompatibilityData != null) {
            return BitWidth.UNKNOWN;
        }
        return this.width;
    }

    Location getWidthDeterminant() {
        if (this.incompatibilityData != null) {
            return null;
        }
        return this.widthDeterminant;
    }

    WidthIncompatibilityData getWidthIncompatibilityData() {
        return this.incompatibilityData;
    }

    void isolate() {
        this.parent = this;
    }

    void unite(WireBundle other) {
        WireBundle group2;
        WireBundle group = this.find();
        if (group != (group2 = other.find())) {
            group.parent = group2;
        }
    }

    WireBundle find() {
        WireBundle ret = this;
        if (ret.parent != ret) {
            while (ret.parent != (ret = ret.parent)) {
            }
            this.parent = ret;
        }
        return ret;
    }
}

