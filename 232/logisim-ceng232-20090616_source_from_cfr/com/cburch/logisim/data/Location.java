/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.Direction;
import com.cburch.logisim.util.Cache;

public class Location
implements Comparable {
    private static final Cache cache = new Cache();
    private final int hashCode;
    private final int x;
    private final int y;

    private Location(int hashCode, int x, int y) {
        this.hashCode = hashCode;
        this.x = x;
        this.y = y;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int manhattanDistanceTo(Location o) {
        return Math.abs(o.x - this.x) + Math.abs(o.y - this.y);
    }

    public int manhattanDistanceTo(int x, int y) {
        return Math.abs(x - this.x) + Math.abs(y - this.y);
    }

    public Location translate(int dx, int dy) {
        if (dx == 0 && dy == 0) {
            return this;
        }
        return Location.create(this.x + dx, this.y + dy);
    }

    public Location translate(Direction dir, int dist) {
        return this.translate(dir, dist, 0);
    }

    public Location translate(Direction dir, int dist, int right) {
        if (dist == 0 && right == 0) {
            return this;
        }
        if (dir == Direction.EAST) {
            return Location.create(this.x + dist, this.y + right);
        }
        if (dir == Direction.WEST) {
            return Location.create(this.x - dist, this.y - right);
        }
        if (dir == Direction.SOUTH) {
            return Location.create(this.x - right, this.y + dist);
        }
        if (dir == Direction.NORTH) {
            return Location.create(this.x + right, this.y - dist);
        }
        return Location.create(this.x + dist, this.y + right);
    }

    public boolean equals(Object other_obj) {
        if (!(other_obj instanceof Location)) {
            return false;
        }
        Location other = (Location)other_obj;
        return this.x == other.x && this.y == other.y;
    }

    public int hashCode() {
        return this.hashCode;
    }

    public int compareTo(Object other_obj) {
        Location other = (Location)other_obj;
        if (this.x != other.x) {
            return this.x - other.x;
        }
        return this.y - other.y;
    }

    public String toString() {
        return "(" + this.x + "," + this.y + ")";
    }

    public static Location create(int x, int y) {
        Location loc;
        int hashCode = 31 * x + y;
        Object ret = cache.get(hashCode);
        if (ret != null) {
            loc = (Location)ret;
            if (loc.x == x && loc.y == y) {
                return loc;
            }
        }
        loc = new Location(hashCode, x, y);
        cache.put(hashCode, loc);
        return loc;
    }

    public static Location parse(String value) {
        int comma;
        String base = value;
        if ((value = value.trim()).charAt(0) == '(') {
            int len = value.length();
            if (value.charAt(len - 1) != ')') {
                throw new NumberFormatException("invalid point '" + base + "'");
            }
            value = value.substring(1, len - 1);
        }
        if ((comma = (value = value.trim()).indexOf(44)) < 0 && (comma = value.indexOf(32)) < 0) {
            throw new NumberFormatException("invalid point '" + base + "'");
        }
        int x = Integer.parseInt(value.substring(0, comma).trim());
        int y = Integer.parseInt(value.substring(comma + 1).trim());
        return Location.create(x, y);
    }
}

