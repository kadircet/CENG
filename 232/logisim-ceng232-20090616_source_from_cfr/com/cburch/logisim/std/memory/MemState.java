/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.comp.ComponentState;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.std.memory.MemContents;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Graphics;

class MemState
implements ComponentState,
Cloneable,
HexModelListener {
    private static final int ROWS = 4;
    private static final int TABLE_WIDTH12 = 80;
    private static final int TABLE_WIDTH32 = 65;
    private static final int ENTRY_HEIGHT = 15;
    private static final int ENTRY_XOFFS12 = 40;
    private static final int ENTRY_XOFFS32 = 60;
    private static final int ENTRY_YOFFS = 5;
    private static final int ADDR_WIDTH_PER_CHAR = 10;
    private MemContents contents;
    private int columns;
    private long curScroll = 0;
    private long cursorLoc = -1;
    private long curAddr = -1;

    MemState(MemContents contents) {
        this.contents = contents;
        this.setBits(contents.getLogLength(), contents.getWidth());
        contents.addHexModelListener(this);
    }

    @Override
    public Object clone() {
        try {
            MemState ret = (MemState)super.clone();
            ret.contents = (MemContents)this.contents.clone();
            ret.contents.addHexModelListener(ret);
            return ret;
        }
        catch (CloneNotSupportedException e) {
            return null;
        }
    }

    private void setBits(int addrBits, int dataBits) {
        long maxScroll;
        if (this.contents == null) {
            this.contents = MemContents.create(addrBits, dataBits);
        } else {
            this.contents.setDimensions(addrBits, dataBits);
        }
        this.columns = addrBits <= 12 ? (dataBits <= 8 ? (dataBits <= 4 ? 8 : 4) : (dataBits <= 16 ? 2 : 1)) : (dataBits <= 8 ? 2 : 1);
        long newLast = this.contents.getLastOffset();
        if (this.cursorLoc > newLast) {
            this.cursorLoc = newLast;
        }
        if (this.curAddr - newLast > 0) {
            this.curAddr = -1;
        }
        if (this.curScroll > (maxScroll = Math.max(0, newLast + 1 - (long)(3 * this.columns)))) {
            this.curScroll = maxScroll;
        }
    }

    public MemContents getContents() {
        return this.contents;
    }

    int getAddrBits() {
        return this.contents.getLogLength();
    }

    int getDataBits() {
        return this.contents.getWidth();
    }

    long getLastAddress() {
        return (1 << this.contents.getLogLength()) - 1;
    }

    boolean isValidAddr(long addr) {
        int addrBits = this.contents.getLogLength();
        return addr >>> addrBits == 0;
    }

    int getRows() {
        return 4;
    }

    int getColumns() {
        return this.columns;
    }

    long getCursor() {
        return this.cursorLoc;
    }

    long getCurrent() {
        return this.curAddr;
    }

    long getScroll() {
        return this.curScroll;
    }

    void setCursor(long value) {
        this.cursorLoc = this.isValidAddr(value) ? value : -1;
    }

    void setCurrent(long value) {
        this.curAddr = this.isValidAddr(value) ? value : -1;
    }

    void scrollToShow(long addr) {
        if (this.isValidAddr(addr)) {
            long curTop = this.curScroll / (long)this.columns * (long)this.columns;
            if ((addr = addr / (long)this.columns * (long)this.columns) < curTop) {
                this.curScroll = addr;
            } else if (addr >= curTop + (long)(4 * this.columns)) {
                this.curScroll = addr - (long)(3 * this.columns);
                if (this.curScroll < 0) {
                    this.curScroll = 0;
                }
            }
        }
    }

    void setScroll(long addr) {
        long maxAddr = this.getLastAddress() - (long)(4 * this.columns);
        if (addr > maxAddr) {
            addr = maxAddr;
        }
        if (addr < 0) {
            addr = 0;
        }
        this.curScroll = addr;
    }

    public long getAddressAt(int x, int y) {
        int boxW;
        int addrBits = this.getAddrBits();
        int boxX = addrBits <= 12 ? 40 : 60;
        int n = boxW = addrBits <= 12 ? 80 : 65;
        if (x < boxX || x >= boxX + boxW || y <= 5 || y >= 65) {
            return -1;
        }
        int row = (y - 5) / 15;
        int col = (x - boxX) / (boxW / this.columns);
        long ret = this.curScroll / (long)this.columns * (long)this.columns + (long)(this.columns * row) + (long)col;
        return this.isValidAddr(ret) ? ret : this.getLastAddress();
    }

    public Bounds getBounds(long addr, Bounds bds) {
        int addrBits;
        int boxW;
        int boxX = bds.getX() + ((addrBits = this.getAddrBits()) <= 12 ? 40 : 60);
        int n = boxW = addrBits <= 12 ? 80 : 65;
        if (addr < 0) {
            int addrLen = (this.contents.getWidth() + 3) / 4;
            int width = 10 * addrLen;
            return Bounds.create(boxX - width, bds.getY() + 5, width, 15);
        }
        int bdsX = this.addrToX(bds, addr);
        int bdsY = this.addrToY(bds, addr);
        return Bounds.create(bdsX, bdsY, boxW / this.columns, 15);
    }

    public void paint(Graphics g, int leftX, int topY) {
        int addrBits = this.getAddrBits();
        int dataBits = this.contents.getWidth();
        int boxX = leftX + (addrBits <= 12 ? 40 : 60);
        int boxY = topY + 5;
        int boxW = addrBits <= 12 ? 80 : 65;
        int boxH = 60;
        GraphicsUtil.switchToWidth(g, 1);
        g.drawRect(boxX, boxY, boxW, boxH);
        int entryWidth = boxW / this.columns;
        for (int row = 0; row < 4; ++row) {
            long addr = this.curScroll / (long)this.columns * (long)this.columns + (long)(this.columns * row);
            int x = boxX;
            int y = boxY + 15 * row;
            int yoffs = 12;
            if (this.isValidAddr(addr)) {
                g.setColor(Color.GRAY);
                GraphicsUtil.drawText(g, StringUtil.toHexString(this.getAddrBits(), (int)addr), x - 2, y + yoffs, 1, 1);
            }
            g.setColor(Color.BLACK);
            for (int col = 0; col < this.columns && this.isValidAddr(addr); ++col) {
                int val = this.contents.get(addr);
                if (addr == this.curAddr) {
                    g.fillRect(x, y, entryWidth, 15);
                    g.setColor(Color.WHITE);
                    GraphicsUtil.drawText(g, StringUtil.toHexString(dataBits, val), x + entryWidth / 2, y + yoffs, 0, 1);
                    g.setColor(Color.BLACK);
                } else {
                    GraphicsUtil.drawText(g, StringUtil.toHexString(dataBits, val), x + entryWidth / 2, y + yoffs, 0, 1);
                }
                ++addr;
                x += entryWidth;
            }
        }
    }

    private int addrToX(Bounds bds, long addr) {
        int addrBits;
        int boxX = bds.getX() + ((addrBits = this.getAddrBits()) <= 12 ? 40 : 60);
        int boxW = addrBits <= 12 ? 80 : 65;
        long row = addr / (long)this.columns;
        long topRow = this.curScroll / (long)this.columns;
        if (row < topRow || row >= topRow + 4) {
            return -1;
        }
        int col = (int)(addr - row * (long)this.columns);
        if (col < 0 || col >= this.columns) {
            return -1;
        }
        return boxX + boxW * col / this.columns;
    }

    private int addrToY(Bounds bds, long addr) {
        long row = addr / (long)this.columns;
        long topRow = this.curScroll / (long)this.columns;
        if (row < topRow || row >= topRow + 4) {
            return -1;
        }
        return (int)((long)(bds.getY() + 5) + 15 * (row - topRow));
    }

    @Override
    public void metainfoChanged(HexModel source) {
        this.setBits(this.contents.getLogLength(), this.contents.getWidth());
    }

    @Override
    public void bytesChanged(HexModel source, long start, long numBytes, int[] oldValues) {
    }
}

