/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.LogFrame;
import com.cburch.logisim.gui.log.LogPanel;
import com.cburch.logisim.gui.log.Model;
import com.cburch.logisim.gui.log.ModelEvent;
import com.cburch.logisim.gui.log.ModelListener;
import com.cburch.logisim.gui.log.Selection;
import com.cburch.logisim.gui.log.SelectionItem;
import com.cburch.logisim.gui.log.Strings;
import com.cburch.logisim.gui.log.ValueLog;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;
import javax.swing.BoundedRangeModel;
import javax.swing.JScrollBar;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

class TablePanel
extends LogPanel {
    private static final Font HEAD_FONT = new Font("Serif", 1, 14);
    private static final Font BODY_FONT = new Font("Serif", 0, 14);
    private static final int COLUMN_SEP = 8;
    private static final int HEADER_SEP = 4;
    private MyListener myListener;
    private int cellWidth;
    private int cellHeight;
    private int rowCount;
    private int tableWidth;
    private int tableHeight;
    private VerticalScrollBar vsb;

    public TablePanel(LogFrame frame) {
        super(frame);
        this.myListener = new MyListener();
        this.cellWidth = 25;
        this.cellHeight = 15;
        this.rowCount = 0;
        this.vsb = new VerticalScrollBar();
        this.modelChanged(null, this.getModel());
    }

    @Override
    public String getTitle() {
        return Strings.get("tableTab");
    }

    @Override
    public String getHelpText() {
        return Strings.get("tableHelp");
    }

    @Override
    public void localeChanged() {
        this.computePreferredSize();
        this.repaint();
    }

    @Override
    public void modelChanged(Model oldModel, Model newModel) {
        if (oldModel != null) {
            oldModel.removeModelListener(this.myListener);
        }
        if (newModel != null) {
            newModel.addModelListener(this.myListener);
        }
    }

    public int getColumn(MouseEvent event) {
        int x = event.getX() - (this.getWidth() - this.tableWidth) / 2;
        if (x < 0) {
            return -1;
        }
        Selection sel = this.getModel().getSelection();
        int ret = (x + 4) / (this.cellWidth + 8);
        return ret >= 0 && ret < sel.size() ? ret : -1;
    }

    public int getRow(MouseEvent event) {
        int y = event.getY() - (this.getHeight() - this.tableHeight) / 2;
        if (y < this.cellHeight + 4) {
            return -1;
        }
        int ret = (y - this.cellHeight - 4) / this.cellHeight;
        return ret >= 0 && ret < this.rowCount ? ret : -1;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Dimension sz = this.getSize();
        int top = Math.max(0, (sz.height - this.tableHeight) / 2);
        int left = Math.max(0, (sz.width - this.tableWidth) / 2);
        Model model = this.getModel();
        if (model == null) {
            return;
        }
        Selection sel = model.getSelection();
        int columns = sel.size();
        if (columns == 0) {
            g.setFont(BODY_FONT);
            GraphicsUtil.drawCenteredText(g, Strings.get("tableEmptyMessage"), sz.width / 2, sz.height / 2);
            return;
        }
        g.setColor(Color.GRAY);
        int lineY = top + this.cellHeight + 2;
        g.drawLine(left, lineY, left + this.tableWidth, lineY);
        g.setColor(Color.BLACK);
        g.setFont(HEAD_FONT);
        FontMetrics headerMetric = g.getFontMetrics();
        int x = left;
        int y = top + headerMetric.getAscent() + 1;
        for (int i = 0; i < columns; ++i) {
            x = this.paintHeader(sel.get(i).toShortString(), x, y, g, headerMetric);
        }
        g.setFont(BODY_FONT);
        FontMetrics bodyMetric = g.getFontMetrics();
        Rectangle clip = g.getClipBounds();
        int firstRow = Math.max(0, (clip.y - y) / this.cellHeight - 1);
        int lastRow = Math.min(this.rowCount, 2 + (clip.y + clip.height - y) / this.cellHeight);
        int y0 = top + this.cellHeight + 4;
        x = left;
        for (int col = 0; col < columns; ++col) {
            SelectionItem item = sel.get(col);
            ValueLog log = model.getValueLog(item);
            int radix = item.getRadix();
            int offs = this.rowCount - log.size();
            y = y0 + Math.max(offs, firstRow) * this.cellHeight;
            for (int row = Math.max((int)offs, (int)firstRow); row < lastRow; ++row) {
                Value val = log.get(row - offs);
                String label = val.toDisplayString(radix);
                int width = bodyMetric.stringWidth(label);
                g.drawString(label, x + (this.cellWidth - width) / 2, y + bodyMetric.getAscent());
                y += this.cellHeight;
            }
            x += this.cellWidth + 8;
        }
    }

    private int paintHeader(String header, int x, int y, Graphics g, FontMetrics fm) {
        int width = fm.stringWidth(header);
        g.drawString(header, x + (this.cellWidth - width) / 2, y);
        return x + this.cellWidth + 8;
    }

    private void computePreferredSize() {
        Model model = this.getModel();
        Selection sel = model.getSelection();
        int columns = sel.size();
        if (columns == 0) {
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
            for (int i = 0; i < columns; ++i) {
                String header = sel.get(i).toShortString();
                this.cellWidth = Math.max(this.cellWidth, fm.stringWidth(header));
            }
        }
        this.tableWidth = (this.cellWidth + 8) * columns - 8;
        this.tableHeight = this.cellHeight * (1 + this.rowCount) + 4;
        this.setPreferredSize(new Dimension(this.tableWidth, this.tableHeight));
        this.revalidate();
        this.repaint();
    }

    JScrollBar getVerticalScrollBar() {
        return this.vsb;
    }

    private class VerticalScrollBar
    extends JScrollBar
    implements ChangeListener {
        private int oldMaximum;
        private int oldExtent;

        public VerticalScrollBar() {
            this.oldMaximum = -1;
            this.oldExtent = -1;
            this.getModel().addChangeListener(this);
        }

        @Override
        public int getUnitIncrement(int direction) {
            int curY = this.getValue();
            if (direction > 0) {
                return curY > 0 ? TablePanel.this.cellHeight : TablePanel.this.cellHeight + 4;
            }
            return curY > TablePanel.this.cellHeight + 4 ? TablePanel.this.cellHeight : TablePanel.this.cellHeight + 4;
        }

        @Override
        public int getBlockIncrement(int direction) {
            int curY = this.getValue();
            int curHeight = this.getVisibleAmount();
            int numCells = curHeight / TablePanel.this.cellHeight - 1;
            if (numCells <= 0) {
                numCells = 1;
            }
            if (direction > 0) {
                return curY > 0 ? numCells * TablePanel.this.cellHeight : numCells * TablePanel.this.cellHeight + 4;
            }
            return curY > TablePanel.this.cellHeight + 4 ? numCells * TablePanel.this.cellHeight : numCells * TablePanel.this.cellHeight + 4;
        }

        @Override
        public void stateChanged(ChangeEvent event) {
            int newMaximum = this.getMaximum();
            int newExtent = this.getVisibleAmount();
            if (this.oldMaximum != newMaximum || this.oldExtent != newExtent) {
                if (this.getValue() + this.oldExtent >= this.oldMaximum) {
                    this.setValue(newMaximum - newExtent);
                }
                this.oldMaximum = newMaximum;
                this.oldExtent = newExtent;
            }
        }
    }

    private class MyListener
    implements ModelListener {
        private MyListener() {
        }

        @Override
        public void selectionChanged(ModelEvent event) {
            this.computeRowCount();
        }

        @Override
        public void entryAdded(ModelEvent event, Value[] values) {
            int oldCount = TablePanel.this.rowCount;
            this.computeRowCount();
            if (oldCount == TablePanel.this.rowCount) {
                int value = TablePanel.this.vsb.getValue();
                if (value > TablePanel.this.vsb.getMinimum() && value < TablePanel.this.vsb.getMaximum() - TablePanel.this.vsb.getVisibleAmount()) {
                    TablePanel.this.vsb.setValue(TablePanel.this.vsb.getValue() - TablePanel.this.vsb.getUnitIncrement(-1));
                } else {
                    TablePanel.this.repaint();
                }
            }
        }

        @Override
        public void filePropertyChanged(ModelEvent event) {
        }

        private void computeRowCount() {
            Model model = TablePanel.this.getModel();
            Selection sel = model.getSelection();
            int rows = 0;
            for (int i = sel.size() - 1; i >= 0; --i) {
                int x = model.getValueLog(sel.get(i)).size();
                if (x <= rows) continue;
                rows = x;
            }
            if (TablePanel.this.rowCount != rows) {
                TablePanel.this.rowCount = rows;
                TablePanel.this.computePreferredSize();
            }
        }
    }

}

