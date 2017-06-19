/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JRootPane;

class DefaultRegistry {
    private JRootPane rootPane;

    public DefaultRegistry(JRootPane rootPane) {
        this.rootPane = rootPane;
        rootPane.setDefaultButton(null);
    }

    public void registerDefaultButton(JComponent comp, JButton button) {
        comp.addFocusListener(new MyListener(button));
    }

    private class MyListener
    implements FocusListener {
        JButton defaultButton;

        MyListener(JButton defaultButton) {
            this.defaultButton = defaultButton;
        }

        @Override
        public void focusGained(FocusEvent event) {
            DefaultRegistry.this.rootPane.setDefaultButton(this.defaultButton);
        }

        @Override
        public void focusLost(FocusEvent event) {
            JButton currentDefault = DefaultRegistry.this.rootPane.getDefaultButton();
            if (currentDefault == this.defaultButton) {
                DefaultRegistry.this.rootPane.setDefaultButton(null);
            }
        }
    }

}

