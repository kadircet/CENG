/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.gui.log;

import com.cburch.logisim.circuit.Circuit;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;

class ComponentIcon
implements Icon {
    public static final int TRIANGLE_NONE = 0;
    public static final int TRIANGLE_CLOSED = 1;
    public static final int TRIANGLE_OPEN = 2;
    private Component comp;
    private int triangleState = 0;

    ComponentIcon(Component comp) {
        this.comp = comp;
    }

    public void setTriangleState(int value) {
        this.triangleState = value;
    }

    @Override
    public int getIconHeight() {
        return 20;
    }

    @Override
    public int getIconWidth() {
        return 20;
    }

    @Override
    public void paintIcon(java.awt.Component c, Graphics g, int x, int y) {
        Graphics gIcon = g.create();
        ComponentDrawContext context = new ComponentDrawContext(c, null, null, g, gIcon);
        this.comp.getFactory().paintIcon(context, x, y, this.comp.getAttributeSet());
        gIcon.dispose();
        if (this.triangleState != 0) {
            int[] xp;
            int[] yp;
            if (this.triangleState == 1) {
                xp = new int[]{x + 13, x + 13, x + 17};
                yp = new int[]{y + 11, y + 19, y + 15};
            } else {
                xp = new int[]{x + 11, x + 19, x + 15};
                yp = new int[]{y + 13, y + 13, y + 17};
            }
            g.setColor(Color.LIGHT_GRAY);
            g.fillPolygon(xp, yp, 3);
            g.setColor(Color.DARK_GRAY);
            g.drawPolygon(xp, yp, 3);
        }
    }
}

