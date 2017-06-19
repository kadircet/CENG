/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.WindowMenu;
import com.cburch.logisim.util.WindowMenuItem;
import com.cburch.logisim.util.WindowMenuManager;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.util.Collection;
import java.util.HashMap;
import javax.swing.JFrame;
import javax.swing.JRadioButtonMenuItem;

public abstract class WindowMenuItemManager {
    private MyListener myListener;
    private String text;
    private boolean persistent;
    private boolean listenerAdded;
    private boolean inManager;
    private HashMap menuItems;

    public WindowMenuItemManager(String text, boolean persistent) {
        this.myListener = new MyListener();
        this.listenerAdded = false;
        this.inManager = false;
        this.menuItems = new HashMap();
        this.text = text;
        this.persistent = persistent;
        if (persistent) {
            WindowMenuManager.addManager(this);
        }
    }

    public abstract JFrame getJFrame(boolean var1);

    public void frameOpened(JFrame frame) {
        if (!this.listenerAdded) {
            frame.addWindowListener(this.myListener);
            this.listenerAdded = true;
        }
        this.addToManager();
        WindowMenuManager.setCurrentManager(this);
    }

    public void frameClosed(JFrame frame) {
        if (!this.persistent) {
            if (this.listenerAdded) {
                frame.removeWindowListener(this.myListener);
                this.listenerAdded = false;
            }
            this.removeFromManager();
        }
    }

    private void addToManager() {
        if (!this.persistent && !this.inManager) {
            WindowMenuManager.addManager(this);
            this.inManager = true;
        }
    }

    private void removeFromManager() {
        if (!this.persistent && this.inManager) {
            this.inManager = false;
            for (WindowMenu menu : WindowMenuManager.getMenus()) {
                JRadioButtonMenuItem menuItem = (JRadioButtonMenuItem)this.menuItems.get(menu);
                menu.removeMenuItem(this, menuItem);
            }
            WindowMenuManager.removeManager(this);
        }
    }

    public String getText() {
        return this.text;
    }

    public void setText(String value) {
        this.text = value;
        for (JRadioButtonMenuItem menuItem : this.menuItems.values()) {
            menuItem.setText(this.text);
        }
    }

    JRadioButtonMenuItem getMenuItem(WindowMenu key) {
        return (JRadioButtonMenuItem)this.menuItems.get(key);
    }

    void createMenuItem(WindowMenu menu) {
        WindowMenuItem ret = new WindowMenuItem(this);
        this.menuItems.put(menu, ret);
        menu.addMenuItem(this, ret, this.persistent);
    }

    void removeMenuItem(WindowMenu menu) {
        JRadioButtonMenuItem item = (JRadioButtonMenuItem)this.menuItems.remove(menu);
        if (item != null) {
            menu.removeMenuItem(this, item);
        }
    }

    void setSelected(boolean selected) {
        for (JRadioButtonMenuItem item : this.menuItems.values()) {
            item.setSelected(selected);
        }
    }

    private class MyListener
    implements WindowListener {
        private MyListener() {
        }

        @Override
        public void windowOpened(WindowEvent event) {
        }

        @Override
        public void windowClosing(WindowEvent event) {
            JFrame frame = WindowMenuItemManager.this.getJFrame(false);
            if (frame.getDefaultCloseOperation() == 1) {
                WindowMenuItemManager.this.removeFromManager();
            }
        }

        @Override
        public void windowClosed(WindowEvent event) {
            WindowMenuItemManager.this.removeFromManager();
        }

        @Override
        public void windowDeiconified(WindowEvent event) {
        }

        @Override
        public void windowIconified(WindowEvent event) {
            WindowMenuItemManager.this.addToManager();
            WindowMenuManager.setCurrentManager(WindowMenuItemManager.this);
        }

        @Override
        public void windowActivated(WindowEvent event) {
            WindowMenuItemManager.this.addToManager();
            WindowMenuManager.setCurrentManager(WindowMenuItemManager.this);
        }

        @Override
        public void windowDeactivated(WindowEvent event) {
            WindowMenuManager.unsetCurrentManager(WindowMenuItemManager.this);
        }
    }

}

