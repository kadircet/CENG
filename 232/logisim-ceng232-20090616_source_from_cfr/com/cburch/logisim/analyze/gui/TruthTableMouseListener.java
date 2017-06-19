/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.TruthTablePanel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.TruthTable;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

class TruthTableMouseListener
implements MouseListener {
    private int cellX;
    private int cellY;
    private Entry oldValue;
    private Entry newValue;

    TruthTableMouseListener() {
    }

    @Override
    public void mousePressed(MouseEvent event) {
        TruthTablePanel source = (TruthTablePanel)event.getSource();
        TruthTable model = source.getTruthTable();
        int cols = model.getInputColumnCount() + model.getOutputColumnCount();
        int rows = model.getRowCount();
        this.cellX = source.getOutputColumn(event);
        this.cellY = source.getRow(event);
        if (this.cellX < 0 || this.cellY < 0 || this.cellX >= cols || this.cellY >= rows) {
            return;
        }
        this.oldValue = source.getTruthTable().getOutputEntry(this.cellY, this.cellX);
        this.newValue = this.oldValue == Entry.ZERO ? Entry.ONE : (this.oldValue == Entry.ONE ? Entry.DONT_CARE : Entry.ZERO);
        source.setEntryProvisional(this.cellY, this.cellX, this.newValue);
    }

    @Override
    public void mouseReleased(MouseEvent event) {
        TruthTablePanel source = (TruthTablePanel)event.getSource();
        TruthTable model = source.getTruthTable();
        int cols = model.getInputColumnCount() + model.getOutputColumnCount();
        int rows = model.getRowCount();
        if (this.cellX < 0 || this.cellY < 0 || this.cellX >= cols || this.cellY >= rows) {
            return;
        }
        int x = source.getOutputColumn(event);
        int y = source.getRow(event);
        TruthTable table = source.getTruthTable();
        if (x == this.cellX && y == this.cellY) {
            table.setOutputEntry(y, x, this.newValue);
        }
        source.setEntryProvisional(this.cellY, this.cellX, null);
        this.cellX = -1;
        this.cellY = -1;
    }

    @Override
    public void mouseClicked(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
    }

    @Override
    public void mouseExited(MouseEvent e) {
    }
}

