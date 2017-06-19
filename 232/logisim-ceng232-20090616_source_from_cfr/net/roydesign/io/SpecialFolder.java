/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.io;

import java.io.File;
import java.io.FileNotFoundException;
import net.roydesign.mac.MRJAdapter;

public class SpecialFolder {
    private static final String osName = System.getProperty("os.name");

    private SpecialFolder() {
    }

    public static File getHomeFolder() {
        return new File(System.getProperty("user.home"));
    }

    public static File getPreferencesFolder() throws FileNotFoundException {
        if (MRJAdapter.mrjVersion != -1.0f) {
            return MRJAdapter.findFolder(-32763, 1886545254, true);
        }
        if (osName.startsWith("Windows")) {
            return new File(System.getProperty("user.home"), "Application Data");
        }
        return new File(System.getProperty("user.home"));
    }

    public static File getTemporaryItemsFolder() throws FileNotFoundException {
        if (MRJAdapter.mrjVersion != -1.0f) {
            return MRJAdapter.findFolder(-32763, 1952804208, true);
        }
        if (MRJAdapter.javaVersion >= 1.2f) {
            return new File(System.getProperty("java.io.tmpdir"));
        }
        if (osName.startsWith("Windows")) {
            return new File("c:\temp\"");
        }
        throw new FileNotFoundException();
    }

    public static File getDesktopFolder() throws FileNotFoundException {
        if (MRJAdapter.mrjVersion != -1.0f) {
            return MRJAdapter.findFolder(-32763, 1684370283, true);
        }
        if (osName.startsWith("Windows")) {
            return new File(System.getProperty("user.home"), "Desktop");
        }
        throw new FileNotFoundException();
    }

    public static File findMacFolder(short domain, String type, boolean create) throws FileNotFoundException {
        return MRJAdapter.findFolder(domain, type, create);
    }
}

