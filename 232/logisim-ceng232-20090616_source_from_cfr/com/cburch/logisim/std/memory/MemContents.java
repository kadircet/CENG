/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.hex.HexModel;
import com.cburch.hex.HexModelListener;
import com.cburch.logisim.std.memory.MemContentsSub;
import com.cburch.logisim.util.EventSourceWeakSupport;
import java.util.Arrays;
import java.util.Iterator;

class MemContents
implements Cloneable,
HexModel {
    private static final int PAGE_SIZE_BITS = 12;
    private static final int PAGE_SIZE = 4096;
    private static final int PAGE_MASK = 4095;
    private EventSourceWeakSupport listeners = new EventSourceWeakSupport();
    private int width;
    private int addrBits;
    private int mask;
    private MemContentsSub.ContentsInterface[] pages;

    static MemContents create(int addrBits, int width) {
        return new MemContents(addrBits, width);
    }

    private MemContents(int addrBits, int width) {
        this.setDimensions(addrBits, width);
    }

    @Override
    public void addHexModelListener(HexModelListener l) {
        if (this.listeners == null) {
            this.listeners = new EventSourceWeakSupport();
        }
        this.listeners.add(l);
    }

    @Override
    public void removeHexModelListener(HexModelListener l) {
        if (this.listeners == null) {
            return;
        }
        this.listeners.add(l);
        if (this.listeners.size() == 0) {
            this.listeners = null;
        }
    }

    private void fireMetainfoChanged() {
        if (this.listeners == null) {
            return;
        }
        Iterator it = this.listeners.iterator();
        if (!it.hasNext()) {
            this.listeners = null;
            return;
        }
        while (it.hasNext()) {
            HexModelListener l = (HexModelListener)it.next();
            l.metainfoChanged(this);
        }
    }

    private void fireBytesChanged(long start, long numBytes, int[] oldValues) {
        if (this.listeners == null) {
            return;
        }
        Iterator it = this.listeners.iterator();
        if (!it.hasNext()) {
            this.listeners = null;
            return;
        }
        while (it.hasNext()) {
            HexModelListener l = (HexModelListener)it.next();
            l.bytesChanged(this, start, numBytes, oldValues);
        }
    }

    public Object clone() {
        try {
            MemContents ret = (MemContents)super.clone();
            ret.listeners = new EventSourceWeakSupport();
            ret.pages = new MemContentsSub.ContentsInterface[this.pages.length];
            for (int i = 0; i < ret.pages.length; ++i) {
                if (this.pages[i] == null) continue;
                ret.pages[i] = (MemContentsSub.ContentsInterface)this.pages[i].clone();
            }
            return ret;
        }
        catch (CloneNotSupportedException ex) {
            return this;
        }
    }

    public int getLogLength() {
        return this.addrBits;
    }

    public int getWidth() {
        return this.width;
    }

    @Override
    public int get(long addr) {
        int page = (int)(addr >>> 12);
        int offs = (int)(addr & 4095);
        if (page < 0 || page >= this.pages.length || this.pages[page] == null) {
            return 0;
        }
        return this.pages[page].get(offs) & this.mask;
    }

    public boolean isClear() {
        for (int i = 0; i < this.pages.length; ++i) {
            MemContentsSub.ContentsInterface page = this.pages[i];
            if (page == null) continue;
            for (int j = page.getLength() - 1; j >= 0; --j) {
                if (page.get(j) == 0) continue;
                return false;
            }
        }
        return true;
    }

    @Override
    public void set(long addr, int value) {
        int val;
        int page = (int)(addr >>> 12);
        int offs = (int)(addr & 4095);
        int old = this.pages[page] == null ? 0 : this.pages[page].get(offs) & this.mask;
        if (old != (val = value & this.mask)) {
            if (this.pages[page] == null) {
                this.pages[page] = MemContentsSub.createContents(4096, this.width);
            }
            this.pages[page].set(offs, val);
            this.fireBytesChanged(addr, 1, new int[]{old});
        }
    }

    @Override
    public void set(long start, int[] values) {
        int pageStart = (int)(start >>> 12);
        int startOffs = (int)(start & 4095);
        int pageEnd = (int)(start + (long)values.length >>> 12);
        int endOffs = (int)(start + (long)values.length & 4095);
        if (pageStart == pageEnd) {
            this.ensurePage(pageStart);
            MemContentsSub.ContentsInterface page = this.pages[pageStart];
            if (!page.matches(values, startOffs, this.mask)) {
                int[] oldValues = page.get(startOffs, values.length);
                page.load(startOffs, values, this.mask);
                if (page.isClear()) {
                    this.pages[pageStart] = null;
                }
                this.fireBytesChanged(start, values.length, oldValues);
            }
        } else {
            int[] vals;
            if (startOffs == 0) {
                --pageStart;
                startOffs = 4096;
            } else {
                this.ensurePage(pageStart);
                vals = new int[4096 - startOffs];
                System.arraycopy(values, 0, vals, 0, vals.length);
                MemContentsSub.ContentsInterface page = this.pages[pageStart];
                if (!page.matches(vals, startOffs, this.mask)) {
                    int[] oldValues = page.get(startOffs, vals.length);
                    page.load(startOffs, vals, this.mask);
                    if (page.isClear()) {
                        this.pages[pageStart] = null;
                    }
                    this.fireBytesChanged(start, 4096 - pageStart, oldValues);
                }
            }
            vals = new int[4096];
            int offs = 4096 - startOffs;
            int i = pageStart + 1;
            while (i < pageEnd) {
                MemContentsSub.ContentsInterface page = this.pages[i];
                if (page == null) {
                    boolean allZeroes = true;
                    for (int j = 0; j < 4096; ++j) {
                        if ((values[j] & this.mask) != 0) continue;
                        allZeroes = false;
                        break;
                    }
                    if (!allZeroes) {
                        this.pages[i] = page = MemContentsSub.createContents(4096, this.width);
                    }
                }
                if (page != null) {
                    System.arraycopy(values, offs, vals, 0, 4096);
                    if (!page.matches(vals, startOffs, this.mask)) {
                        int[] oldValues = page.get(0, 4096);
                        page.load(0, vals, this.mask);
                        if (page.isClear()) {
                            this.pages[i] = null;
                        }
                        this.fireBytesChanged((long)i << 12, 4096, oldValues);
                    }
                }
                ++i;
                offs += 4096;
            }
            if (endOffs > 0) {
                this.ensurePage(pageEnd);
                vals = new int[endOffs];
                System.arraycopy(values, offs, vals, 0, endOffs);
                MemContentsSub.ContentsInterface page = this.pages[pageEnd];
                if (!page.matches(vals, startOffs, this.mask)) {
                    int[] oldValues = page.get(0, endOffs);
                    page.load(0, vals, this.mask);
                    if (page.isClear()) {
                        this.pages[pageEnd] = null;
                    }
                    this.fireBytesChanged((long)pageEnd << 12, endOffs, oldValues);
                }
            }
        }
    }

    @Override
    public void fill(long start, long len, int value) {
        if (len == 0) {
            return;
        }
        int pageStart = (int)(start >>> 12);
        int startOffs = (int)(start & 4095);
        int pageEnd = (int)(start + len >>> 12);
        int endOffs = (int)(start + len & 4095);
        value &= this.mask;
        if (pageStart == pageEnd) {
            this.ensurePage(pageStart);
            int[] vals = new int[(int)len];
            Arrays.fill(vals, value);
            MemContentsSub.ContentsInterface page = this.pages[pageStart];
            if (!page.matches(vals, startOffs, this.mask)) {
                int[] oldValues = page.get(startOffs, (int)len);
                page.load(startOffs, vals, this.mask);
                if (value == 0 && page.isClear()) {
                    this.pages[pageStart] = null;
                }
                this.fireBytesChanged(start, len, oldValues);
            }
        } else {
            int[] vals;
            int[] oldValues;
            if (startOffs == 0) {
                --pageStart;
                startOffs = 4096;
            } else if (value != 0 || this.pages[pageStart] != null) {
                this.ensurePage(pageStart);
                vals = new int[4096 - startOffs];
                Arrays.fill(vals, value);
                MemContentsSub.ContentsInterface page = this.pages[pageStart];
                if (!page.matches(vals, startOffs, this.mask)) {
                    oldValues = page.get(startOffs, vals.length);
                    page.load(startOffs, vals, this.mask);
                    if (value == 0 && page.isClear()) {
                        this.pages[pageStart] = null;
                    }
                    this.fireBytesChanged(start, 4096 - pageStart, oldValues);
                }
            }
            if (value == 0) {
                for (int i = pageStart + 1; i < pageEnd; ++i) {
                    if (this.pages[i] == null) continue;
                    this.clearPage(i);
                }
            } else {
                vals = new int[4096];
                Arrays.fill(vals, value);
                for (int i = pageStart + 1; i < pageEnd; ++i) {
                    this.ensurePage(i);
                    MemContentsSub.ContentsInterface page = this.pages[i];
                    if (page.matches(vals, startOffs, this.mask)) continue;
                    int[] oldValues2 = page.get(0, 4096);
                    page.load(0, vals, this.mask);
                    this.fireBytesChanged((long)i << 12, 4096, oldValues2);
                }
            }
            if (endOffs > 0) {
                MemContentsSub.ContentsInterface page = this.pages[pageEnd];
                if (value != 0 || page != null) {
                    this.ensurePage(pageEnd);
                    int[] vals2 = new int[endOffs];
                    Arrays.fill(vals2, value);
                    if (!page.matches(vals2, startOffs, this.mask)) {
                        oldValues = page.get(0, endOffs);
                        page.load(0, vals2, this.mask);
                        if (value == 0 && page.isClear()) {
                            this.pages[pageEnd] = null;
                        }
                        this.fireBytesChanged((long)pageEnd << 12, endOffs, oldValues);
                    }
                }
            }
        }
    }

    public void clear() {
        for (int i = 0; i < this.pages.length; ++i) {
            if (this.pages[i] == null || this.pages[i] == null) continue;
            this.clearPage(i);
        }
    }

    private void clearPage(int index) {
        MemContentsSub.ContentsInterface page = this.pages[index];
        int[] oldValues = new int[page.getLength()];
        boolean changed = false;
        for (int j = 0; j < oldValues.length; ++j) {
            int val;
            oldValues[j] = val = page.get(j) & this.mask;
            if (val == 0) continue;
            changed = true;
        }
        if (changed) {
            this.pages[index] = null;
            this.fireBytesChanged(index << 12, oldValues.length, oldValues);
        }
    }

    public void setDimensions(int addrBits, int width) {
        int pageLength;
        int pageCount;
        if (addrBits == this.addrBits && width == this.width) {
            return;
        }
        this.addrBits = addrBits;
        this.width = width;
        this.mask = width == 32 ? -1 : (1 << width) - 1;
        MemContentsSub.ContentsInterface[] oldPages = this.pages;
        if (addrBits < 12) {
            pageCount = 1;
            pageLength = 1 << addrBits;
        } else {
            pageCount = 1 << addrBits - 12;
            pageLength = 4096;
        }
        this.pages = new MemContentsSub.ContentsInterface[pageCount];
        if (oldPages != null) {
            int n = Math.min(oldPages.length, this.pages.length);
            for (int i = 0; i < n; ++i) {
                if (oldPages[i] == null) continue;
                this.pages[i] = MemContentsSub.createContents(pageLength, width);
                int m = Math.max(oldPages[i].getLength(), pageLength);
                for (int j = 0; j < m; ++j) {
                    this.pages[i].set(j, oldPages[i].get(j));
                }
            }
        }
        if (pageCount == 0 && this.pages[0] == null) {
            this.pages[0] = MemContentsSub.createContents(pageLength, width);
        }
        this.fireMetainfoChanged();
    }

    @Override
    public long getFirstOffset() {
        return 0;
    }

    @Override
    public long getLastOffset() {
        return (1 << this.addrBits) - 1;
    }

    @Override
    public int getValueWidth() {
        return this.width;
    }

    private void ensurePage(int index) {
        if (this.pages[index] == null) {
            this.pages[index] = MemContentsSub.createContents(4096, this.width);
        }
    }
}

