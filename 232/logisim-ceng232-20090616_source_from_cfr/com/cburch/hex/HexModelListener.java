/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexModel;

public interface HexModelListener {
    public void metainfoChanged(HexModel var1);

    public void bytesChanged(HexModel var1, long var2, long var4, int[] var6);
}

