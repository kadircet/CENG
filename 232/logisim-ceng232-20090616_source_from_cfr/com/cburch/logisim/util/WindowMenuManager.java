/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.WindowMenu;
import com.cburch.logisim.util.WindowMenuItemManager;
import java.util.ArrayList;
import java.util.List;

class WindowMenuManager {
    private static ArrayList menus = new ArrayList();
    private static ArrayList managers = new ArrayList();
    private static WindowMenuItemManager currentManager = null;

    private WindowMenuManager() {
    }

    public static void addMenu(WindowMenu menu) {
        for (WindowMenuItemManager manager : managers) {
            manager.createMenuItem(menu);
        }
        menus.add(menu);
    }

    public static void addManager(WindowMenuItemManager manager) {
        for (WindowMenu menu : menus) {
            manager.createMenuItem(menu);
        }
        managers.add(manager);
    }

    public static void removeManager(WindowMenuItemManager manager) {
        for (WindowMenu menu : menus) {
            manager.removeMenuItem(menu);
        }
        managers.remove(manager);
    }

    static List getMenus() {
        return menus;
    }

    static WindowMenuItemManager getCurrentManager() {
        return currentManager;
    }

    static void setCurrentManager(WindowMenuItemManager value) {
        boolean doEnable;
        if (value == currentManager) {
            return;
        }
        boolean bl = doEnable = currentManager == null != (value == null);
        if (currentManager == null) {
            WindowMenuManager.setNullItems(false);
        } else {
            currentManager.setSelected(false);
        }
        currentManager = value;
        if (currentManager == null) {
            WindowMenuManager.setNullItems(true);
        } else {
            currentManager.setSelected(true);
        }
        if (doEnable) {
            WindowMenuManager.enableAll();
        }
    }

    static void unsetCurrentManager(WindowMenuItemManager value) {
        if (value != currentManager) {
            return;
        }
        WindowMenuManager.setCurrentManager(null);
    }

    private static void setNullItems(boolean value) {
        for (WindowMenu menu : menus) {
            menu.setNullItemSelected(value);
        }
    }

    private static void enableAll() {
        for (WindowMenu menu : menus) {
            menu.computeEnabled();
        }
    }
}

