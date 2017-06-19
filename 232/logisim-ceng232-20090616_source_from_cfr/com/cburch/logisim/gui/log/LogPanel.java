/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.proj.Project;
import java.awt.LayoutManager;
import javax.swing.JPanel;

abstract class LogPanel
extends JPanel {
    private LogFrame logFrame;

    public LogPanel(LogFrame frame) {
        this.logFrame = frame;
    }

    public LogPanel(LogFrame frame, LayoutManager manager) {
        super(manager);
        this.logFrame = frame;
    }

    public abstract String getTitle();

    public abstract String getHelpText();

    public abstract void localeChanged();

    public abstract void modelChanged(Model var1, Model var2);

    LogFrame getLogFrame() {
        return this.logFrame;
    }

    Project getProject() {
        return this.logFrame.getProject();
    }

    Model getModel() {
        return this.logFrame.getModel();
    }

    Selection getSelection() {
        return this.logFrame.getModel().getSelection();
    }
}

