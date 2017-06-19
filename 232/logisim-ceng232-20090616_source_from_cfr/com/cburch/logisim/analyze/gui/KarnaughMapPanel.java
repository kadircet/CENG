/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.analyze.gui;

import com.cburch.logisim.analyze.gui.Strings;
import com.cburch.logisim.analyze.gui.TruthTablePanel;
import com.cburch.logisim.analyze.model.AnalyzerModel;
import com.cburch.logisim.analyze.model.Entry;
import com.cburch.logisim.analyze.model.Implicant;
import com.cburch.logisim.analyze.model.OutputExpressions;
import com.cburch.logisim.analyze.model.OutputExpressionsEvent;
import com.cburch.logisim.analyze.model.OutputExpressionsListener;
import com.cburch.logisim.analyze.model.TruthTable;
import com.cburch.logisim.analyze.model.TruthTableEvent;
import com.cburch.logisim.analyze.model.TruthTableListener;
import com.cburch.logisim.analyze.model.VariableList;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.List;
import javax.swing.JPanel;

class KarnaughMapPanel
extends JPanel
implements TruthTablePanel {
    private static final Font HEAD_FONT = new Font("Serif", 1, 14);
    private static final Font BODY_FONT = new Font("Serif", 0, 14);
    private static final Color[] IMP_COLORS = new Color[]{new Color(255, 0, 0, 128), new Color(0, 150, 0, 128), new Color(0, 0, 255, 128), new Color(255, 0, 255, 128)};
    private static final int MAX_VARS = 4;
    private static final int[] ROW_VARS = new int[]{0, 0, 1, 1, 2};
    private static final int[] COL_VARS = new int[]{0, 1, 1, 2, 2};
    private static final int CELL_HORZ_SEP = 10;
    private static final int CELL_VERT_SEP = 10;
    private static final int IMP_INSET = 4;
    private static final int IMP_RADIUS = 5;
    private MyListener myListener;
    private AnalyzerModel model;
    private String output;
    private int headHeight;
    private int cellWidth;
    private int cellHeight;
    private int tableWidth;
    private int tableHeight;
    private int provisionalX;
    private int provisionalY;
    private Entry provisionalValue;

    public KarnaughMapPanel(AnalyzerModel model) {
        this.myListener = new MyListener();
        this.cellWidth = 1;
        this.cellHeight = 1;
        this.provisionalValue = null;
        this.model = model;
        model.getOutputExpressions().addOutputExpressionsListener(this.myListener);
        model.getTruthTable().addTruthTableListener(this.myListener);
        this.setToolTipText(" ");
    }

    public void setOutput(String value) {
        boolean recompute = (this.output == null || value == null) && this.output != value;
        this.output = value;
        if (recompute) {
            this.computePreferredSize();
        } else {
            this.repaint();
        }
    }

    @Override
    public TruthTable getTruthTable() {
        return this.model.getTruthTable();
    }

    @Override
    public int getRow(MouseEvent event) {
        TruthTable table = this.model.getTruthTable();
        int inputs = table.getInputColumnCount();
        if (inputs >= ROW_VARS.length) {
            return -1;
        }
        int left = this.computeMargin(this.getWidth(), this.tableWidth);
        int top = this.computeMargin(this.getHeight(), this.tableHeight);
        int x = event.getX() - left - this.headHeight - this.cellWidth;
        int y = event.getY() - top - this.headHeight - this.cellHeight;
        if (x < 0 || y < 0) {
            return -1;
        }
        int row = y / this.cellHeight;
        int col = x / this.cellWidth;
        int rows = 1 << ROW_VARS[inputs];
        int cols = 1 << COL_VARS[inputs];
        if (row >= rows || col >= cols) {
            return -1;
        }
        return this.getTableRow(row, col, rows, cols);
    }

    @Override
    public int getOutputColumn(MouseEvent event) {
        return this.model.getOutputs().indexOf(this.output);
    }

    @Override
    public void setEntryProvisional(int y, int x, Entry value) {
        this.provisionalY = y;
        this.provisionalX = x;
        this.provisionalValue = value;
        this.repaint();
    }

    @Override
    public String getToolTipText(MouseEvent event) {
        TruthTable table = this.model.getTruthTable();
        int row = this.getRow(event);
        int col = this.getOutputColumn(event);
        Entry entry = table.getOutputEntry(row, col);
        return entry.getErrorMessage();
    }

    void localeChanged() {
        this.computePreferredSize();
        this.repaint();
    }

    private void computePreferredSize() {
        Graphics g = this.getGraphics();
        TruthTable table = this.model.getTruthTable();
        String message = null;
        if (this.output == null) {
            message = Strings.get("karnaughNoOutputError");
        } else if (table.getInputColumnCount() > 4) {
            message = Strings.get("karnaughTooManyInputsError");
        }
        if (message != null) {
            if (g == null) {
                this.tableHeight = 15;
                this.tableWidth = 100;
            } else {
                FontMetrics fm = g.getFontMetrics(BODY_FONT);
                this.tableHeight = fm.getHeight();
                this.tableWidth = fm.stringWidth(message);
            }
            this.setPreferredSize(new Dimension(this.tableWidth, this.tableHeight));
            this.repaint();
            return;
        }
        if (g == null) {
            this.headHeight = 16;
            this.cellHeight = 16;
            this.cellWidth = 24;
        } else {
            FontMetrics headFm = g.getFontMetrics(HEAD_FONT);
            this.headHeight = headFm.getHeight();
            FontMetrics fm = g.getFontMetrics(BODY_FONT);
            this.cellHeight = fm.getAscent() + 10;
            this.cellWidth = fm.stringWidth("00") + 10;
        }
        int rows = 1 << ROW_VARS[table.getInputColumnCount()];
        int cols = 1 << COL_VARS[table.getInputColumnCount()];
        this.tableWidth = this.headHeight + this.cellWidth * (cols + 1);
        this.tableHeight = this.headHeight + this.cellHeight * (rows + 1);
        this.setPreferredSize(new Dimension(this.tableWidth, this.tableHeight));
        this.invalidate();
        this.repaint();
    }

    @Override
    public void paintComponent(Graphics g) {
        int i;
        super.paintComponent(g);
        TruthTable table = this.model.getTruthTable();
        int inputCount = table.getInputColumnCount();
        Dimension sz = this.getSize();
        String message = null;
        if (this.output == null) {
            message = Strings.get("karnaughNoOutputError");
        } else if (inputCount > 4) {
            message = Strings.get("karnaughTooManyInputsError");
        }
        if (message != null) {
            g.setFont(BODY_FONT);
            GraphicsUtil.drawCenteredText(g, message, sz.width / 2, sz.height / 2);
            return;
        }
        int left = this.computeMargin(sz.width, this.tableWidth);
        int top = this.computeMargin(sz.height, this.tableHeight);
        int x = left;
        int y = top;
        int rowVars = ROW_VARS[inputCount];
        int colVars = COL_VARS[inputCount];
        int rows = 1 << rowVars;
        int cols = 1 << colVars;
        g.setFont(HEAD_FONT);
        FontMetrics headFm = g.getFontMetrics();
        String rowHeader = this.header(0, rowVars);
        String colHeader = this.header(rowVars, rowVars + colVars);
        int xoffs = (this.tableWidth + this.headHeight + this.cellWidth - headFm.stringWidth(colHeader)) / 2;
        g.drawString(colHeader, x + xoffs, y + headFm.getAscent());
        int headerWidth = headFm.stringWidth(rowHeader);
        if (headerWidth <= this.headHeight) {
            int headX = x + (this.headHeight - headerWidth) / 2;
            int headY = y + (this.tableHeight + this.headHeight + this.cellHeight + headFm.getAscent()) / 2;
            g.drawString(rowHeader, headX, headY);
        } else if (g instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g.create();
            int yoffs = (this.tableHeight + this.headHeight + this.cellHeight + headerWidth) / 2;
            int headX = x + headFm.getAscent();
            int headY = y + yoffs;
            g2.rotate(-1.5707963267948966);
            g2.drawString(rowHeader, - headY, headX);
            g2.dispose();
        }
        x += this.headHeight;
        y += this.headHeight;
        g.setFont(BODY_FONT);
        FontMetrics fm = g.getFontMetrics();
        int dy = (this.cellHeight + fm.getAscent()) / 2;
        for (i = 0; i < cols; ++i) {
            String label = this.label(i, cols);
            g.drawString(label, x + (i + 1) * this.cellWidth + (this.cellWidth - fm.stringWidth(label)) / 2, y + dy);
        }
        for (i = 0; i < rows; ++i) {
            String label = this.label(i, rows);
            g.drawString(label, x + (this.cellWidth - fm.stringWidth(label)) / 2, y + (i + 1) * this.cellHeight + dy);
        }
        int outputColumn = table.getOutputIndex(this.output);
        x += this.cellWidth;
        y += this.cellHeight;
        g.setColor(ERROR_COLOR);
        for (int i2 = 0; i2 < rows; ++i2) {
            for (int j = 0; j < cols; ++j) {
                int row = this.getTableRow(i2, j, rows, cols);
                Entry entry = table.getOutputEntry(row, outputColumn);
                if (this.provisionalValue != null && row == this.provisionalY && outputColumn == this.provisionalX) {
                    entry = this.provisionalValue;
                }
                if (!entry.isError()) continue;
                g.fillRect(x + j * this.cellWidth, y + i2 * this.cellHeight, this.cellWidth, this.cellHeight);
            }
        }
        List implicants = this.model.getOutputExpressions().getMinimalImplicants(this.output);
        if (implicants != null) {
            int index = 0;
            for (Implicant imp : implicants) {
                g.setColor(IMP_COLORS[index % IMP_COLORS.length]);
                this.paintImplicant(g, imp, x, y, rows, cols);
                ++index;
            }
        }
        g.setColor(Color.GRAY);
        if (cols > 1 || inputCount == 0) {
            g.drawLine(x, y, left + this.tableWidth, y);
        }
        if (rows > 1 || inputCount == 0) {
            g.drawLine(x, y, x, top + this.tableHeight);
        }
        if (outputColumn < 0) {
            return;
        }
        g.setColor(Color.BLACK);
        for (int i3 = 0; i3 < rows; ++i3) {
            for (int j = 0; j < cols; ++j) {
                int row = this.getTableRow(i3, j, rows, cols);
                if (this.provisionalValue != null && row == this.provisionalY && outputColumn == this.provisionalX) {
                    String text = this.provisionalValue.getDescription();
                    g.setColor(Color.GREEN);
                    g.drawString(text, x + j * this.cellWidth + (this.cellWidth - fm.stringWidth(text)) / 2, y + i3 * this.cellHeight + dy);
                    g.setColor(Color.BLACK);
                    continue;
                }
                Entry entry = table.getOutputEntry(row, outputColumn);
                String text = entry.getDescription();
                g.drawString(text, x + j * this.cellWidth + (this.cellWidth - fm.stringWidth(text)) / 2, y + i3 * this.cellHeight + dy);
            }
        }
    }

    private void paintImplicant(Graphics g, Implicant imp, int x, int y, int rows, int cols) {
        int rowMax = -1;
        int rowMin = rows;
        int colMax = -1;
        int colMin = cols;
        boolean oneRowFound = false;
        int count = 0;
        Iterator it = imp.getTerms();
        while (it.hasNext()) {
            Implicant sq = (Implicant)it.next();
            int tableRow = sq.getRow();
            int row = this.getRow(tableRow, rows, cols);
            int col = this.getCol(tableRow, rows, cols);
            if (row == 1) {
                oneRowFound = true;
            }
            if (row > rowMax) {
                rowMax = row;
            }
            if (row < rowMin) {
                rowMin = row;
            }
            if (col > colMax) {
                colMax = col;
            }
            if (col < colMin) {
                colMin = col;
            }
            ++count;
        }
        int numCols = colMax - colMin + 1;
        int numRows = rowMax - rowMin + 1;
        int covered = numCols * numRows;
        int d = 10;
        if (covered == count) {
            g.fillRoundRect(x + colMin * this.cellWidth + 4, y + rowMin * this.cellHeight + 4, numCols * this.cellWidth - 8, numRows * this.cellHeight - 8, d, d);
        } else if (covered == 16) {
            int w;
            if (count == 4) {
                w = this.cellWidth - 4;
                int h = this.cellHeight - 4;
                int x1 = x + 3 * this.cellWidth + 4;
                int y1 = y + 3 * this.cellHeight + 4;
                g.fillRoundRect(x, y, w, h, d, d);
                g.fillRoundRect(x1, y, w, h, d, d);
                g.fillRoundRect(x, y1, w, h, d, d);
                g.fillRoundRect(x1, y1, w, h, d, d);
            } else if (oneRowFound) {
                w = this.cellWidth - 4;
                int h = 4 * this.cellHeight - 8;
                int x1 = x + 3 * this.cellWidth + 4;
                g.fillRoundRect(x, y + 4, w, h, d, d);
                g.fillRoundRect(x1, y + 4, w, h, d, d);
            } else {
                w = 4 * this.cellWidth - 8;
                int h = this.cellHeight - 4;
                int y1 = y + 3 * this.cellHeight + 4;
                g.fillRoundRect(x + 4, y, w, h, d, d);
                g.fillRoundRect(x + 4, y1, w, h, d, d);
            }
        } else if (numCols == 4) {
            int top = y + rowMin * this.cellHeight + 4;
            int w = this.cellWidth - 4;
            int h = numRows * this.cellHeight - 8;
            g.fillRoundRect(x, top, w, h, d, d);
            g.fillRoundRect(x + 3 * this.cellWidth + 4, top, w, h, d, d);
        } else {
            int left = x + colMin * this.cellWidth + 4;
            int w = numCols * this.cellWidth - 8;
            int h = this.cellHeight - 4;
            g.fillRoundRect(left, y, w, h, d, d);
            g.fillRoundRect(left, y + 3 * this.cellHeight + 4, w, h, d, d);
        }
    }

    private String header(int start, int stop) {
        if (start >= stop) {
            return "";
        }
        VariableList inputs = this.model.getInputs();
        StringBuffer ret = new StringBuffer(inputs.get(start));
        for (int i = start + 1; i < stop; ++i) {
            ret.append(", ");
            ret.append(inputs.get(i));
        }
        return ret.toString();
    }

    private String label(int row, int rows) {
        switch (rows) {
            case 2: {
                return "" + row;
            }
            case 4: {
                switch (row) {
                    case 0: {
                        return "00";
                    }
                    case 1: {
                        return "01";
                    }
                    case 2: {
                        return "11";
                    }
                    case 3: {
                        return "10";
                    }
                }
            }
        }
        return "";
    }

    private int getTableRow(int row, int col, int rows, int cols) {
        return this.toRow(row, rows) * cols + this.toRow(col, cols);
    }

    private int toRow(int row, int rows) {
        if (rows == 4) {
            switch (row) {
                case 2: {
                    return 3;
                }
                case 3: {
                    return 2;
                }
            }
            return row;
        }
        return row;
    }

    private int getRow(int tableRow, int rows, int cols) {
        int ret = tableRow / cols;
        switch (ret) {
            case 2: {
                return 3;
            }
            case 3: {
                return 2;
            }
        }
        return ret;
    }

    private int getCol(int tableRow, int rows, int cols) {
        int ret = tableRow % cols;
        switch (ret) {
            case 2: {
                return 3;
            }
            case 3: {
                return 2;
            }
        }
        return ret;
    }

    private int computeMargin(int compDim, int tableDim) {
        int ret = (compDim - tableDim) / 2;
        return ret >= 0 ? ret : Math.max(- this.headHeight, compDim - tableDim);
    }

    private class MyListener
    implements OutputExpressionsListener,
    TruthTableListener {
        private MyListener() {
        }

        @Override
        public void expressionChanged(OutputExpressionsEvent event) {
            if (event.getType() == 2 && event.getVariable().equals(KarnaughMapPanel.this.output)) {
                KarnaughMapPanel.this.repaint();
            }
        }

        @Override
        public void cellsChanged(TruthTableEvent event) {
            KarnaughMapPanel.this.repaint();
        }

        @Override
        public void structureChanged(TruthTableEvent event) {
            KarnaughMapPanel.this.computePreferredSize();
        }
    }

}

