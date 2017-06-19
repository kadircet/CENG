/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.VariableListEvent;

public class TruthTableEvent {
    private TruthTable source;
    private int column;
    private Object data;

    public TruthTableEvent(TruthTable source, VariableListEvent event) {
        this.source = source;
        this.data = event;
    }

    public TruthTableEvent(TruthTable source, int column) {
        this.source = source;
        this.column = column;
    }

    public int getColumn() {
        return this.column;
    }

    public TruthTable getSource() {
        return this.source;
    }

    public Object getData() {
        return this.data;
    }
}

