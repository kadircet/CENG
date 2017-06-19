/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Location;

public class EndData {
    public static final int INPUT_ONLY = 1;
    public static final int OUTPUT_ONLY = 2;
    public static final int INPUT_OUTPUT = 3;
    private Location loc;
    private BitWidth width;
    private int i_o;
    private boolean exclusive;

    public EndData(Location loc, BitWidth width, int type, boolean exclusive) {
        this.loc = loc;
        this.width = width;
        this.i_o = type;
        this.exclusive = exclusive;
    }

    public EndData(Location loc, BitWidth width, int type) {
        this(loc, width, type, type == 2);
    }

    public boolean isExclusive() {
        return this.exclusive;
    }

    public boolean isInput() {
        return (this.i_o & 1) != 0;
    }

    public boolean isOutput() {
        return (this.i_o & 2) != 0;
    }

    public Location getLocation() {
        return this.loc;
    }

    public BitWidth getWidth() {
        return this.width;
    }

    public int getType() {
        return this.i_o;
    }

    public boolean equals(Object other) {
        if (!(other instanceof EndData)) {
            return false;
        }
        if (other == this) {
            return true;
        }
        EndData o = (EndData)other;
        return o.loc.equals(this.loc) && o.width.equals(this.width) && o.i_o == this.i_o;
    }
}

