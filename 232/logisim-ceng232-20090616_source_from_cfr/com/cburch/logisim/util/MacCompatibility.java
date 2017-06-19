/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.io.File;
import java.io.IOException;
import javax.swing.JMenuBar;
import net.roydesign.mac.MRJAdapter;

public class MacCompatibility {
    public static final double mrjVersion;

    private MacCompatibility() {
    }

    public static boolean isAboutAutomaticallyPresent() {
        try {
            return MRJAdapter.isAboutAutomaticallyPresent();
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean isPreferencesAutomaticallyPresent() {
        try {
            return MRJAdapter.isPreferencesAutomaticallyPresent();
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean isQuitAutomaticallyPresent() {
        try {
            return MRJAdapter.isQuitAutomaticallyPresent();
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static boolean isSwingUsingScreenMenuBar() {
        try {
            return MRJAdapter.isSwingUsingScreenMenuBar();
        }
        catch (Throwable t) {
            return false;
        }
    }

    public static void setFramelessJMenuBar(JMenuBar menubar) {
        try {
            MRJAdapter.setFramelessJMenuBar(menubar);
        }
        catch (Throwable t) {
            // empty catch block
        }
    }

    public static void setFileCreatorAndType(File dest, String app, String type) throws IOException {
        IOException ioExcept = null;
        try {
            try {
                MRJAdapter.setFileCreatorAndType(dest, app, type);
            }
            catch (IOException e) {
                ioExcept = e;
            }
        }
        catch (Throwable t) {
            // empty catch block
        }
        if (ioExcept != null) {
            throw ioExcept;
        }
    }

    static {
        double versionValue;
        try {
            versionValue = MRJAdapter.mrjVersion;
        }
        catch (Throwable t) {
            versionValue = 0.0;
        }
        mrjVersion = versionValue;
    }
}

