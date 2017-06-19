/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

public class Cache {
    private int mask;
    private Object[] data;

    public Cache() {
        this(8);
    }

    public Cache(int logSize) {
        if (logSize > 12) {
            logSize = 12;
        }
        this.data = new Object[1 << logSize];
        this.mask = this.data.length - 1;
    }

    public Object get(int hashCode) {
        return this.data[hashCode & this.mask];
    }

    public void put(int hashCode, Object value) {
        if (value != null) {
            this.data[hashCode & this.mask] = value;
        }
    }

    public Object get(Object value) {
        if (value == null) {
            return null;
        }
        int code = value.hashCode() & this.mask;
        Object ret = this.data[code];
        if (ret != null && ret.equals(value)) {
            return ret;
        }
        this.data[code] = value;
        return value;
    }
}

