/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.circuit.Wire;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Graphics;

class WireFactory
extends AbstractComponentFactory {
    public static final WireFactory instance = new WireFactory();

    private WireFactory() {
    }

    @Override
    public String getName() {
        return "Wire";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("wireComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return Wire.create(Location.create(0, 0), Location.create(100, 0));
    }

    @Override
    public Component createComponent(Location loc, AttributeSet attrs) {
        Object dir = attrs.getValue(Wire.dir_attr);
        int len = (Integer)attrs.getValue(Wire.len_attr);
        if (dir == Wire.horz_value) {
            return Wire.create(loc, loc.translate(len, 0));
        }
        return Wire.create(loc, loc.translate(0, len));
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        Object dir = attrs.getValue(Wire.dir_attr);
        int len = (Integer)attrs.getValue(Wire.len_attr);
        if (dir == Wire.horz_value) {
            return Bounds.create(0, -2, len, 5);
        }
        return Bounds.create(-2, 0, 5, len);
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        Object dir = attrs.getValue(Wire.dir_attr);
        int len = (Integer)attrs.getValue(Wire.len_attr);
        g.setColor(color);
        GraphicsUtil.switchToWidth(g, 3);
        if (dir == Wire.horz_value) {
            g.drawLine(x, y, x + len, y);
        } else {
            g.drawLine(x, y, x, y + len);
        }
    }
}

