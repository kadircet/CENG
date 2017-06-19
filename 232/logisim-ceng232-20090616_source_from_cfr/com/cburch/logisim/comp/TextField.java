/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.TextFieldCaret;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.comp.TextFieldListener;
import com.cburch.logisim.data.Bounds;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.util.Iterator;
import java.util.LinkedList;

public class TextField {
    public static final int H_LEFT = -1;
    public static final int H_CENTER = 0;
    public static final int H_RIGHT = 1;
    public static final int V_TOP = -1;
    public static final int V_CENTER = 0;
    public static final int V_BASELINE = 1;
    public static final int V_BOTTOM = 2;
    private int x;
    private int y;
    private int halign;
    private int valign;
    private Font font;
    private String text = "";
    private LinkedList listeners = new LinkedList();

    public TextField(int x, int y, int halign, int valign) {
        this(x, y, halign, valign, null);
    }

    public TextField(int x, int y, int halign, int valign, Font font) {
        this.x = x;
        this.y = y;
        this.halign = halign;
        this.valign = valign;
        this.font = font;
    }

    public void addTextFieldListener(TextFieldListener l) {
        this.listeners.add(l);
    }

    public void removeTextFieldListener(TextFieldListener l) {
        this.listeners.remove(l);
    }

    public void fireTextChanged(TextFieldEvent e) {
        Iterator it = this.listeners.iterator();
        while (it.hasNext()) {
            ((TextFieldListener)it.next()).textChanged(e);
        }
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public int getHAlign() {
        return this.halign;
    }

    public int getVAlign() {
        return this.valign;
    }

    public Font getFont() {
        return this.font;
    }

    public String getText() {
        return this.text;
    }

    public TextFieldCaret getCaret(Graphics g, int pos) {
        return new TextFieldCaret(this, g, pos);
    }

    public void setText(String text) {
        if (!text.equals(this.text)) {
            TextFieldEvent e = new TextFieldEvent(this, this.text, text);
            this.text = text;
            this.fireTextChanged(e);
        }
    }

    public void setLocation(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setLocation(int x, int y, int halign, int valign) {
        this.x = x;
        this.y = y;
        this.halign = halign;
        this.valign = valign;
    }

    public void setAlign(int halign, int valign) {
        this.halign = halign;
        this.valign = valign;
    }

    public void setHorzAlign(int halign) {
        this.halign = halign;
    }

    public void setVertAlign(int valign) {
        this.valign = valign;
    }

    public void setFont(Font font) {
        this.font = font;
    }

    public TextFieldCaret getCaret(Graphics g, int x, int y) {
        return new TextFieldCaret(this, g, x, y);
    }

    public Bounds getBounds(Graphics g) {
        int x = this.x;
        int y = this.y;
        FontMetrics fm = this.font == null ? g.getFontMetrics() : g.getFontMetrics(this.font);
        int width = fm.stringWidth(this.text);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        switch (this.halign) {
            case 0: {
                x -= width / 2;
                break;
            }
            case 1: {
                x -= width;
                break;
            }
        }
        switch (this.valign) {
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
        return Bounds.create(x, y - ascent, width, ascent + descent);
    }

    public void draw(Graphics g) {
        Font old = g.getFont();
        if (this.font != null) {
            g.setFont(this.font);
        }
        int x = this.x;
        int y = this.y;
        FontMetrics fm = g.getFontMetrics();
        int width = fm.stringWidth(this.text);
        int ascent = fm.getAscent();
        int descent = fm.getDescent();
        switch (this.halign) {
            case 0: {
                x -= width / 2;
                break;
            }
            case 1: {
                x -= width;
                break;
            }
        }
        switch (this.valign) {
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
        g.drawString(this.text, x, y);
        g.setFont(old);
    }
}

