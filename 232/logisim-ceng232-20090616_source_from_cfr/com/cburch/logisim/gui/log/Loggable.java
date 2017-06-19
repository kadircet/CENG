/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.data.Value;

public interface Loggable {
    public Object[] getLogOptions(CircuitState var1);

    public String getLogName(Object var1);

    public Value getLogValue(CircuitState var1, Object var2);
}

