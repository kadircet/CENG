/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TabInterface;
import com.cburch.logisim.analyze.gui.TableTabCaret;
import com.cburch.logisim.analyze.gui.TableTabClip;
import com.cburch.logisim.analyze.gui.TruthTablePanel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.TruthTableEvent;
import com.cburch.logisim.analyze.model.TruthTableListener;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.JPanel;
import javax.swing.JScrollBar;

class TableTab
extends JPanel
implements TruthTablePanel,
TabInterface {
    private static final Font HEAD_FONT = new Font("Serif", 1, 14);
    private static final Font BODY_FONT = new Font("Serif", 0, 14);
    private static final int COLUMN_SEP = 8;
    private static final int HEADER_SEP = 4;
    private MyListener myListener;
    private TruthTable table;
    private int cellWidth;
    private int cellHeight;
    private int tableWidth;
    private int tableHeight;
    private int provisionalX;
    private int provisionalY;
    private Entry provisionalValue;
    private TableTabCaret caret;
    private TableTabClip clip;

    public TableTab(TruthTable table) {
        this.myListener = new MyListener();
        this.cellWidth = 25;
        this.cellHeight = 15;
        this.provisionalValue = null;
        this.table = table;
        table.addTruthTableListener(this.myListener);
        this.setToolTipText(" ");
        this.caret = new TableTabCaret(this);
        this.clip = new TableTabClip(this);
    }

    @Override
    public TruthTable getTruthTable() {
        return this.table;
    }

    TableTabCaret getCaret() {
        return this.caret;
    }

    void localeChanged() {
        this.computePreferredSize();
        this.repaint();
    }

    public int getColumn(MouseEvent event) {
        int x = event.getX() - (this.getWidth() - this.tableWidth) / 2;
        if (x < 0) {
            return -1;
        }
        int inputs = this.table.getInputColumnCount();
        int cols = inputs + this.table.getOutputColumnCount();
        int ret = (x + 4) / (this.cellWidth + 8);
        if (inputs == 0) {
            --ret;
        }
        return ret >= 0 ? (ret < cols ? ret : cols) : -1;
    }

    int getColumnCount() {
        int inputs = this.table.getInputColumnCount();
        int outputs = this.table.getOutputColumnCount();
        return inputs + outputs;
    }

    @Override
    public int getOutputColumn(MouseEvent event) {
        int ret;
        int inputs = this.table.getInputColumnCount();
        if (inputs == 0) {
            inputs = 1;
        }
        return (ret = this.getColumn(event)) >= inputs ? ret - inputs : -1;
    }

    @Override
    public int getRow(MouseEvent event) {
        int y = event.getY() - (this.getHeight() - this.tableHeight) / 2;
        if (y < this.cellHeight + 4) {
            return -1;
        }
        int ret = (y - this.cellHeight - 4) / this.cellHeight;
        int rows = this.table.getRowCount();
        return ret >= 0 ? (ret < rows ? ret : rows) : -1;
    }

    @Override
    public void setEntryProvisional(int y, int x, Entry value) {
        this.provisionalY = y;
        this.provisionalX = x;
        this.provisionalValue = value;
        int top = (this.getHeight() - this.tableHeight) / 2 + this.cellHeight + 4 + y * this.cellHeight;
        this.repaint(0, top, this.getWidth(), this.cellHeight);
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        int row = this.getRow(event);
        int col = this.getOutputColumn(event);
        Entry entry = this.table.getOutputEntry(row, col);
        return entry.getErrorMessage();
    }

    @Override
    public void paintComponent(Graphics g) {
        int i;
        super.paintComponent(g);
        this.caret.paintBackground(g);
        Dimension sz = this.getSize();
        int top = Math.max(0, (sz.height - this.tableHeight) / 2);
        int left = Math.max(0, (sz.width - this.tableWidth) / 2);
        int inputs = this.table.getInputColumnCount();
        int outputs = this.table.getOutputColumnCount();
        if (inputs == 0 && outputs == 0) {
            g.setFont(BODY_FONT);
            GraphicsUtil.drawCenteredText(g, Strings.get("tableEmptyMessage"), sz.width / 2, sz.height / 2);
            return;
        }
        g.setColor(Color.GRAY);
        int lineX = left + (this.cellWidth + 8) * inputs - 4;
        if (inputs == 0) {
            lineX = left + this.cellWidth + 4;
        }
        int lineY = top + this.cellHeight + 2;
        g.drawLine(left, lineY, left + this.tableWidth, lineY);
        g.drawLine(lineX, top, lineX, top + this.tableHeight);
        g.setColor(Color.BLACK);
        g.setFont(HEAD_FONT);
        FontMetrics headerMetric = g.getFontMetrics();
        int x = left;
        int y = top + headerMetric.getAscent() + 1;
        if (inputs == 0) {
            x = this.paintHeader(Strings.get("tableNullHeader"), x, y, g, headerMetric);
        } else {
            for (i = 0; i < inputs; ++i) {
                x = this.paintHeader(this.table.getInputHeader(i), x, y, g, headerMetric);
            }
        }
        if (outputs == 0) {
            x = this.paintHeader(Strings.get("tableNullHeader"), x, y, g, headerMetric);
        } else {
            for (i = 0; i < outputs; ++i) {
                x = this.paintHeader(this.table.getOutputHeader(i), x, y, g, headerMetric);
            }
        }
        g.setFont(BODY_FONT);
        FontMetrics bodyMetric = g.getFontMetrics();
        y = top + this.cellHeight + 4;
        Rectangle clip = g.getClipBounds();
        int firstRow = Math.max(0, (clip.y - y) / this.cellHeight);
        int lastRow = Math.min(this.table.getRowCount(), 2 + (clip.y + clip.height - y) / this.cellHeight);
        y += firstRow * this.cellHeight;
        if (inputs == 0) {
            left += this.cellWidth + 8;
        }
        boolean provisional = false;
        for (int i2 = firstRow; i2 < lastRow; ++i2) {
            x = left;
            for (int j = 0; j < inputs + outputs; ++j) {
                Entry entry;
                Entry entry2 = entry = j < inputs ? this.table.getInputEntry(i2, j) : this.table.getOutputEntry(i2, j - inputs);
                if (this.provisionalValue != null && i2 == this.provisionalY && j - inputs == this.provisionalX) {
                    provisional = true;
                    entry = this.provisionalValue;
                }
                if (entry.isError()) {
                    g.setColor(ERROR_COLOR);
                    g.fillRect(x, y, this.cellWidth, this.cellHeight);
                    g.setColor(Color.BLACK);
                }
                String label = entry.getDescription();
                int width = bodyMetric.stringWidth(label);
                if (provisional) {
                    provisional = false;
                    g.setColor(Color.GREEN);
                    g.drawString(label, x + (this.cellWidth - width) / 2, y + bodyMetric.getAscent());
                    g.setColor(Color.BLACK);
                } else {
                    g.drawString(label, x + (this.cellWidth - width) / 2, y + bodyMetric.getAscent());
                }
                x += this.cellWidth + 8;
            }
            y += this.cellHeight;
        }
        this.caret.paintForeground(g);
    }

    int getCellWidth() {
        return this.cellWidth;
    }

    int getCellHeight() {
        return this.cellHeight;
    }

    int getX(int col) {
        Dimension sz = this.getSize();
        int left = Math.max(0, (sz.width - this.tableWidth) / 2);
        int inputs = this.table.getInputColumnCount();
        if (inputs == 0) {
            left += this.cellWidth + 8;
        }
        return left + col * (this.cellWidth + 8);
    }

    int getY(int row) {
        Dimension sz = this.getSize();
        int top = Math.max(0, (sz.height - this.tableHeight) / 2);
        return top + this.cellHeight + 4 + row * this.cellHeight;
    }

    private int paintHeader(String header, int x, int y, Graphics g, FontMetrics fm) {
        int width = fm.stringWidth(header);
        g.drawString(header, x + (this.cellWidth - width) / 2, y);
        return x + this.cellWidth + 8;
    }

    private void computePreferredSize() {
        int inputs = this.table.getInputColumnCount();
        int outputs = this.table.getOutputColumnCount();
        if (inputs == 0 && outputs == 0) {
            this.setPreferredSize(new Dimension(0, 0));
            return;
        }
        Graphics g = this.getGraphics();
        if (g == null) {
            this.cellHeight = 16;
            this.cellWidth = 24;
        } else {
            FontMetrics fm = g.getFontMetrics(HEAD_FONT);
            this.cellHeight = fm.getHeight();
            this.cellWidth = 24;
            if (inputs == 0 || outputs == 0) {
                this.cellWidth = Math.max(this.cellWidth, fm.stringWidth(Strings.get("tableNullHeader")));
            }
            for (int i = 0; i < inputs + outputs; ++i) {
                String header = i < inputs ? this.table.getInputHeader(i) : this.table.getOutputHeader(i - inputs);
                this.cellWidth = Math.max(this.cellWidth, fm.stringWidth(header));
            }
        }
        if (inputs == 0) {
            inputs = 1;
        }
        if (outputs == 0) {
            outputs = 1;
        }
        this.tableWidth = (this.cellWidth + 8) * (inputs + outputs) - 8;
        this.tableHeight = this.cellHeight * (1 + this.table.getRowCount()) + 4;
        this.setPreferredSize(new Dimension(this.tableWidth, this.tableHeight));
        this.revalidate();
        this.repaint();
    }

    JScrollBar getVerticalScrollBar() {
        return new JScrollBar(){

            @Override
            public int getUnitIncrement(int direction) {
                int curY = this.getValue();
                if (direction > 0) {
                    return curY > 0 ? TableTab.this.cellHeight : TableTab.this.cellHeight + 4;
                }
                return curY > TableTab.this.cellHeight + 4 ? TableTab.this.cellHeight : TableTab.this.cellHeight + 4;
            }

            @Override
            public int getBlockIncrement(int direction) {
                int curY = this.getValue();
                int curHeight = this.getVisibleAmount();
                int numCells = curHeight / TableTab.this.cellHeight - 1;
                if (numCells <= 0) {
                    numCells = 1;
                }
                if (direction > 0) {
                    return curY > 0 ? numCells * TableTab.this.cellHeight : numCells * TableTab.this.cellHeight + 4;
                }
                return curY > TableTab.this.cellHeight + 4 ? numCells * TableTab.this.cellHeight : numCells * TableTab.this.cellHeight + 4;
            }
        };
    }

    @Override
    public void copy() {
        this.requestFocus();
        this.clip.copy();
    }

    @Override
    public void paste() {
        this.requestFocus();
        this.clip.paste();
    }

    @Override
    public void delete() {
        int t;
        this.requestFocus();
        int r0 = this.caret.getCursorRow();
        int r1 = this.caret.getMarkRow();
        int c0 = this.caret.getCursorCol();
        int c1 = this.caret.getMarkCol();
        if (r0 < 0 || r1 < 0) {
            return;
        }
        if (r1 < r0) {
            t = r0;
            r0 = r1;
            r1 = t;
        }
        if (c1 < c0) {
            t = c0;
            c0 = c1;
            c1 = t;
        }
        int inputs = this.table.getInputColumnCount();
        for (int c = c0; c <= c1; ++c) {
            if (c < inputs) continue;
            for (int r = r0; r <= r1; ++r) {
                this.table.setOutputEntry(r, c - inputs, Entry.DONT_CARE);
            }
        }
    }

    @Override
    public void selectAll() {
        this.caret.selectAll();
    }

    private class MyListener
    implements TruthTableListener {
        private MyListener() {
        }

        @Override
        public void cellsChanged(TruthTableEvent event) {
            TableTab.this.repaint();
        }

        @Override
        public void structureChanged(TruthTableEvent event) {
            TableTab.this.computePreferredSize();
        }
    }

}

