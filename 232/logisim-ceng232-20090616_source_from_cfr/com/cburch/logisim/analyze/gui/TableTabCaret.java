/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.TableTab;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.TruthTableEvent;
import com.cburch.logisim.analyze.model.TruthTableListener;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;

class TableTabCaret {
    private static Color SELECT_COLOR = new Color(192, 192, 255);
    private Listener listener;
    private TableTab table;
    private int cursorRow;
    private int cursorCol;
    private int markRow;
    private int markCol;

    TableTabCaret(TableTab table) {
        this.listener = new Listener();
        this.table = table;
        this.cursorRow = 0;
        this.cursorCol = 0;
        this.markRow = 0;
        this.markCol = 0;
        table.getTruthTable().addTruthTableListener(this.listener);
        table.addMouseListener(this.listener);
        table.addMouseMotionListener(this.listener);
        table.addKeyListener(this.listener);
        table.addFocusListener(this.listener);
        InputMap imap = table.getInputMap();
        ActionMap amap = table.getActionMap();
        AbstractAction nullAction = new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        String nullKey = "null";
        amap.put(nullKey, nullAction);
        imap.put(KeyStroke.getKeyStroke(40, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(38, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(37, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(39, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(34, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(33, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(36, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(35, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(10, 0), nullKey);
    }

    int getCursorRow() {
        return this.cursorRow;
    }

    int getCursorCol() {
        return this.cursorCol;
    }

    int getMarkRow() {
        return this.markRow;
    }

    int getMarkCol() {
        return this.markCol;
    }

    void selectAll() {
        this.table.requestFocus();
        TruthTable model = this.table.getTruthTable();
        this.setCursor(model.getRowCount(), model.getInputColumnCount() + model.getOutputColumnCount(), false);
        this.setCursor(0, 0, true);
    }

    private void setCursor(int row, int col, boolean keepMark) {
        TruthTable model = this.table.getTruthTable();
        int rows = model.getRowCount();
        int cols = model.getInputColumnCount() + model.getOutputColumnCount();
        if (row < 0) {
            row = 0;
        }
        if (col < 0) {
            col = 0;
        }
        if (row >= rows) {
            row = rows - 1;
        }
        if (col >= cols) {
            col = cols - 1;
        }
        if (row != this.cursorRow || col != this.cursorCol || !keepMark && (row != this.markRow || col != this.markCol)) {
            if (!keepMark && this.markRow == this.cursorRow && this.markCol == this.cursorCol) {
                int oldRow = this.cursorRow;
                int oldCol = this.cursorCol;
                this.cursorRow = row;
                this.cursorCol = col;
                this.markRow = row;
                this.markCol = col;
                this.expose(oldRow, oldCol);
                this.expose(this.cursorRow, this.cursorCol);
            } else {
                int r0 = Math.min(row, Math.min(this.cursorRow, this.markRow));
                int r1 = Math.max(row, Math.max(this.cursorRow, this.markRow));
                int c0 = Math.min(col, Math.min(this.cursorCol, this.markCol));
                int c1 = Math.max(col, Math.max(this.cursorCol, this.markCol));
                this.cursorRow = row;
                this.cursorCol = col;
                if (!keepMark) {
                    this.markRow = row;
                    this.markCol = col;
                }
                int x0 = this.table.getX(c0);
                int x1 = this.table.getX(c1) + this.table.getCellWidth();
                int y0 = this.table.getY(r0);
                int y1 = this.table.getY(r1) + this.table.getCellHeight();
                this.table.repaint(x0 - 2, y0 - 2, x1 - x0 + 4, y1 - y0 + 4);
            }
        }
        int cx = this.table.getX(this.cursorCol);
        int cy = this.table.getY(this.cursorRow);
        int cw = this.table.getCellWidth();
        int ch = this.table.getCellHeight();
        if (this.cursorRow == 0) {
            ch += cy;
            cy = 0;
        }
        this.table.scrollRectToVisible(new Rectangle(cx, cy, cw, ch));
    }

    private void expose(int row, int col) {
        if (row >= 0) {
            int x0 = this.table.getX(0);
            int x1 = this.table.getX(this.table.getColumnCount() - 1) + this.table.getCellWidth();
            this.table.repaint(x0 - 2, this.table.getY(row) - 2, x1 - x0 + 4, this.table.getCellHeight() + 4);
        }
    }

    void paintBackground(Graphics g) {
        if (this.cursorRow >= 0 && (this.cursorRow != this.markRow || this.cursorCol != this.markCol)) {
            int t;
            g.setColor(SELECT_COLOR);
            int r0 = this.cursorRow;
            int c0 = this.cursorCol;
            int r1 = this.markRow;
            int c1 = this.markCol;
            if (r1 < r0) {
                t = r1;
                r1 = r0;
                r0 = t;
            }
            if (c1 < c0) {
                t = c1;
                c1 = c0;
                c0 = t;
            }
            int x0 = this.table.getX(c0);
            int y0 = this.table.getY(r0);
            int x1 = this.table.getX(c1) + this.table.getCellWidth();
            int y1 = this.table.getY(r1) + this.table.getCellHeight();
            g.fillRect(x0, y0, x1 - x0, y1 - y0);
        }
    }

    void paintForeground(Graphics g) {
        if (!this.table.isFocusOwner()) {
            return;
        }
        if (this.cursorRow >= 0) {
            int x = this.table.getX(this.cursorCol);
            int y = this.table.getY(this.cursorRow);
            GraphicsUtil.switchToWidth(g, 2);
            g.drawRect(x, y, this.table.getCellWidth(), this.table.getCellHeight());
            GraphicsUtil.switchToWidth(g, 2);
        }
    }

    private class Listener
    implements MouseListener,
    MouseMotionListener,
    KeyListener,
    FocusListener,
    TruthTableListener {
        private Listener() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            TableTabCaret.this.table.requestFocus();
            int row = TableTabCaret.this.table.getRow(e);
            int col = TableTabCaret.this.table.getColumn(e);
            TableTabCaret.this.setCursor(row, col, (e.getModifiers() & 1) != 0);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            this.mouseDragged(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int row = TableTabCaret.this.table.getRow(e);
            int col = TableTabCaret.this.table.getColumn(e);
            TableTabCaret.this.setCursor(row, col, true);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
            TruthTable model;
            int inputs;
            int mask = e.getModifiers();
            if ((mask & -2) != 0) {
                return;
            }
            char c = e.getKeyChar();
            Entry newEntry = null;
            switch (c) {
                case ' ': {
                    if (TableTabCaret.this.cursorRow < 0) break;
                    model = TableTabCaret.this.table.getTruthTable();
                    inputs = model.getInputColumnCount();
                    if (TableTabCaret.this.cursorCol < inputs) break;
                    Entry cur = model.getOutputEntry(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol - inputs);
                    cur = cur == Entry.ZERO ? Entry.ONE : (cur == Entry.ONE ? Entry.DONT_CARE : Entry.ZERO);
                    model.setOutputEntry(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol - inputs, cur);
                    break;
                }
                case '0': {
                    newEntry = Entry.ZERO;
                    break;
                }
                case '1': {
                    newEntry = Entry.ONE;
                    break;
                }
                case 'x': {
                    newEntry = Entry.DONT_CARE;
                    break;
                }
                case '\n': {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow + 1, TableTabCaret.this.table.getTruthTable().getInputColumnCount(), (mask & 1) != 0);
                    break;
                }
                case '\b': 
                case '': {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol - 1, (mask & 1) != 0);
                    break;
                }
            }
            if (newEntry != null) {
                model = TableTabCaret.this.table.getTruthTable();
                inputs = model.getInputColumnCount();
                int outputs = model.getOutputColumnCount();
                if (TableTabCaret.this.cursorCol >= inputs) {
                    model.setOutputEntry(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol - inputs, newEntry);
                    if (TableTabCaret.this.cursorCol >= inputs + outputs - 1) {
                        TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow + 1, inputs, false);
                    } else {
                        TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol + 1, false);
                    }
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            if (TableTabCaret.this.cursorRow < 0) {
                return;
            }
            TruthTable model = TableTabCaret.this.table.getTruthTable();
            int rows = model.getRowCount();
            int inputs = model.getInputColumnCount();
            int outputs = model.getOutputColumnCount();
            int cols = inputs + outputs;
            boolean shift = (e.getModifiers() & 1) != 0;
            switch (e.getKeyCode()) {
                case 38: {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow - 1, TableTabCaret.this.cursorCol, shift);
                    break;
                }
                case 37: {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol - 1, shift);
                    break;
                }
                case 40: {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow + 1, TableTabCaret.this.cursorCol, shift);
                    break;
                }
                case 39: {
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol + 1, shift);
                    break;
                }
                case 36: {
                    if (TableTabCaret.this.cursorCol == 0) {
                        TableTabCaret.this.setCursor(0, 0, shift);
                        break;
                    }
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, 0, shift);
                    break;
                }
                case 35: {
                    if (TableTabCaret.this.cursorCol == cols - 1) {
                        TableTabCaret.this.setCursor(rows - 1, cols - 1, shift);
                        break;
                    }
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow, cols - 1, shift);
                    break;
                }
                case 34: {
                    rows = TableTabCaret.access$100((TableTabCaret)TableTabCaret.this).getVisibleRect().height / TableTabCaret.this.table.getCellHeight();
                    if (rows > 2) {
                        --rows;
                    }
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow + rows, TableTabCaret.this.cursorCol, shift);
                    break;
                }
                case 33: {
                    rows = TableTabCaret.access$100((TableTabCaret)TableTabCaret.this).getVisibleRect().height / TableTabCaret.this.table.getCellHeight();
                    if (rows > 2) {
                        --rows;
                    }
                    TableTabCaret.this.setCursor(TableTabCaret.this.cursorRow - rows, TableTabCaret.this.cursorCol, shift);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            if (TableTabCaret.this.cursorRow >= 0) {
                TableTabCaret.this.expose(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol);
            }
        }

        @Override
        public void focusLost(FocusEvent e) {
            if (TableTabCaret.this.cursorRow >= 0) {
                TableTabCaret.this.expose(TableTabCaret.this.cursorRow, TableTabCaret.this.cursorCol);
            }
        }

        @Override
        public void cellsChanged(TruthTableEvent event) {
        }

        @Override
        public void structureChanged(TruthTableEvent event) {
            TruthTable model = event.getSource();
            int inputs = model.getInputColumnCount();
            int outputs = model.getOutputColumnCount();
            int rows = model.getRowCount();
            int cols = inputs + outputs;
            boolean changed = false;
            if (TableTabCaret.this.cursorRow >= rows) {
                TableTabCaret.this.cursorRow = rows - 1;
                changed = true;
            }
            if (TableTabCaret.this.cursorCol >= cols) {
                TableTabCaret.this.cursorCol = cols - 1;
                changed = true;
            }
            if (TableTabCaret.this.markRow >= rows) {
                TableTabCaret.this.markRow = rows - 1;
                changed = true;
            }
            if (TableTabCaret.this.markCol >= cols) {
                TableTabCaret.this.markCol = cols - 1;
                changed = true;
            }
            if (changed) {
                TableTabCaret.this.table.repaint();
            }
        }
    }

}

