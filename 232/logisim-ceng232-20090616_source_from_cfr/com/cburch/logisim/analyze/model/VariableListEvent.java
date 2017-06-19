/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.analyze.model.VariableList;

public class VariableListEvent {
    public static final int ALL_REPLACED = 0;
    public static final int ADD = 1;
    public static final int REMOVE = 2;
    public static final int MOVE = 3;
    public static final int REPLACE = 4;
    private VariableList source;
    private int type;
    private String variable;
    private Object data;

    public VariableListEvent(VariableList source, int type, String variable, Object data) {
        this.source = source;
        this.type = type;
        this.variable = variable;
        this.data = data;
    }

    public VariableList getSource() {
        return this.source;
    }

    public int getType() {
        return this.type;
    }

    public String getVariable() {
        return this.variable;
    }

    public Object getData() {
        return this.data;
    }
}

