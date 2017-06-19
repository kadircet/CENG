/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.Caret;
import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.hex.Highlighter;
import com.cburch.hex.Measures;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.Scrollable;

public class HexEditor
extends JComponent
implements Scrollable {
    private HexModel model;
    private Listener listener;
    private Measures measures;
    private Caret caret;
    private Highlighter highlighter;

    public HexEditor(HexModel model) {
        this.model = model;
        this.listener = new Listener();
        this.measures = new Measures(this);
        this.caret = new Caret(this);
        this.highlighter = new Highlighter(this);
        this.setOpaque(true);
        this.setBackground(Color.WHITE);
        if (model != null) {
            model.addHexModelListener(this.listener);
        }
        this.measures.recompute();
    }

    Measures getMeasures() {
        return this.measures;
    }

    Highlighter getHighlighter() {
        return this.highlighter;
    }

    public HexModel getModel() {
        return this.model;
    }

    public Caret getCaret() {
        return this.caret;
    }

    public Object addHighlight(int start, int end, Color color) {
        return this.highlighter.add(start, end, color);
    }

    public void removeHighlight(Object tag) {
        this.highlighter.remove(tag);
    }

    public void setModel(HexModel value) {
        if (this.model == value) {
            return;
        }
        if (this.model != null) {
            this.model.removeHexModelListener(this.listener);
        }
        this.model = value;
        this.highlighter.clear();
        this.caret.setDot(-1, false);
        if (this.model != null) {
            this.model.addHexModelListener(this.listener);
        }
        this.measures.recompute();
    }

    public void scrollAddressToVisible(int start, int end) {
        if (start < 0 || end < 0) {
            return;
        }
        int x0 = this.measures.toX(start);
        int x1 = this.measures.toX(end) + this.measures.getCellWidth();
        int y0 = this.measures.toY(start);
        int y1 = this.measures.toY(end);
        int h = this.measures.getCellHeight();
        if (y0 == y1) {
            this.scrollRectToVisible(new Rectangle(x0, y0, x1 - x0, h));
        } else {
            this.scrollRectToVisible(new Rectangle(x0, y0, x1 - x0, y1 + h - y0));
        }
    }

    @Override
    public void setFont(Font value) {
        super.setFont(value);
        this.measures.recompute();
    }

    @Override
    public void setBounds(int x, int y, int width, int height) {
        super.setBounds(x, y, width, height);
        this.measures.widthChanged();
    }

    @Override
    protected void paintComponent(Graphics g) {
        this.measures.ensureComputed(g);
        Rectangle clip = g.getClipBounds();
        if (this.isOpaque()) {
            g.setColor(this.getBackground());
            g.fillRect(clip.x, clip.y, clip.width, clip.height);
        }
        long addr0 = this.model.getFirstOffset();
        long addr1 = this.model.getLastOffset();
        long xaddr0 = this.measures.toAddress(0, clip.y);
        if (xaddr0 == addr0) {
            xaddr0 = this.measures.getBaseAddress(this.model);
        }
        long xaddr1 = this.measures.toAddress(this.getWidth(), clip.y + clip.height) + 1;
        this.highlighter.paint(g, xaddr0, xaddr1);
        g.setColor(this.getForeground());
        Font baseFont = g.getFont();
        FontMetrics baseFm = g.getFontMetrics(baseFont);
        Font labelFont = baseFont.deriveFont(2);
        FontMetrics labelFm = g.getFontMetrics(labelFont);
        int cols = this.measures.getColumnCount();
        int baseX = this.measures.getBaseX();
        int baseY = this.measures.toY(xaddr0) + baseFm.getAscent() + baseFm.getLeading() / 2;
        int dy = this.measures.getCellHeight();
        int labelWidth = this.measures.getLabelWidth();
        int labelChars = this.measures.getLabelChars();
        int cellWidth = this.measures.getCellWidth();
        int cellChars = this.measures.getCellChars();
        long a = xaddr0;
        while (a < xaddr1) {
            String label = this.toHex(a, labelChars);
            g.setFont(labelFont);
            g.drawString(label, baseX - labelWidth + (labelWidth - labelFm.stringWidth(label)) / 2, baseY);
            g.setFont(baseFont);
            long b = a;
            int j = 0;
            while (j < cols) {
                if (b >= addr0 && b <= addr1) {
                    String val = this.toHex(this.model.get(b), cellChars);
                    int x = this.measures.toX(b) + (cellWidth - baseFm.stringWidth(val)) / 2;
                    g.drawString(val, x, baseY);
                }
                ++j;
                ++b;
            }
            a += (long)cols;
            baseY += dy;
        }
        this.caret.paintForeground(g, xaddr0, xaddr1);
    }

    private String toHex(long value, int chars) {
        String ret = Long.toHexString(value);
        int retLen = ret.length();
        if (retLen < chars) {
            ret = "0" + ret;
            for (int i = retLen + 1; i < chars; ++i) {
                ret = "0" + ret;
            }
            return ret;
        }
        if (retLen == chars) {
            return ret;
        }
        return ret.substring(retLen - chars);
    }

    public boolean selectionExists() {
        return this.caret.getMark() >= 0 && this.caret.getDot() >= 0;
    }

    public void selectAll() {
        this.caret.setDot(this.model.getLastOffset(), false);
        this.caret.setDot(0, true);
    }

    public void delete() {
        long p0 = this.caret.getMark();
        long p1 = this.caret.getDot();
        if (p0 < 0 || p1 < 0) {
            return;
        }
        if (p0 > p1) {
            long t = p0;
            p0 = p1;
            p1 = t;
        }
        this.model.fill(p0, p1 - p0 + 1, 0);
    }

    @Override
    public Dimension getPreferredScrollableViewportSize() {
        return this.getPreferredSize();
    }

    @Override
    public int getScrollableUnitIncrement(Rectangle vis, int orientation, int direction) {
        if (orientation == 1) {
            int ret = this.measures.getCellHeight();
            if (ret < 1) {
                this.measures.recompute();
                ret = this.measures.getCellHeight();
                if (ret < 1) {
                    return 1;
                }
            }
            return ret;
        }
        return Math.max(1, vis.width / 20);
    }

    @Override
    public int getScrollableBlockIncrement(Rectangle vis, int orientation, int direction) {
        if (orientation == 1) {
            int height = this.measures.getCellHeight();
            if (height < 1) {
                this.measures.recompute();
                height = this.measures.getCellHeight();
                if (height < 1) {
                    return 19 * vis.height / 20;
                }
            }
            int lines = Math.max(1, vis.height / height - 1);
            return lines * height;
        }
        return 19 * vis.width / 20;
    }

    @Override
    public boolean getScrollableTracksViewportWidth() {
        return true;
    }

    @Override
    public boolean getScrollableTracksViewportHeight() {
        return false;
    }

    private class Listener
    implements HexModelListener {
        private Listener() {
        }

        @Override
        public void metainfoChanged(HexModel source) {
            HexEditor.this.measures.recompute();
            HexEditor.this.repaint();
        }

        @Override
        public void bytesChanged(HexModel source, long start, long numBytes, int[] oldValues) {
            HexEditor.this.repaint(0, HexEditor.this.measures.toY(start), HexEditor.this.getWidth(), HexEditor.this.measures.toY(start + numBytes) + HexEditor.this.measures.getCellHeight());
        }
    }

}

