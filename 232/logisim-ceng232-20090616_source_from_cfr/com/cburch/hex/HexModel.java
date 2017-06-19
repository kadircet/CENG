/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexModelListener;

public interface HexModel {
    public void addHexModelListener(HexModelListener var1);

    public void removeHexModelListener(HexModelListener var1);

    public long getFirstOffset();

    public long getLastOffset();

    public int getValueWidth();

    public int get(long var1);

    public void set(long var1, int var3);

    public void set(long var1, int[] var3);

    public void fill(long var1, long var3, int var5);
}

