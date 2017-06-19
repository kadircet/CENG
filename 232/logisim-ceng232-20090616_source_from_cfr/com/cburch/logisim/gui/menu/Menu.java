/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.menu;

import javax.swing.JMenu;

abstract class Menu
extends JMenu {
    Menu() {
    }

    abstract void computeEnabled();
}

