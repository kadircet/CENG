/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.comp;

import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Graphics;
import javax.swing.Icon;

public abstract class AbstractComponentFactory
implements ComponentFactory {
    private static final Icon toolIcon = Icons.getIcon("subcirc.gif");

    protected AbstractComponentFactory() {
    }

    public String toString() {
        return this.getName();
    }

    @Override
    public abstract String getName();

    @Override
    public abstract String getDisplayName();

    @Override
    public abstract Component createComponent(Location var1, AttributeSet var2);

    @Override
    public abstract Bounds getOffsetBounds(AttributeSet var1);

    @Override
    public AttributeSet createAttributeSet() {
        return AttributeSets.EMPTY;
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        Bounds bds = this.getOffsetBounds(attrs);
        g.setColor(color);
        GraphicsUtil.switchToWidth(g, 2);
        g.drawRect(x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight());
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        if (toolIcon != null) {
            toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
        } else {
            g.setColor(Color.black);
            g.drawRect(x + 5, y + 2, 11, 17);
            Value[] v = new Value[]{Value.TRUE, Value.FALSE};
            for (int i = 0; i < 3; ++i) {
                g.setColor(v[i % 2].getColor());
                g.fillOval(x + 5 - 1, y + 5 + 5 * i - 1, 3, 3);
                g.setColor(v[(i + 1) % 2].getColor());
                g.fillOval(x + 16 - 1, y + 5 + 5 * i - 1, 3, 3);
            }
        }
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        return null;
    }
}

