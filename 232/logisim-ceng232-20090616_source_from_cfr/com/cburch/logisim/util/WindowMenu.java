/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.LocaleListener;
import com.cburch.logisim.util.LocaleManager;
import com.cburch.logisim.util.MacCompatibility;
import com.cburch.logisim.util.Strings;
import com.cburch.logisim.util.WindowMenuItem;
import com.cburch.logisim.util.WindowMenuItemManager;
import com.cburch.logisim.util.WindowMenuManager;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import javax.swing.AbstractButton;
import javax.swing.ButtonGroup;
import javax.swing.JFrame;
import javax.swing.JMenu;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.KeyStroke;

public class WindowMenu
extends JMenu {
    private JFrame owner;
    private MyListener myListener;
    private JMenuItem minimize;
    private JMenuItem zoom;
    private JRadioButtonMenuItem nullItem;
    private ArrayList persistentItems;
    private ArrayList transientItems;

    public WindowMenu(JFrame owner) {
        this.myListener = new MyListener();
        this.minimize = new JMenuItem();
        this.zoom = new JMenuItem();
        this.nullItem = new JRadioButtonMenuItem();
        this.persistentItems = new ArrayList();
        this.transientItems = new ArrayList();
        this.owner = owner;
        WindowMenuManager.addMenu(this);
        int menuMask = this.getToolkit().getMenuShortcutKeyMask();
        this.minimize.setAccelerator(KeyStroke.getKeyStroke(77, menuMask));
        if (owner == null) {
            this.minimize.setEnabled(false);
            this.zoom.setEnabled(false);
        } else {
            this.minimize.addActionListener(this.myListener);
            this.zoom.addActionListener(this.myListener);
        }
        this.computeEnabled();
        this.computeContents();
        LocaleManager.addLocaleListener(this.myListener);
        this.myListener.localeChanged();
    }

    void addMenuItem(Object source, JRadioButtonMenuItem item, boolean persistent) {
        if (persistent) {
            this.persistentItems.add(item);
        } else {
            this.transientItems.add(item);
        }
        item.addActionListener(this.myListener);
        this.computeContents();
    }

    void removeMenuItem(Object source, JRadioButtonMenuItem item) {
        if (this.transientItems.remove(item)) {
            item.removeActionListener(this.myListener);
        }
        this.computeContents();
    }

    void computeEnabled() {
        WindowMenuItemManager currentManager = WindowMenuManager.getCurrentManager();
        this.minimize.setEnabled(currentManager != null);
        this.zoom.setEnabled(currentManager != null);
    }

    void setNullItemSelected(boolean value) {
        this.nullItem.setSelected(value);
    }

    private void computeContents() {
        WindowMenuItemManager currentManager;
        JRadioButtonMenuItem item2;
        ButtonGroup bgroup = new ButtonGroup();
        bgroup.add(this.nullItem);
        this.removeAll();
        this.add(this.minimize);
        this.add(this.zoom);
        if (!this.persistentItems.isEmpty()) {
            this.addSeparator();
            for (JRadioButtonMenuItem item2 : this.persistentItems) {
                bgroup.add(item2);
                this.add(item2);
            }
        }
        if (!this.transientItems.isEmpty()) {
            this.addSeparator();
            for (JRadioButtonMenuItem item2 : this.transientItems) {
                bgroup.add(item2);
                this.add(item2);
            }
        }
        if ((currentManager = WindowMenuManager.getCurrentManager()) != null && (item2 = currentManager.getMenuItem(this)) != null) {
            item2.setSelected(true);
        }
    }

    void doMinimize() {
        if (this.owner == null) {
            return;
        }
        this.owner.setExtendedState(1);
    }

    void doZoom() {
        if (this.owner == null) {
            return;
        }
        this.owner.pack();
        Dimension screenSize = this.owner.getToolkit().getScreenSize();
        Dimension windowSize = this.owner.getPreferredSize();
        Point windowLoc = this.owner.getLocation();
        boolean locChanged = false;
        boolean sizeChanged = false;
        if (windowLoc.x + windowSize.width > screenSize.width) {
            windowLoc.x = Math.max(0, screenSize.width - windowSize.width);
            locChanged = true;
            if (windowLoc.x + windowSize.width > screenSize.width) {
                windowSize.width = screenSize.width - windowLoc.x;
                sizeChanged = true;
            }
        }
        if (windowLoc.y + windowSize.height > screenSize.height) {
            windowLoc.y = Math.max(0, screenSize.height - windowSize.height);
            locChanged = true;
            if (windowLoc.y + windowSize.height > screenSize.height) {
                windowSize.height = screenSize.height - windowLoc.y;
                sizeChanged = true;
            }
        }
        if (locChanged) {
            this.owner.setLocation(windowLoc);
        }
        if (sizeChanged) {
            this.owner.setSize(windowSize);
        }
    }

    private class MyListener
    implements LocaleListener,
    ActionListener {
        private MyListener() {
        }

        @Override
        public void localeChanged() {
            WindowMenu.this.setText(Strings.get("windowMenu"));
            WindowMenu.this.minimize.setText(Strings.get("windowMinimizeItem"));
            WindowMenu.this.zoom.setText(MacCompatibility.isQuitAutomaticallyPresent() ? Strings.get("windowZoomItemMac") : Strings.get("windowZoomItem"));
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            WindowMenuItem choice;
            Object src = e.getSource();
            if (src == WindowMenu.this.minimize) {
                WindowMenu.this.doMinimize();
            } else if (src == WindowMenu.this.zoom) {
                WindowMenu.this.doZoom();
            } else if (src instanceof WindowMenuItem && (choice = (WindowMenuItem)src).isSelected()) {
                WindowMenuItem item = this.findOwnerItem();
                if (item != null) {
                    item.setSelected(true);
                }
                choice.actionPerformed(e);
            }
        }

        private WindowMenuItem findOwnerItem() {
            for (WindowMenuItem i2 : WindowMenu.this.persistentItems) {
                if (i2.getJFrame() != WindowMenu.this.owner) continue;
                return i2;
            }
            for (WindowMenuItem i2 : WindowMenu.this.transientItems) {
                if (i2.getJFrame() != WindowMenu.this.owner) continue;
                return i2;
            }
            return null;
        }
    }

}

