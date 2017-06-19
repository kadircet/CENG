/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.gui.main.Canvas;

public class ComponentUserEvent {
    private Canvas canvas;
    private int x = 0;
    private int y = 0;

    ComponentUserEvent(Canvas canvas) {
        this.canvas = canvas;
    }

    public ComponentUserEvent(Canvas canvas, int x, int y) {
        this.canvas = canvas;
        this.x = x;
        this.y = y;
    }

    public Canvas getCanvas() {
        return this.canvas;
    }

    public CircuitState getCircuitState() {
        return this.canvas.getCircuitState();
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }
}

