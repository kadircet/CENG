/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import java.util.HashMap;
import java.util.Map;

class Assignments {
    private Map map = new HashMap();

    public boolean get(String variable) {
        Boolean value = (Boolean)this.map.get(variable);
        return value != null ? value : false;
    }

    public void put(String variable, boolean value) {
        this.map.put(variable, value);
    }
}

