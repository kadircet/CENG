/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.Cache;

public class IntegerFactory {
    public static final Integer ZERO = IntegerFactory.create(0);
    public static final Integer ONE = IntegerFactory.create(1);
    private static Integer[] prefabs = null;
    private static final Cache cache = new Cache(5);

    private IntegerFactory() {
    }

    public static Integer create(int val) {
        Integer i;
        if (prefabs == null) {
            prefabs = new Integer[101];
            for (int i2 = 0; i2 < prefabs.length; ++i2) {
                IntegerFactory.prefabs[i2] = new Integer(i2);
            }
        }
        if (val >= 0 && val < prefabs.length) {
            return prefabs[val];
        }
        Object cached = cache.get(val);
        if (cached != null && (i = (Integer)cached) == val) {
            return i;
        }
        Integer ret = new Integer(val);
        cache.put(val, ret);
        return ret;
    }

    public static Integer create(String str) {
        return IntegerFactory.create(Integer.parseInt(str));
    }
}

