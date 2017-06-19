/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.TruthTable;
import java.awt.Color;
import java.awt.event.MouseEvent;

interface TruthTablePanel {
    public static final Color ERROR_COLOR = new Color(255, 128, 128);

    public TruthTable getTruthTable();

    public int getOutputColumn(MouseEvent var1);

    public int getRow(MouseEvent var1);

    public void setEntryProvisional(int var1, int var2, Entry var3);
}

