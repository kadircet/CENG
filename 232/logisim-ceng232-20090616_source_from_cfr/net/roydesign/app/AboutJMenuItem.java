/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.app;

import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import net.roydesign.app.Application;
import net.roydesign.mac.MRJAdapter;

public class AboutJMenuItem
extends JMenuItem {
    AboutJMenuItem(Application application) {
        super("About");
        String appName = application.getName();
        if (appName != null) {
            this.setText("About " + appName);
        }
    }

    public void addActionListener(ActionListener l) {
        MRJAdapter.addAboutListener(l, this);
        super.addActionListener(l);
    }

    public void removeActionListener(ActionListener l) {
        MRJAdapter.removeAboutListener(l);
        super.removeActionListener(l);
    }

    public static boolean isAutomaticallyPresent() {
        return MRJAdapter.isAboutAutomaticallyPresent();
    }
}

