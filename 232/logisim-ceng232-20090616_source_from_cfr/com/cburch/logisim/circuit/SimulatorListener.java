/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.SimulatorEvent;

public interface SimulatorListener {
    public void propagationCompleted(SimulatorEvent var1);

    public void tickCompleted(SimulatorEvent var1);

    public void simulatorStateChanged(SimulatorEvent var1);
}

