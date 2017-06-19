/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.app;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.roydesign.mac.MRJAdapter;

public class PreferencesJMenuItem
extends JMenuItem {
    PreferencesJMenuItem() {
        super("Preferences");
    }

    public void addActionListener(ActionListener l) {
        MRJAdapter.addPreferencesListener(l, this);
        super.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        MRJAdapter.removePreferencesListener(l);
        super.removeActionListener(l);
    }

    public void setEnabled(boolean enabled) {
        MRJAdapter.setPreferencesEnabled(enabled);
        super.setEnabled(enabled);
    }

    public static boolean isAutomaticallyPresent() {
        return MRJAdapter.isPreferencesAutomaticallyPresent();
    }
}

