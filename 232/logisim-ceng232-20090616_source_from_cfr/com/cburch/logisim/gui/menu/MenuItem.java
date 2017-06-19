/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import com.cburch.logisim.gui.menu.LogisimMenuItem;
import com.cburch.logisim.gui.menu.Menu;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.JMenuItem;

class MenuItem
extends JMenuItem
implements ActionListener {
    private LogisimMenuItem menuItem;
    private Menu menu;
    private boolean enabled;
    private ArrayList listeners = new ArrayList();

    public MenuItem(Menu menu, LogisimMenuItem menuItem) {
        this.menu = menu;
        this.menuItem = menuItem;
        this.enabled = true;
        this.listeners = new ArrayList();
        super.addActionListener(this);
        this.computeEnabled();
    }

    boolean hasListeners() {
        return !this.listeners.isEmpty();
    }

    @Override
    public void addActionListener(ActionListener l) {
        this.listeners.add(l);
        this.computeEnabled();
        this.menu.computeEnabled();
    }

    @Override
    public void removeActionListener(ActionListener l) {
        this.listeners.remove(l);
        this.computeEnabled();
        this.menu.computeEnabled();
    }

    @Override
    public void setEnabled(boolean value) {
        this.enabled = value;
        this.computeEnabled();
    }

    private void computeEnabled() {
        super.setEnabled(this.enabled && this.hasListeners());
    }

    @Override
    public void actionPerformed(ActionEvent event) {
        if (!this.listeners.isEmpty()) {
            ActionEvent e = new ActionEvent(this.menuItem, event.getID(), event.getActionCommand(), event.getWhen(), event.getModifiers());
            for (ActionListener l : this.listeners) {
                l.actionPerformed(e);
            }
        }
    }
}

