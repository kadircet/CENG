/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import com.cburch.hex.Measures;
import java.awt.Color;
import java.awt.Graphics;
import java.util.ArrayList;

class Highlighter {
    private HexEditor hex;
    private ArrayList entries;

    Highlighter(HexEditor hex) {
        this.hex = hex;
        this.entries = new ArrayList();
    }

    public synchronized Object add(long start, long end, Color color) {
        HexModel model = this.hex.getModel();
        if (model == null) {
            return null;
        }
        if (start > end) {
            long t = start;
            start = end;
            end = t;
        }
        if (start < model.getFirstOffset()) {
            start = model.getFirstOffset();
        }
        if (end > model.getLastOffset()) {
            end = model.getLastOffset();
        }
        if (start >= end) {
            return null;
        }
        Entry entry = new Entry(start, end, color);
        this.entries.add(entry);
        this.expose(entry);
        return entry;
    }

    public synchronized void remove(Object tag) {
        if (this.entries.remove(tag)) {
            Entry entry = (Entry)tag;
            this.expose(entry);
        }
    }

    public synchronized void clear() {
        ArrayList oldEntries = this.entries;
        this.entries = new ArrayList();
        for (int n = oldEntries.size(); n >= 0; --n) {
            this.expose((Entry)oldEntries.get(n));
        }
    }

    private void expose(Entry entry) {
        Measures m = this.hex.getMeasures();
        int y0 = m.toY(entry.start);
        int y1 = m.toY(entry.end);
        int h = m.getCellHeight();
        int cellWidth = m.getCellWidth();
        if (y0 == y1) {
            int x0 = m.toX(entry.start);
            int x1 = m.toX(entry.end) + cellWidth;
            this.hex.repaint(x0, y0, x1 - x0, h);
        } else {
            int lineStart = m.getValuesX();
            int lineWidth = m.getValuesWidth();
            this.hex.repaint(lineStart, y0, lineWidth, y1 - y0 + h);
        }
    }

    synchronized void paint(Graphics g, long start, long end) {
        int size = this.entries.size();
        if (size == 0) {
            return;
        }
        Measures m = this.hex.getMeasures();
        int lineStart = m.getValuesX();
        int lineWidth = m.getValuesWidth();
        int cellWidth = m.getCellWidth();
        int cellHeight = m.getCellHeight();
        for (int i = size - 1; i >= 0; --i) {
            Entry e = (Entry)this.entries.get(i);
            if (e.start > end || e.end < start) continue;
            int y0 = m.toY(e.start);
            int y1 = m.toY(e.end);
            int x0 = m.toX(e.start);
            int x1 = m.toX(e.end);
            g.setColor(e.color);
            if (y0 == y1) {
                g.fillRect(x0, y0, x1 - x0 + cellWidth, cellHeight);
                continue;
            }
            int midHeight = y1 - (y0 + cellHeight);
            g.fillRect(x0, y0, lineStart + lineWidth - x0, cellHeight);
            if (midHeight > 0) {
                g.fillRect(lineStart, y0 + cellHeight, lineWidth, midHeight);
            }
            g.fillRect(lineStart, y1, x1 + cellWidth - lineStart, cellHeight);
        }
    }

    private static class Entry {
        private long start;
        private long end;
        private Color color;

        Entry(long start, long end, Color color) {
            this.start = start;
            this.end = end;
            this.color = color;
        }
    }

}

