/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.opts;

import com.cburch.logisim.file.LogisimFile;
import com.cburch.logisim.file.Options;
import com.cburch.logisim.gui.opts.OptionsFrame;
import com.cburch.logisim.proj.Project;
import java.awt.LayoutManager;
import javax.swing.JPanel;

abstract class OptionsPanel
extends JPanel {
    private OptionsFrame optionsFrame;

    public OptionsPanel(OptionsFrame frame) {
        this.optionsFrame = frame;
    }

    public OptionsPanel(OptionsFrame frame, LayoutManager manager) {
        super(manager);
        this.optionsFrame = frame;
    }

    public abstract String getTitle();

    public abstract String getHelpText();

    public abstract void localeChanged();

    OptionsFrame getOptionsFrame() {
        return this.optionsFrame;
    }

    Project getProject() {
        return this.optionsFrame.getProject();
    }

    LogisimFile getLogisimFile() {
        return this.optionsFrame.getLogisimFile();
    }

    Options getOptions() {
        return this.optionsFrame.getOptions();
    }
}

