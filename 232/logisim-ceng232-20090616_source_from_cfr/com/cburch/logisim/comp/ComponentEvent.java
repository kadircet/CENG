/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.Component;

public class ComponentEvent {
    private Component source;
    private Object oldData;
    private Object newData;

    public ComponentEvent(Component source) {
        this(source, null, null);
    }

    public ComponentEvent(Component source, Object oldData, Object newData) {
        this.source = source;
        this.oldData = oldData;
        this.newData = newData;
    }

    public Component getSource() {
        return this.source;
    }

    public Object getData() {
        return this.newData;
    }

    public Object getOldData() {
        return this.oldData;
    }
}

