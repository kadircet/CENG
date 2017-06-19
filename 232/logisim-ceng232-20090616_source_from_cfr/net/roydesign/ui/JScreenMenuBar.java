/*
 * Decompiled with CFR 0_114.
 */
package net.roydesign.ui;

import java.awt.Component;
import java.awt.Container;
import javax.swing.JFrame;
import javax.swing.JMenuBar;
import net.roydesign.mac.MRJAdapter;
import net.roydesign.ui.JScreenMenu;

public class JScreenMenuBar
extends JMenuBar {
    public void addNotify() {
        JFrame f = this.getParentFrame();
        int n = this.getComponentCount();
        int i = n - 1;
        while (i >= 0) {
            Component m = this.getComponent(i);
            if (m instanceof JScreenMenu && !((JScreenMenu)m).isUsedBy(f)) {
                if (MRJAdapter.isSwingUsingScreenMenuBar()) {
                    m.setEnabled(false);
                } else {
                    m.setVisible(false);
                }
            }
            --i;
        }
        super.addNotify();
    }

    protected JFrame getParentFrame() {
        Container comp = this.getParent();
        while (comp != null && !(comp instanceof JFrame)) {
            comp = comp.getParent();
        }
        return (JFrame)comp;
    }
}

