/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.TruthTableEvent;

public interface TruthTableListener {
    public void cellsChanged(TruthTableEvent var1);

    public void structureChanged(TruthTableEvent var1);
}

