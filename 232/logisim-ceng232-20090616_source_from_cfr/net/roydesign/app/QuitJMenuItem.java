/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.app;

import java.awt.Toolkit;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.KeyStroke;
import net.roydesign.app.Application;
import net.roydesign.mac.MRJAdapter;

public class QuitJMenuItem
extends JMenuItem {
    QuitJMenuItem(Application application) {
        super("Quit");
        this.setAccelerator(KeyStroke.getKeyStroke(81, Toolkit.getDefaultToolkit().getMenuShortcutKeyMask()));
        String appName = application.getName();
        if (MRJAdapter.mrjVersion >= 3.0f && appName != null) {
            this.setText("Quit " + appName);
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

