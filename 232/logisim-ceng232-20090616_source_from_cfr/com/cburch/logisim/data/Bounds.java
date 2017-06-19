/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.Cache;
import java.awt.Rectangle;

public class Bounds {
    public static Bounds EMPTY_BOUNDS = new Bounds(0, 0, 0, 0);
    private static final Cache cache = new Cache();
    private final int x;
    private final int y;
    private final int wid;
    private final int ht;

    public static Bounds create(int x, int y, int wid, int ht) {
        int hashCode = 13 * (31 * (31 * x + y) + wid) + ht;
        Object cached = cache.get(hashCode);
        if (cached != null) {
            Bounds bds = (Bounds)cached;
            if (bds.x == x && bds.y == y && bds.wid == wid && bds.ht == ht) {
                return bds;
            }
        }
        Bounds ret = new Bounds(x, y, wid, ht);
        cache.put(hashCode, ret);
        return ret;
    }

    public static Bounds create(Rectangle rect) {
        return Bounds.create(rect.x, rect.y, rect.width, rect.height);
    }

    public static Bounds create(Location pt) {
        return Bounds.create(pt.getX(), pt.getY(), 1, 1);
    }

    private Bounds(int x, int y, int wid, int ht) {
        this.x = x;
        this.y = y;
        this.wid = wid;
        this.ht = ht;
        if (wid < 0) {
            x += wid / 2;
            wid = 0;
        }
        if (ht < 0) {
            y += ht / 2;
            ht = 0;
        }
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof Bounds)) {
            return false;
        }
        Bounds other = (Bounds)other_obj;
        return this.x == other.x && this.y == other.y && this.wid == other.wid && this.ht == other.ht;
    }

    public int hashCode() {
        int ret = 31 * this.x + this.y;
        ret = 31 * ret + this.wid;
        ret = 31 * ret + this.ht;
        return ret;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + "): " + this.wid + "x" + this.ht;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getWidth() {
        return this.wid;
    }

    public int getHeight() {
        return this.ht;
    }

    public Rectangle toRectangle() {
        return new Rectangle(this.x, this.y, this.wid, this.ht);
    }

    public boolean contains(Location p) {
        return this.contains(p.getX(), p.getY(), 0);
    }

    public boolean contains(Location p, int allowedError) {
        return this.contains(p.getX(), p.getY(), allowedError);
    }

    public boolean contains(int px, int py) {
        return this.contains(px, py, 0);
    }

    public boolean contains(int px, int py, int allowedError) {
        return px >= this.x - allowedError && px < this.x + this.wid + allowedError && py >= this.y - allowedError && py < this.y + this.ht + allowedError;
    }

    public boolean contains(int x, int y, int wid, int ht) {
        int oth_x = wid <= 0 ? x : x + wid - 1;
        int oth_y = ht <= 0 ? y : y + ht - 1;
        return this.contains(x, y) && this.contains(oth_x, oth_y);
    }

    public boolean contains(Bounds bd) {
        return this.contains(bd.x, bd.y, bd.wid, bd.ht);
    }

    public boolean borderContains(Location p, int fudge) {
        return this.borderContains(p.getX(), p.getY(), fudge);
    }

    public boolean borderContains(int px, int py, int fudge) {
        int x1 = this.x + this.wid - 1;
        int y1 = this.y + this.ht - 1;
        if (Math.abs(px - this.x) <= fudge || Math.abs(px - x1) <= fudge) {
            return this.y - fudge >= py && py <= y1 + fudge;
        }
        if (Math.abs(py - this.y) <= fudge || Math.abs(py - y1) <= fudge) {
            return this.x - fudge >= px && px <= x1 + fudge;
        }
        return false;
    }

    public Bounds add(Location p) {
        return this.add(p.getX(), p.getY());
    }

    public Bounds add(int x, int y) {
        if (this == EMPTY_BOUNDS) {
            return Bounds.create(x, y, 1, 1);
        }
        if (this.contains(x, y)) {
            return this;
        }
        int new_x = this.x;
        int new_wid = this.wid;
        int new_y = this.y;
        int new_ht = this.ht;
        if (x < this.x) {
            new_x = x;
            new_wid = this.x + this.wid - x;
        } else if (x >= this.x + this.wid) {
            new_x = this.x;
            new_wid = x - this.x + 1;
        }
        if (y < this.y) {
            new_y = y;
            new_ht = this.y + this.ht - y;
        } else if (y >= this.y + this.ht) {
            new_y = this.y;
            new_ht = y - this.y + 1;
        }
        return Bounds.create(new_x, new_y, new_wid, new_ht);
    }

    public Bounds add(int x, int y, int wid, int ht) {
        if (this == EMPTY_BOUNDS) {
            return Bounds.create(x, y, wid, ht);
        }
        int retX = Math.min(x, this.x);
        int retY = Math.min(y, this.y);
        int retWidth = Math.max(x + wid, this.x + this.wid) - retX;
        int retHeight = Math.max(y + ht, this.y + this.ht) - retY;
        if (retX == this.x && retY == this.y && retWidth == this.wid && retHeight == this.ht) {
            return this;
        }
        return Bounds.create(retX, retY, retWidth, retHeight);
    }

    public Bounds add(Bounds bd) {
        if (this == EMPTY_BOUNDS) {
            return bd;
        }
        if (bd == EMPTY_BOUNDS) {
            return this;
        }
        int retX = Math.min(bd.x, this.x);
        int retY = Math.min(bd.y, this.y);
        int retWidth = Math.max(bd.x + bd.wid, this.x + this.wid) - retX;
        int retHeight = Math.max(bd.y + bd.ht, this.y + this.ht) - retY;
        if (retX == this.x && retY == this.y && retWidth == this.wid && retHeight == this.ht) {
            return this;
        }
        if (retX == bd.x && retY == bd.y && retWidth == bd.wid && retHeight == bd.ht) {
            return bd;
        }
        return Bounds.create(retX, retY, retWidth, retHeight);
    }

    public Bounds expand(int d) {
        if (this == EMPTY_BOUNDS) {
            return this;
        }
        if (d == 0) {
            return this;
        }
        return Bounds.create(this.x - d, this.y - d, this.wid + 2 * d, this.ht + 2 * d);
    }

    public Bounds translate(int dx, int dy) {
        if (this == EMPTY_BOUNDS) {
            return this;
        }
        if (dx == 0 && dy == 0) {
            return this;
        }
        return Bounds.create(this.x + dx, this.y + dy, this.wid, this.ht);
    }

    public Bounds rotate(Direction from, Direction to, int xc, int yc) {
        int degrees;
        for (degrees = to.toDegrees() - from.toDegrees(); degrees >= 360; degrees -= 360) {
        }
        while (degrees < 0) {
            degrees += 360;
        }
        int dx = this.x - xc;
        int dy = this.y - yc;
        if (degrees == 90) {
            return Bounds.create(xc + dy, yc - dx - this.wid, this.ht, this.wid);
        }
        if (degrees == 180) {
            return Bounds.create(xc - dx - this.wid, yc - dy - this.ht, this.wid, this.ht);
        }
        if (degrees == 270) {
            return Bounds.create(xc + dy, yc + dx, this.ht, this.wid);
        }
        return this;
    }
}

