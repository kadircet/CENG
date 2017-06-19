/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Simulator;

public interface SimulateListener {
    public void stateChangeRequested(Simulator var1, CircuitState var2);
}

