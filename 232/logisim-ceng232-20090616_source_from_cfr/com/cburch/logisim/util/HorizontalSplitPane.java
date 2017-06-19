/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Insets;
import java.awt.LayoutManager;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import javax.swing.JComponent;
import javax.swing.JPanel;

public class HorizontalSplitPane
extends JPanel {
    static final int DRAG_TOLERANCE = 3;
    private static final Color DRAG_COLOR = new Color(0, 0, 0, 128);
    private JComponent comp0;
    private JComponent comp1;
    private MyDragbar dragbar;
    private double fraction;

    public HorizontalSplitPane(JComponent comp0, JComponent comp1) {
        this(comp0, comp1, 0.5);
    }

    public HorizontalSplitPane(JComponent comp0, JComponent comp1, double fraction) {
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
    extends Dragbar {
        MyDragbar() {
            this.setCursor(Cursor.getPredefinedCursor(9));
        }

        @Override
        int getDragValue(MouseEvent e) {
            return this.getY() + e.getY() - HorizontalSplitPane.this.getInsets().top;
        }

        @Override
        void setDragValue(int value) {
            Insets in = HorizontalSplitPane.this.getInsets();
            HorizontalSplitPane.this.setFraction((double)value / (double)(HorizontalSplitPane.this.getHeight() - in.bottom - in.top));
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
            if (HorizontalSplitPane.this.fraction <= 0.0) {
                return HorizontalSplitPane.this.comp1.getPreferredSize();
            }
            if (HorizontalSplitPane.this.fraction >= 1.0) {
                return HorizontalSplitPane.this.comp0.getPreferredSize();
            }
            Insets in = parent.getInsets();
            Dimension d0 = HorizontalSplitPane.this.comp0.getPreferredSize();
            Dimension d1 = HorizontalSplitPane.this.comp1.getPreferredSize();
            return new Dimension(in.left + Math.max(d0.width, d1.width) + in.right, in.top + d0.height + d1.height + in.bottom);
        }

        @Override
        public Dimension minimumLayoutSize(Container parent) {
            if (HorizontalSplitPane.this.fraction <= 0.0) {
                return HorizontalSplitPane.this.comp1.getMinimumSize();
            }
            if (HorizontalSplitPane.this.fraction >= 1.0) {
                return HorizontalSplitPane.this.comp0.getMinimumSize();
            }
            Insets in = parent.getInsets();
            Dimension d0 = HorizontalSplitPane.this.comp0.getMinimumSize();
            Dimension d1 = HorizontalSplitPane.this.comp1.getMinimumSize();
            return new Dimension(in.left + Math.max(d0.width, d1.width) + in.right, in.top + d0.height + d1.height + in.bottom);
        }

        @Override
        public void layoutContainer(Container parent) {
            int split;
            Insets in = parent.getInsets();
            int maxWidth = parent.getWidth() - (in.left + in.right);
            int maxHeight = parent.getHeight() - (in.top + in.bottom);
            if (HorizontalSplitPane.this.fraction <= 0.0) {
                split = 0;
            } else if (HorizontalSplitPane.this.fraction >= 1.0) {
                split = maxWidth;
            } else {
                split = (int)Math.round((double)maxHeight * HorizontalSplitPane.this.fraction);
                split = Math.min(split, maxHeight - HorizontalSplitPane.access$200((HorizontalSplitPane)HorizontalSplitPane.this).getMinimumSize().height);
                split = Math.max(split, HorizontalSplitPane.access$300((HorizontalSplitPane)HorizontalSplitPane.this).getMinimumSize().height);
            }
            HorizontalSplitPane.this.comp0.setBounds(in.left, in.top, maxWidth, split);
            HorizontalSplitPane.this.comp1.setBounds(in.left, in.top + split, maxWidth, maxHeight - split);
            HorizontalSplitPane.this.dragbar.setBounds(in.left, in.top + split - 3, maxWidth, 6);
        }
    }

    static abstract class Dragbar
    extends JComponent
    implements MouseListener,
    MouseMotionListener {
        private boolean dragging = false;
        private int curValue;

        Dragbar() {
            this.addMouseListener(this);
            this.addMouseMotionListener(this);
        }

        abstract int getDragValue(MouseEvent var1);

        abstract void setDragValue(int var1);

        @Override
        public void paintComponent(Graphics g) {
            if (this.dragging) {
                g.setColor(DRAG_COLOR);
                g.fillRect(0, 0, this.getWidth(), this.getHeight());
            }
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            if (!this.dragging) {
                this.curValue = this.getDragValue(e);
                this.dragging = true;
                this.repaint();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (this.dragging) {
                this.dragging = false;
                int newValue = this.getDragValue(e);
                if (newValue != this.curValue) {
                    this.setDragValue(newValue);
                }
                this.repaint();
            }
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            int newValue;
            if (this.dragging && (newValue = this.getDragValue(e)) != this.curValue) {
                this.setDragValue(newValue);
            }
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }
    }

}

