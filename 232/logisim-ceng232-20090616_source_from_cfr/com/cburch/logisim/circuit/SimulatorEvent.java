/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Simulator;

public class SimulatorEvent {
    private Simulator source;

    public SimulatorEvent(Simulator source) {
        this.source = source;
    }

    public Simulator getSource() {
        return this.source;
    }
}

