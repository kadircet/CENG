/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretEvent;
import com.cburch.logisim.tools.CaretListener;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

class TextFieldCaret
implements Caret {
    private LinkedList listeners = new LinkedList();
    private TextField field;
    private Graphics g;
    private String old_text;
    private String cur_text;
    private int pos;

    public TextFieldCaret(TextField field, Graphics g, int pos) {
        this.field = field;
        this.g = g;
        this.old_text = field.getText();
        this.cur_text = field.getText();
        this.pos = pos;
    }

    public TextFieldCaret(TextField field, Graphics g, int x, int y) {
        this(field, g, 0);
        this.moveCaret(x, y);
    }

    @Override
    public void addCaretListener(CaretListener l) {
        this.listeners.add(l);
    }

    @Override
    public void removeCaretListener(CaretListener l) {
        this.listeners.remove(l);
    }

    @Override
    public String getText() {
        return this.cur_text;
    }

    @Override
    public void commitText(String text) {
        this.cur_text = text;
        this.pos = this.cur_text.length();
        this.field.setText(text);
    }

    @Override
    public void draw(Graphics g) {
        if (this.field.getFont() != null) {
            g.setFont(this.field.getFont());
        }
        Bounds bds = this.getBounds(g);
        g.setColor(Color.white);
        g.fillRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        g.setColor(Color.black);
        g.drawRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        int x = this.field.getX();
        int y = this.field.getY();
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(this.cur_text);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        switch (this.field.getHAlign()) {
            case 0: {
                x -= width / 2;
                break;
            }
            case 1: {
                x -= width;
                break;
            }
        }
        switch (this.field.getVAlign()) {
            case -1: {
                y += ascent;
                break;
            }
            case 0: {
                y += (ascent - descent) / 2;
                break;
            }
            case 2: {
                y -= descent;
                break;
            }
        }
        g.drawString(this.cur_text, x, y);
        if (this.pos > 0) {
            x += fm.stringWidth(this.cur_text.substring(0, this.pos));
        }
        g.drawLine(x, y, x, y - ascent);
    }

    @Override
    public Bounds getBounds(Graphics g) {
        int x = this.field.getX();
        int y = this.field.getY();
        Font font = this.field.getFont();
        FontMetrics fm = font == null ? g.getFontMetrics() : g.getFontMetrics(font);
        int width = fm.stringWidth(this.cur_text);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        int height = ascent + descent;
        switch (this.field.getHAlign()) {
            case 0: {
                x -= width / 2;
                break;
            }
            case 1: {
                x -= width;
                break;
            }
        }
        switch (this.field.getVAlign()) {
            case -1: {
                y += ascent;
                break;
            }
            case 0: {
                y += (ascent - descent) / 2;
                break;
            }
            case 2: {
                y -= descent;
                break;
            }
        }
        return Bounds.create(x, y - ascent, width, height).add(this.field.getBounds(g)).expand(3);
    }

    @Override
    public void cancelEditing() {
        CaretEvent e = new CaretEvent(this, this.old_text, this.old_text);
        this.cur_text = this.old_text;
        this.pos = this.cur_text.length();
        Iterator it = ((List)this.listeners.clone()).iterator();
        while (it.hasNext()) {
            ((CaretListener)it.next()).editingCanceled(e);
        }
    }

    @Override
    public void stopEditing() {
        CaretEvent e = new CaretEvent(this, this.old_text, this.cur_text);
        this.field.setText(this.cur_text);
        Iterator it = ((List)this.listeners.clone()).iterator();
        while (it.hasNext()) {
            ((CaretListener)it.next()).editingStopped(e);
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
        this.moveCaret(e.getX(), e.getY());
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        this.moveCaret(e.getX(), e.getY());
    }

    @Override
    public void keyPressed(KeyEvent e) {
        int ign = 14;
        if ((e.getModifiers() & ign) != 0) {
            return;
        }
        switch (e.getKeyCode()) {
            case 37: 
            case 226: {
                if (this.pos <= 0) break;
                --this.pos;
                break;
            }
            case 39: 
            case 227: {
                if (this.pos >= this.cur_text.length()) break;
                ++this.pos;
                break;
            }
            case 36: {
                this.pos = 0;
                break;
            }
            case 35: {
                this.pos = this.cur_text.length();
                break;
            }
            case 3: 
            case 27: {
                this.cancelEditing();
                break;
            }
            case 12: {
                this.cur_text = "";
                this.pos = 0;
                break;
            }
            case 10: {
                this.stopEditing();
                break;
            }
            case 8: {
                if (this.pos <= 0) break;
                this.cur_text = this.cur_text.substring(0, this.pos - 1) + this.cur_text.substring(this.pos);
                --this.pos;
                break;
            }
            case 127: {
                if (this.pos >= this.cur_text.length()) break;
                this.cur_text = this.cur_text.substring(0, this.pos) + this.cur_text.substring(this.pos + 1);
                break;
            }
            case 155: 
            case 65485: 
            case 65487: 
            case 65489: {
                break;
            }
        }
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
        int ign = 14;
        if ((e.getModifiers() & ign) != 0) {
            return;
        }
        char c = e.getKeyChar();
        if (c == '\n') {
            this.stopEditing();
        } else if (c != '\uffff' && !Character.isISOControl(c)) {
            this.cur_text = this.pos < this.cur_text.length() ? this.cur_text.substring(0, this.pos) + c + this.cur_text.substring(this.pos) : this.cur_text + c;
            ++this.pos;
        }
    }

    private void moveCaret(int x, int y) {
        Bounds bds = this.getBounds(this.g);
        FontMetrics fm = this.g.getFontMetrics();
        x -= bds.getX();
        int last = 0;
        for (int i = 0; i < this.cur_text.length(); ++i) {
            int cur = fm.stringWidth(this.cur_text.substring(0, i + 1));
            if (x <= (last + cur) / 2) {
                this.pos = i;
                return;
            }
            last = cur;
        }
        this.pos = this.cur_text.length();
    }
}

