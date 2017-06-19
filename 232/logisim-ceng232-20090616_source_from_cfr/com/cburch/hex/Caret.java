/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.hex;

import com.cburch.hex.HexEditor;
import com.cburch.hex.HexModel;
import com.cburch.hex.Highlighter;
import com.cburch.hex.Measures;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.ArrayList;
import javax.swing.AbstractAction;
import javax.swing.Action;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.KeyStroke;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

public class Caret {
    private static Color SELECT_COLOR = new Color(192, 192, 255);
    private static final Stroke CURSOR_STROKE = new BasicStroke(2.0f);
    private HexEditor hex;
    private ArrayList listeners;
    private long mark;
    private long cursor;
    private Object highlight;

    Caret(HexEditor hex) {
        this.hex = hex;
        this.listeners = new ArrayList();
        this.cursor = -1;
        Listener l = new Listener();
        hex.addMouseListener(l);
        hex.addMouseMotionListener(l);
        hex.addKeyListener(l);
        hex.addFocusListener(l);
        InputMap imap = hex.getInputMap();
        ActionMap amap = hex.getActionMap();
        AbstractAction nullAction = new AbstractAction(){

            @Override
            public void actionPerformed(ActionEvent e) {
            }
        };
        String nullKey = "null";
        amap.put(nullKey, nullAction);
        imap.put(KeyStroke.getKeyStroke(40, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(38, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(37, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(39, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(34, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(33, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(36, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(35, 0), nullKey);
        imap.put(KeyStroke.getKeyStroke(10, 0), nullKey);
    }

    public void addChangeListener(ChangeListener l) {
        this.listeners.add(l);
    }

    public void removeChangeListener(ChangeListener l) {
        this.listeners.remove(l);
    }

    public long getMark() {
        return this.mark;
    }

    public long getDot() {
        return this.cursor;
    }

    public void setDot(long value, boolean keepMark) {
        HexModel model = this.hex.getModel();
        if (model == null || value < model.getFirstOffset() || value > model.getLastOffset()) {
            value = -1;
        }
        if (this.cursor != value) {
            long oldValue = this.cursor;
            if (this.highlight != null) {
                this.hex.getHighlighter().remove(this.highlight);
                this.highlight = null;
            }
            if (!keepMark) {
                this.mark = value;
            } else if (this.mark != value) {
                this.highlight = this.hex.getHighlighter().add(this.mark, value, SELECT_COLOR);
            }
            this.cursor = value;
            this.expose(oldValue, false);
            this.expose(value, true);
            if (!this.listeners.isEmpty()) {
                ChangeEvent event = new ChangeEvent(this);
                for (int i = this.listeners.size() - 1; i >= 0; --i) {
                    ((ChangeListener)this.listeners.get(i)).stateChanged(event);
                }
            }
        }
    }

    private void expose(long loc, boolean scrollTo) {
        if (loc >= 0) {
            Measures measures = this.hex.getMeasures();
            int x = measures.toX(loc);
            int y = measures.toY(loc);
            int w = measures.getCellWidth();
            int h = measures.getCellHeight();
            this.hex.repaint(x - 1, y - 1, w + 2, h + 2);
            if (scrollTo) {
                this.hex.scrollRectToVisible(new Rectangle(x, y, w, h));
            }
        }
    }

    void paintForeground(Graphics g, long start, long end) {
        if (this.cursor >= start && this.cursor < end && this.hex.isFocusOwner()) {
            Measures measures = this.hex.getMeasures();
            int x = measures.toX(this.cursor);
            int y = measures.toY(this.cursor);
            Graphics2D g2 = (Graphics2D)g;
            Stroke oldStroke = g2.getStroke();
            g2.setColor(this.hex.getForeground());
            g2.setStroke(CURSOR_STROKE);
            g2.drawRect(x, y, measures.getCellWidth() - 1, measures.getCellHeight() - 1);
            g2.setStroke(oldStroke);
        }
    }

    private class Listener
    implements MouseListener,
    MouseMotionListener,
    KeyListener,
    FocusListener {
        private Listener() {
        }

        @Override
        public void mouseClicked(MouseEvent e) {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            Measures measures = Caret.this.hex.getMeasures();
            long loc = measures.toAddress(e.getX(), e.getY());
            Caret.this.setDot(loc, (e.getModifiers() & 1) != 0);
            if (!Caret.this.hex.isFocusOwner()) {
                Caret.this.hex.requestFocus();
            }
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            this.mouseDragged(e);
        }

        @Override
        public void mouseEntered(MouseEvent e) {
        }

        @Override
        public void mouseExited(MouseEvent e) {
        }

        @Override
        public void mouseDragged(MouseEvent e) {
            Measures measures = Caret.this.hex.getMeasures();
            long loc = measures.toAddress(e.getX(), e.getY());
            Caret.this.setDot(loc, true);
        }

        @Override
        public void mouseMoved(MouseEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
            int mask = e.getModifiers();
            if ((mask & -2) != 0) {
                return;
            }
            char c = e.getKeyChar();
            int cols = Caret.this.hex.getMeasures().getColumnCount();
            switch (c) {
                case ' ': {
                    if (Caret.this.cursor < 0) break;
                    Caret.this.setDot(Caret.this.cursor + 1, (mask & 1) != 0);
                    break;
                }
                case '\n': {
                    if (Caret.this.cursor < 0) break;
                    Caret.this.setDot(Caret.this.cursor + (long)cols, (mask & 1) != 0);
                    break;
                }
                case '\b': 
                case '': {
                    Caret.this.hex.delete();
                    break;
                }
                default: {
                    HexModel model;
                    int digit = Character.digit(e.getKeyChar(), 16);
                    if (digit < 0 || (model = Caret.this.hex.getModel()) == null || Caret.this.cursor < model.getFirstOffset() || Caret.this.cursor > model.getLastOffset()) break;
                    int curValue = model.get(Caret.this.cursor);
                    int newValue = 16 * curValue + digit;
                    model.set(Caret.this.cursor, newValue);
                }
            }
        }

        @Override
        public void keyPressed(KeyEvent e) {
            int cols = Caret.this.hex.getMeasures().getColumnCount();
            boolean shift = (e.getModifiers() & 1) != 0;
            switch (e.getKeyCode()) {
                case 38: {
                    if (Caret.this.cursor < (long)cols) break;
                    Caret.this.setDot(Caret.this.cursor - (long)cols, shift);
                    break;
                }
                case 37: {
                    if (Caret.this.cursor < 1) break;
                    Caret.this.setDot(Caret.this.cursor - 1, shift);
                    break;
                }
                case 40: {
                    if (Caret.this.cursor < Caret.this.hex.getModel().getFirstOffset() || Caret.this.cursor > Caret.this.hex.getModel().getLastOffset() - (long)cols) break;
                    Caret.this.setDot(Caret.this.cursor + (long)cols, shift);
                    break;
                }
                case 39: {
                    if (Caret.this.cursor < Caret.this.hex.getModel().getFirstOffset() || Caret.this.cursor > Caret.this.hex.getModel().getLastOffset() - 1) break;
                    Caret.this.setDot(Caret.this.cursor + 1, shift);
                    break;
                }
                case 36: {
                    if (Caret.this.cursor >= 0) {
                        int dist = (int)(Caret.this.cursor % (long)cols);
                        if (dist == 0) {
                            Caret.this.setDot(0, shift);
                            break;
                        }
                        Caret.this.setDot(Caret.this.cursor - (long)dist, shift);
                        break;
                    }
                }
                case 35: {
                    if (Caret.this.cursor < 0) break;
                    HexModel model = Caret.this.hex.getModel();
                    long dest = Caret.this.cursor / (long)cols * (long)cols + (long)cols - 1;
                    if (model != null) {
                        long end = model.getLastOffset();
                        if (dest > end || dest == Caret.this.cursor) {
                            dest = end;
                        }
                        Caret.this.setDot(dest, shift);
                        break;
                    }
                    Caret.this.setDot(dest, shift);
                    break;
                }
                case 34: {
                    int rows = Caret.access$000((Caret)Caret.this).getVisibleRect().height / Caret.this.hex.getMeasures().getCellHeight();
                    if (rows > 2) {
                        --rows;
                    }
                    if (Caret.this.cursor < 0) break;
                    long max = Caret.this.hex.getModel().getLastOffset();
                    if (Caret.this.cursor + (long)(rows * cols) <= max) {
                        Caret.this.setDot(Caret.this.cursor + (long)(rows * cols), shift);
                        break;
                    }
                    long n = Caret.this.cursor;
                    while (n + (long)cols < max) {
                        n += (long)cols;
                    }
                    Caret.this.setDot(n, shift);
                    break;
                }
                case 33: {
                    int rows = Caret.access$000((Caret)Caret.this).getVisibleRect().height / Caret.this.hex.getMeasures().getCellHeight();
                    if (rows > 2) {
                        --rows;
                    }
                    if (Caret.this.cursor >= (long)(rows * cols)) {
                        Caret.this.setDot(Caret.this.cursor - (long)(rows * cols), shift);
                        break;
                    }
                    if (Caret.this.cursor < (long)cols) break;
                    Caret.this.setDot(Caret.this.cursor % (long)cols, shift);
                }
            }
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void focusGained(FocusEvent e) {
            Caret.this.expose(Caret.this.cursor, false);
        }

        @Override
        public void focusLost(FocusEvent e) {
            Caret.this.expose(Caret.this.cursor, false);
        }
    }

}

