/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.model;

import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringGetter;

class Strings {
    private static LocaleManager source = new LocaleManager("com/cburch/logisim/resources", "analyze");

    Strings() {
    }

    public static String get(String key) {
        return source.get(key);
    }

    public static StringGetter getter(String key) {
        return source.getter(key);
    }

    public static StringGetter getter(String key, String arg) {
        return source.getter(key, arg);
    }
}

