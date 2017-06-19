/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.prefs;

import com.cburch.logisim.gui.prefs.PreferencesFrame;
import java.awt.LayoutManager;
import javax.swing.JPanel;

abstract class OptionsPanel
extends JPanel {
    private PreferencesFrame optionsFrame;

    public OptionsPanel(PreferencesFrame frame) {
        this.optionsFrame = frame;
    }

    public OptionsPanel(PreferencesFrame frame, LayoutManager manager) {
        super(manager);
        this.optionsFrame = frame;
    }

    public abstract String getTitle();

    public abstract String getHelpText();

    public abstract void localeChanged();

    PreferencesFrame getPreferencesFrame() {
        return this.optionsFrame;
    }
}

