/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.data;

import com.cburch.logisim.data.AttributeOptionInterface;
import com.cburch.logisim.data.Strings;
import com.cburch.logisim.util.StringGetter;

public class Direction
implements AttributeOptionInterface {
    public static final Direction EAST = new Direction("east", Strings.getter("directionEastOption"), 0);
    public static final Direction WEST = new Direction("west", Strings.getter("directionWestOption"), 1);
    public static final Direction NORTH = new Direction("north", Strings.getter("directionNorthOption"), 2);
    public static final Direction SOUTH = new Direction("south", Strings.getter("directionSouthOption"), 3);
    public static final Direction[] cardinals = new Direction[]{NORTH, EAST, SOUTH, WEST};
    private String name;
    private StringGetter disp;
    private int id;

    public static Direction parse(String str) {
        if (str.equals(Direction.EAST.name)) {
            return EAST;
        }
        if (str.equals(Direction.WEST.name)) {
            return WEST;
        }
        if (str.equals(Direction.NORTH.name)) {
            return NORTH;
        }
        if (str.equals(Direction.SOUTH.name)) {
            return SOUTH;
        }
        throw new NumberFormatException("illegal direction '" + str + "'");
    }

    private Direction(String name, StringGetter disp, int id) {
        this.name = name;
        this.disp = disp;
        this.id = id;
    }

    @Override
    public String toString() {
        return this.name;
    }

    @Override
    public String toDisplayString() {
        return this.disp.get();
    }

    public int hashCode() {
        return this.id;
    }

    public double toRadians() {
        if (this == EAST) {
            return 0.0;
        }
        if (this == WEST) {
            return 3.141592653589793;
        }
        if (this == NORTH) {
            return 1.5707963267948966;
        }
        if (this == SOUTH) {
            return -1.5707963267948966;
        }
        return 0.0;
    }

    public int toDegrees() {
        if (this == EAST) {
            return 0;
        }
        if (this == WEST) {
            return 180;
        }
        if (this == NORTH) {
            return 90;
        }
        if (this == SOUTH) {
            return 270;
        }
        return 0;
    }

    public Direction reverse() {
        if (this == EAST) {
            return WEST;
        }
        if (this == WEST) {
            return EAST;
        }
        if (this == NORTH) {
            return SOUTH;
        }
        if (this == SOUTH) {
            return NORTH;
        }
        return WEST;
    }

    @Override
    public Object getValue() {
        return this;
    }
}

