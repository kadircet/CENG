/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.tools;

import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.tools.CaretListener;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

public interface Caret {
    public void addCaretListener(CaretListener var1);

    public void removeCaretListener(CaretListener var1);

    public String getText();

    public Bounds getBounds(Graphics var1);

    public void draw(Graphics var1);

    public void commitText(String var1);

    public void cancelEditing();

    public void stopEditing();

    public void mousePressed(MouseEvent var1);

    public void mouseDragged(MouseEvent var1);

    public void mouseReleased(MouseEvent var1);

    public void keyPressed(KeyEvent var1);

    public void keyReleased(KeyEvent var1);

    public void keyTyped(KeyEvent var1);
}

