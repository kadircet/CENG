/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.util.SmallSet;

class WireThread {
    private WireThread parent;
    private SmallSet bundles = new SmallSet();

    WireThread() {
        this.parent = this;
    }

    SmallSet getBundles() {
        return this.bundles;
    }

    void unite(WireThread other) {
        WireThread group2;
        WireThread group = this.find();
        if (group != (group2 = other.find())) {
            group.parent = group2;
        }
    }

    WireThread find() {
        WireThread ret = this;
        if (ret.parent != ret) {
            while (ret.parent != (ret = ret.parent)) {
            }
            this.parent = ret;
        }
        return ret;
    }
}

