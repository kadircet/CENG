/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim;

import com.cburch.logisim.gui.start.Startup;

public class Main {
    public static final String VERSION_NAME = "2.1.8";

    public static void main(String[] args) {
        Startup startup = Startup.parseArgs(args);
        if (startup == null) {
            System.exit(0);
        } else {
            startup.run();
        }
    }
}

