/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

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
}

