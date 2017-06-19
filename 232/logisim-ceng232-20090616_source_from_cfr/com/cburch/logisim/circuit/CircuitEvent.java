/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Circuit;

public class CircuitEvent {
    public static final int ACTION_SET_NAME = 0;
    public static final int ACTION_ADD = 1;
    public static final int ACTION_REMOVE = 2;
    public static final int ACTION_CHANGE = 3;
    public static final int ACTION_INVALIDATE = 4;
    public static final int ACTION_CLEAR = 5;
    private int action;
    private Circuit circuit;
    private Object data;

    CircuitEvent(int action, Circuit circuit, Object data) {
        this.action = action;
        this.circuit = circuit;
        this.data = data;
    }

    public int getAction() {
        return this.action;
    }

    public Circuit getCircuit() {
        return this.circuit;
    }

    public Object getData() {
        return this.data;
    }
}

