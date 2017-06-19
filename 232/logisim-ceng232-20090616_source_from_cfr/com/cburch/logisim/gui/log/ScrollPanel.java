/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.log.LogPanel;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.TablePanel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.LayoutManager;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;

class ScrollPanel
extends LogPanel {
    private TablePanel table;

    public ScrollPanel(LogFrame frame) {
        super(frame);
        this.table = new TablePanel(frame);
        JScrollPane pane = new JScrollPane(this.table, 22, 30);
        pane.setVerticalScrollBar(this.table.getVerticalScrollBar());
        this.setLayout(new BorderLayout());
        this.add(pane);
    }

    @Override
    public String getTitle() {
        return this.table.getTitle();
    }

    @Override
    public String getHelpText() {
        return this.table.getHelpText();
    }

    @Override
    public void localeChanged() {
        this.table.localeChanged();
    }

    @Override
    public void modelChanged(Model oldModel, Model newModel) {
        this.table.modelChanged(oldModel, newModel);
    }
}

