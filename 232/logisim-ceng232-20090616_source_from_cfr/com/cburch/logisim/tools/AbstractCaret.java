/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretListener;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class AbstractCaret
implements Caret {
    private ArrayList listeners = new ArrayList();
    private List listenersView = Collections.unmodifiableList(this.listeners);
    private Bounds bds = Bounds.EMPTY_BOUNDS;

    @Override
    public void addCaretListener(CaretListener e) {
        this.listeners.add(e);
    }

    @Override
    public void removeCaretListener(CaretListener e) {
        this.listeners.remove(e);
    }

    protected List getCaretListeners() {
        return this.listenersView;
    }

    public void setBounds(Bounds value) {
        this.bds = value;
    }

    @Override
    public String getText() {
        return "";
    }

    @Override
    public Bounds getBounds(Graphics g) {
        return this.bds;
    }

    @Override
    public void draw(Graphics g) {
    }

    @Override
    public void commitText(String text) {
    }

    @Override
    public void cancelEditing() {
    }

    @Override
    public void stopEditing() {
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseDragged(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void keyPressed(KeyEvent e) {
    }

    @Override
    public void keyReleased(KeyEvent e) {
    }

    @Override
    public void keyTyped(KeyEvent e) {
    }
}

