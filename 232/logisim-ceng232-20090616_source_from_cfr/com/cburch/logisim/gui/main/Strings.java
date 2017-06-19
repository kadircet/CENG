/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.main;

import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.StringGetter;
import java.util.Locale;
import javax.swing.JMenuItem;

class Strings {
    private static LocaleManager source = new LocaleManager("com/cburch/logisim/resources", "gui");

    Strings() {
    }

    public static String get(String key) {
        return source.get(key);
    }

    public static StringGetter getter(String key) {
        return source.getter(key);
    }

    public static Locale[] getLocaleOptions() {
        return source.getLocaleOptions();
    }

    public static JMenuItem createLocaleMenuItem() {
        return source.createLocaleMenuItem();
    }
}

