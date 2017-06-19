/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.gui.main.Canvas;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public abstract class Tool {
    private static Cursor dflt_cursor = Cursor.getPredefinedCursor(1);

    public abstract String getName();

    public abstract String getDisplayName();

    public abstract String getDescription();

    public Tool cloneTool() {
        return this;
    }

    public boolean sharesSource(Tool other) {
        return this == other;
    }

    public AttributeSet getAttributeSet() {
        return null;
    }

    public void setAttributeSet(AttributeSet attrs) {
    }

    public void paintIcon(ComponentDrawContext c, int x, int y) {
    }

    public String toString() {
        return this.getName();
    }

    public void draw(ComponentDrawContext context) {
    }

    public void draw(Canvas canvas, ComponentDrawContext context) {
        this.draw(context);
    }

    public void select(Canvas canvas) {
    }

    public void deselect(Canvas canvas) {
    }

    public void mousePressed(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void mouseDragged(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void mouseReleased(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void mouseEntered(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void mouseExited(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void mouseMoved(Canvas canvas, Graphics g, MouseEvent e) {
    }

    public void keyTyped(Canvas canvas, KeyEvent e) {
    }

    public void keyPressed(Canvas canvas, KeyEvent e) {
    }

    public void keyReleased(Canvas canvas, KeyEvent e) {
    }

    public Cursor getCursor() {
        return dflt_cursor;
    }
}

