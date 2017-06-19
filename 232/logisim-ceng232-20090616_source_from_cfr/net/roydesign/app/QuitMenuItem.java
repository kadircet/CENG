/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.app;

import java.awt.MenuItem;
import java.awt.MenuShortcut;
import java.awt.event.ActionListener;
import net.roydesign.app.Application;
import net.roydesign.mac.MRJAdapter;

public class QuitMenuItem
extends MenuItem {
    QuitMenuItem(Application application) {
        super("Quit", new MenuShortcut(81));
        String appName = application.getName();
        if (MRJAdapter.mrjVersion >= 3.0f && appName != null) {
            this.setLabel("Quit " + appName);
        }
    }

    public void addActionListener(ActionListener l) {
        MRJAdapter.addQuitApplicationListener(l, this);
        super.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        MRJAdapter.removeQuitApplicationListener(l);
        super.removeActionListener(l);
    }

    public static boolean isAutomaticallyPresent() {
        return MRJAdapter.isQuitAutomaticallyPresent();
    }
}

