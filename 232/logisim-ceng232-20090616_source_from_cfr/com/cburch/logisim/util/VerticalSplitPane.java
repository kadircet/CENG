/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import com.cburch.logisim.util.HorizontalSplitPane;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class VerticalSplitPane
extends JPanel {
    private JComponent comp0;
    private JComponent comp1;
    private MyDragbar dragbar;
    private double fraction;

    public VerticalSplitPane(JComponent comp0, JComponent comp1) {
        this(comp0, comp1, 0.5);
    }

    public VerticalSplitPane(JComponent comp0, JComponent comp1, double fraction) {
        this.comp0 = comp0;
        this.comp1 = comp1;
        this.dragbar = new MyDragbar();
        this.fraction = fraction;
        this.setLayout(new MyLayout());
        this.add(this.dragbar);
        this.add(comp0);
        this.add(comp1);
    }

    public double getFraction() {
        return this.fraction;
    }

    public void setFraction(double value) {
        if (value < 0.0) {
            value = 0.0;
        }
        if (value > 1.0) {
            value = 1.0;
        }
        if (this.fraction != value) {
            this.fraction = value;
            this.revalidate();
        }
    }

    private class MyDragbar
    extends HorizontalSplitPane.Dragbar {
        MyDragbar() {
            this.setCursor(Cursor.getPredefinedCursor(11));
        }

        @Override
        int getDragValue(MouseEvent e) {
            return this.getX() + e.getX() - VerticalSplitPane.this.getInsets().left;
        }

        @Override
        void setDragValue(int value) {
            Insets in = VerticalSplitPane.this.getInsets();
            VerticalSplitPane.this.setFraction((double)value / (double)(VerticalSplitPane.this.getWidth() - in.left - in.right));
            this.revalidate();
        }
    }

    private class MyLayout
    implements LayoutManager {
        private MyLayout() {
        }

        @Override
        public void addLayoutComponent(String name, Component comp) {
        }

        @Override
        public void removeLayoutComponent(Component comp) {
        }

        @Override
        public Dimension preferredLayoutSize(Container parent) {
            if (VerticalSplitPane.this.fraction <= 0.0) {
                return VerticalSplitPane.this.comp1.getPreferredSize();
            }
            if (VerticalSplitPane.this.fraction >= 1.0) {
                return VerticalSplitPane.this.comp0.getPreferredSize();
            }
            Insets in = parent.getInsets();
            Dimension d0 = VerticalSplitPane.this.comp0.getPreferredSize();
            Dimension d1 = VerticalSplitPane.this.comp1.getPreferredSize();
            return new Dimension(in.left + d0.width + d1.width + in.right, in.top + Math.max(d0.height, d1.height) + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            if (VerticalSplitPane.this.fraction <= 0.0) {
                return VerticalSplitPane.this.comp1.getMinimumSize();
            }
            if (VerticalSplitPane.this.fraction >= 1.0) {
                return VerticalSplitPane.this.comp0.getMinimumSize();
            }
            Insets in = parent.getInsets();
            Dimension d0 = VerticalSplitPane.this.comp0.getMinimumSize();
            Dimension d1 = VerticalSplitPane.this.comp1.getMinimumSize();
            return new Dimension(in.left + d0.width + d1.width + in.right, in.top + Math.max(d0.height, d1.height) + in.bottom);
        }

        @Override
        public void layoutContainer(Container parent) {
            int split;
            Insets in = parent.getInsets();
            int maxWidth = parent.getWidth() - (in.left + in.right);
            int maxHeight = parent.getHeight() - (in.top + in.bottom);
            if (VerticalSplitPane.this.fraction <= 0.0) {
                split = 0;
            } else if (VerticalSplitPane.this.fraction >= 1.0) {
                split = maxWidth;
            } else {
                split = (int)Math.round((double)maxWidth * VerticalSplitPane.this.fraction);
                split = Math.min(split, maxWidth - VerticalSplitPane.access$100((VerticalSplitPane)VerticalSplitPane.this).getMinimumSize().width);
                split = Math.max(split, VerticalSplitPane.access$200((VerticalSplitPane)VerticalSplitPane.this).getMinimumSize().width);
            }
            VerticalSplitPane.this.comp0.setBounds(in.left, in.top, split, maxHeight);
            VerticalSplitPane.this.comp1.setBounds(in.left + split, in.top, maxWidth - split, maxHeight);
            VerticalSplitPane.this.dragbar.setBounds(in.left + split - 3, in.top, 6, maxHeight);
        }
    }

}

