/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.TableConstraints;
import java.awt.Component;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.LayoutManager2;
import java.util.ArrayList;

public class TableLayout
implements LayoutManager2 {
    private int colCount;
    private ArrayList contents;
    private int curRow;
    private int curCol;
    private Dimension prefs;
    private int[] prefRow;
    private int[] prefCol;
    private double[] rowWeight;

    public TableLayout(int colCount) {
        this.colCount = colCount;
        this.contents = new ArrayList();
        this.curRow = 0;
        this.curCol = 0;
    }

    public void setRowWeight(int rowIndex, double weight) {
        if (weight < 0.0) {
            throw new IllegalArgumentException("weight must be nonnegative");
        }
        if (rowIndex < 0) {
            throw new IllegalArgumentException("row index must be nonnegative");
        }
        if ((this.rowWeight == null || rowIndex >= this.rowWeight.length) && weight != 0.0) {
            double[] a = new double[rowIndex + 10];
            if (this.rowWeight != null) {
                System.arraycopy(this.rowWeight, 0, a, 0, this.rowWeight.length);
            }
            this.rowWeight = a;
        }
        this.rowWeight[rowIndex] = weight;
    }

    @Override
    public void addLayoutComponent(String name, Component comp) {
        while (this.curRow >= this.contents.size()) {
            this.contents.add(new Component[this.colCount]);
        }
        Component[] rowContents = (Component[])this.contents.get(this.curRow);
        rowContents[this.curCol] = comp;
        ++this.curCol;
        if (this.curCol == this.colCount) {
            ++this.curRow;
            this.curCol = 0;
        }
        this.prefs = null;
    }

    @Override
    public void addLayoutComponent(Component comp, Object constraints) {
        if (constraints instanceof TableConstraints) {
            TableConstraints con = (TableConstraints)constraints;
            if (con.getRow() >= 0) {
                this.curRow = con.getRow();
            }
            if (con.getCol() >= 0) {
                this.curCol = con.getCol();
            }
        }
        this.addLayoutComponent("", comp);
    }

    @Override
    public void removeLayoutComponent(Component comp) {
        int n = this.contents.size();
        for (int i = 0; i < n; ++i) {
            Component[] row = (Component[])this.contents.get(i);
            for (int j = 0; j < row.length; ++j) {
                if (row[j] != comp) continue;
                row[j] = null;
                return;
            }
        }
        this.prefs = null;
    }

    @Override
    public Dimension preferredLayoutSize(Container parent) {
        if (this.prefs == null) {
            int[] prefCol = new int[this.colCount];
            int[] prefRow = new int[this.contents.size()];
            int height = 0;
            for (int i = 0; i < prefRow.length; ++i) {
                Component[] row = (Component[])this.contents.get(i);
                int rowHeight = 0;
                for (int j = 0; j < row.length; ++j) {
                    if (row[j] == null) continue;
                    Dimension dim = row[j].getPreferredSize();
                    if (dim.height > rowHeight) {
                        rowHeight = dim.height;
                    }
                    if (dim.width <= prefCol[j]) continue;
                    prefCol[j] = dim.width;
                }
                prefRow[i] = rowHeight;
                height += rowHeight;
            }
            int width = 0;
            for (int i2 = 0; i2 < prefCol.length; ++i2) {
                width += prefCol[i2];
            }
            this.prefs = new Dimension(width, height);
            this.prefRow = prefRow;
            this.prefCol = prefCol;
        }
        return new Dimension(this.prefs);
    }

    @Override
    public Dimension minimumLayoutSize(Container parent) {
        return this.preferredLayoutSize(parent);
    }

    @Override
    public Dimension maximumLayoutSize(Container parent) {
        return new Dimension(Integer.MAX_VALUE, Integer.MAX_VALUE);
    }

    @Override
    public float getLayoutAlignmentX(Container parent) {
        return 0.5f;
    }

    @Override
    public float getLayoutAlignmentY(Container parent) {
        return 0.5f;
    }

    @Override
    public void layoutContainer(Container parent) {
        Dimension pref = this.preferredLayoutSize(parent);
        int[] prefRow = this.prefRow;
        int[] prefCol = this.prefCol;
        Dimension size = parent.getSize();
        int yRemaining = size.height - pref.height;
        double rowWeightTotal = 0.0;
        if (yRemaining != 0 && this.rowWeight != null) {
            for (int i = 0; i < this.rowWeight.length; ++i) {
                rowWeightTotal += this.rowWeight[i];
            }
        }
        double y0 = rowWeightTotal == 0.0 && yRemaining > 0 ? (double)yRemaining / 2.0 : 0.0;
        int x0 = (size.width - pref.width) / 2;
        if (x0 < 0) {
            x0 = 0;
        }
        double y = y0;
        int n = this.contents.size();
        for (int i = 0; i < n; ++i) {
            Component[] row = (Component[])this.contents.get(i);
            int yRound = (int)(y + 0.5);
            int x = x0;
            for (int j = 0; j < row.length; ++j) {
                Component comp = row[j];
                if (comp != null) {
                    row[j].setBounds(x, yRound, prefCol[j], prefRow[i]);
                }
                x += prefCol[j];
            }
            y += (double)prefRow[i];
            if (rowWeightTotal <= 0.0 || i >= this.rowWeight.length) continue;
            y += (double)yRemaining * this.rowWeight[i] / rowWeightTotal;
        }
    }

    @Override
    public void invalidateLayout(Container parent) {
        this.prefs = null;
    }
}

