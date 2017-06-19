/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

public class TableConstraints {
    private int col;
    private int row;

    public static TableConstraints at(int row, int col) {
        return new TableConstraints(row, col);
    }

    private TableConstraints(int row, int col) {
        this.col = col;
        this.row = row;
    }

    int getRow() {
        return this.row;
    }

    int getCol() {
        return this.col;
    }
}

