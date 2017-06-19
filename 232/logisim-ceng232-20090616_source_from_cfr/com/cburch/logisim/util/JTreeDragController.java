/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.util;

import java.awt.Point;
import javax.swing.JTree;

public interface JTreeDragController {
    public boolean canPerformAction(JTree var1, Object var2, int var3, Point var4);

    public boolean executeDrop(JTree var1, Object var2, Object var3, int var4);
}

