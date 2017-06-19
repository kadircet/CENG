/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Frame;
import java.awt.Menu;
import java.awt.MenuBar;
import java.awt.MenuContainer;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.ScreenMenu;

public class ScreenMenuBar
extends MenuBar {
    public void addNotify() {
        Frame f = (Frame)this.getParent();
        int n = this.getMenuCount();
        int i = n - 1;
        while (i >= 0) {
            Menu m = this.getMenu(i);
            if (m instanceof ScreenMenu && !((ScreenMenu)m).isUsedBy(f)) {
                if (MRJAdapter.isAWTUsingScreenMenuBar()) {
                    m.setEnabled(false);
                } else {
                    this.remove(i);
                }
            }
            --i;
        }
        super.addNotify();
    }
}

