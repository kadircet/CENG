/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Location;
import java.awt.Dimension;

public class Size {
    private final int wid;
    private final int ht;

    public static Size create(int wid, int ht) {
        return new Size(wid, ht);
    }

    private Size(int wid, int ht) {
        this.wid = wid;
        this.ht = ht;
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof Size)) {
            return false;
        }
        Size other = (Size)other_obj;
        return this.wid == other.wid && this.ht == other.ht;
    }

    public String toString() {
        return "" + this.wid + "x" + this.ht;
    }

    public int getWidth() {
        return this.wid;
    }

    public int getHeight() {
        return this.ht;
    }

    public Dimension toAwtDimension() {
        return new Dimension(this.wid, this.ht);
    }

    public boolean contains(Location p) {
        return this.contains(p.getX(), p.getY());
    }

    public boolean contains(int x, int y) {
        return x >= 0 && y >= 0 && x < this.wid && y < this.ht;
    }

    public boolean contains(int x, int y, int wid, int ht) {
        int oth_x = wid <= 0 ? x : x + wid - 1;
        int oth_y = ht <= 0 ? y : y + wid - 1;
        return this.contains(x, y) && this.contains(oth_x, oth_y);
    }

    public boolean contains(Size bd) {
        return this.contains(bd.wid, bd.ht);
    }
}

