/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

class Measures {
    private HexEditor hex;
    private int headerChars;
    private int cellChars;
    private int headerWidth;
    private int spacerWidth;
    private int cellWidth;
    private int cellHeight;
    private int cols;
    private int baseX;
    private boolean guessed;

    public Measures(HexEditor hex) {
        this.hex = hex;
        this.guessed = true;
        this.cols = 1;
        this.cellWidth = -1;
        this.cellHeight = -1;
        this.cellChars = 2;
        this.headerChars = 4;
        this.computeCellSize(null);
    }

    public int getColumnCount() {
        return this.cols;
    }

    public int getBaseX() {
        return this.baseX;
    }

    public int getCellHeight() {
        return this.cellHeight;
    }

    public int getCellWidth() {
        return this.cellWidth;
    }

    public int getLabelWidth() {
        return this.headerWidth;
    }

    public int getLabelChars() {
        return this.headerChars;
    }

    public int getCellChars() {
        return this.cellChars;
    }

    public int getValuesX() {
        return this.baseX + this.spacerWidth;
    }

    public int getValuesWidth() {
        return (this.cols - 1) / 4 * this.spacerWidth + this.cols * this.cellWidth;
    }

    public long getBaseAddress(HexModel model) {
        if (model == null) {
            return 0;
        }
        long addr0 = model.getFirstOffset();
        return addr0 - addr0 % (long)this.cols;
    }

    public int toY(long addr) {
        long row = (addr - this.getBaseAddress(this.hex.getModel())) / (long)this.cols;
        long ret = row * (long)this.cellHeight;
        return ret < Integer.MAX_VALUE ? (int)ret : Integer.MAX_VALUE;
    }

    public int toX(long addr) {
        int col = (int)(addr % (long)this.cols);
        return this.baseX + (1 + col / 4) * this.spacerWidth + col * this.cellWidth;
    }

    public long toAddress(int x, int y) {
        long ret;
        HexModel model = this.hex.getModel();
        if (model == null) {
            return Integer.MIN_VALUE;
        }
        long addr0 = model.getFirstOffset();
        long addr1 = model.getLastOffset();
        long base = this.getBaseAddress(model) + (long)y / (long)this.cellHeight * (long)this.cols;
        int offs = (x - this.baseX) / (this.cellWidth + (this.spacerWidth + 2) / 4);
        if (offs < 0) {
            offs = 0;
        }
        if (offs >= this.cols) {
            offs = this.cols - 1;
        }
        if ((ret = base + (long)offs) > addr1) {
            ret = addr1;
        }
        if (ret < addr0) {
            ret = addr0;
        }
        return ret;
    }

    void ensureComputed(Graphics g) {
        if (this.guessed || this.cellWidth < 0) {
            this.computeCellSize(g);
        }
    }

    void recompute() {
        this.computeCellSize(this.hex.getGraphics());
    }

    void widthChanged() {
        int width;
        int oldCols = this.cols;
        if (this.guessed || this.cellWidth < 0) {
            this.cols = 16;
            width = this.hex.getPreferredSize().width;
        } else {
            width = this.hex.getWidth();
            int ret = (width - this.headerWidth) / (this.cellWidth + (this.spacerWidth + 3) / 4);
            this.cols = ret >= 16 ? 16 : (ret >= 8 ? 8 : 4);
        }
        int lineWidth = this.headerWidth + this.cols * this.cellWidth + (this.cols / 4 - 1) * this.spacerWidth;
        int newBase = this.headerWidth + Math.max(0, (width - lineWidth) / 2);
        if (this.baseX != newBase) {
            this.baseX = newBase;
            this.hex.repaint();
        }
        if (this.cols != oldCols) {
            this.recompute();
        }
    }

    private void computeCellSize(Graphics g) {
        int spaceWidth;
        int lineHeight;
        int charWidth;
        FontMetrics fm;
        long height;
        HexModel model = this.hex.getModel();
        if (model == null) {
            this.headerChars = 4;
            this.cellChars = 2;
        } else {
            int logSize = 0;
            long addrEnd = model.getLastOffset();
            while (addrEnd > 1 << logSize) {
                ++logSize;
            }
            this.headerChars = (logSize + 3) / 4;
            this.cellChars = (model.getValueWidth() + 3) / 4;
        }
        FontMetrics fontMetrics = fm = g == null ? null : g.getFontMetrics(this.hex.getFont());
        if (fm == null) {
            charWidth = 8;
            spaceWidth = 6;
            Font font = this.hex.getFont();
            lineHeight = font == null ? 16 : font.getSize();
        } else {
            this.guessed = false;
            charWidth = 0;
            for (int i = 0; i < 16; ++i) {
                int width = fm.stringWidth(Integer.toHexString(i));
                if (width <= charWidth) continue;
                charWidth = width;
            }
            spaceWidth = fm.stringWidth(" ");
            lineHeight = fm.getHeight();
        }
        this.headerWidth = this.headerChars * charWidth + spaceWidth;
        this.spacerWidth = spaceWidth;
        this.cellWidth = this.cellChars * charWidth + spaceWidth;
        this.cellHeight = lineHeight;
        int width = this.headerWidth + this.cols * this.cellWidth + this.cols / 4 * this.spacerWidth;
        if (model == null) {
            height = 16 * this.cellHeight;
        } else {
            long addr0 = this.getBaseAddress(model);
            long addr1 = model.getLastOffset();
            long rows = (int)((addr1 - addr0 + 1 + (long)this.cols - 1) / (long)this.cols);
            height = rows * (long)this.cellHeight;
            if (height > Integer.MAX_VALUE) {
                height = Integer.MAX_VALUE;
            }
        }
        Dimension pref = this.hex.getPreferredSize();
        if (pref.width != width || (long)pref.height != height) {
            pref.width = width;
            pref.height = (int)height;
            this.hex.setPreferredSize(pref);
            this.hex.revalidate();
        }
        this.widthChanged();
    }
}

