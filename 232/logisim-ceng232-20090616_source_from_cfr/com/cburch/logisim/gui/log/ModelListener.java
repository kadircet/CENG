/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.ModelEvent;

interface ModelListener {
    public void selectionChanged(ModelEvent var1);

    public void entryAdded(ModelEvent var1, Value[] var2);

    public void filePropertyChanged(ModelEvent var1);
}

