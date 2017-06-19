/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.WindowMenuItemManager;
import com.cburch.logisim.util.WindowMenuManager;
import java.awt.event.ActionEvent;
import javax.swing.JFrame;
import javax.swing.JRadioButtonMenuItem;

class WindowMenuItem
extends JRadioButtonMenuItem {
    private WindowMenuItemManager manager;

    WindowMenuItem(WindowMenuItemManager manager) {
        this.manager = manager;
        this.setText(manager.getText());
        this.setSelected(WindowMenuManager.getCurrentManager() == manager);
    }

    public JFrame getJFrame() {
        return this.manager.getJFrame(true);
    }

    public void actionPerformed(ActionEvent event) {
        JFrame frame = this.getJFrame();
        frame.setExtendedState(0);
        frame.setVisible(true);
        frame.toFront();
    }
}

