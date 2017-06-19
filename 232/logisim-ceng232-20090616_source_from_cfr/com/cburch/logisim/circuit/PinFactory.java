/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.PinAttributes;
import com.cburch.logisim.circuit.ProbeFactory;
import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;

public class PinFactory
extends AbstractComponentFactory {
    public static PinFactory instance = new PinFactory();
    private static final Font iconTextFont = new Font("SansSerif", 1, 9);
    private static final Color iconTextColor = Value.WIDTH_ERROR_COLOR.darker();
    private static final Icon inputIcon = Icons.getIcon("pinInput.gif");
    private static final Icon outputIcon = Icons.getIcon("pinOutput.gif");

    @Override
    public String getName() {
        return "Pin";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("pinComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new PinAttributes();
    }

    @Override
    public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
        return new Pin(loc, attrs);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        PinAttributes pinAttrs = (PinAttributes)attrs;
        return ProbeFactory.getOffsetBounds(pinAttrs.facing, pinAttrs.width, RadixOption.RADIX_2);
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        if (key == TOOL_TIP) {
            PinAttributes pinAttrs = (PinAttributes)attrs;
            return pinAttrs.type == 2 ? Strings.get("pinOutputToolTip") : Strings.get("pinInputToolTip");
        }
        if (key == FACING_ATTRIBUTE_KEY) {
            return Pin.facing_attr;
        }
        return super.getFeature(key, attrs);
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y, AttributeSet attrs) {
        this.paintIconBase(c, x, y, attrs);
        PinAttributes pinAttrs = (PinAttributes)attrs;
        BitWidth w = pinAttrs.width;
        if (!w.equals(BitWidth.ONE)) {
            Graphics g = c.getGraphics();
            g.setColor(iconTextColor);
            g.setFont(iconTextFont);
            GraphicsUtil.drawCenteredText(g, "" + w.getWidth(), x + 10, y + 9);
            g.setColor(Color.BLACK);
        }
    }

    private void paintIconBase(ComponentDrawContext c, int x, int y, AttributeSet attrs) {
        PinAttributes pinAttrs = (PinAttributes)attrs;
        boolean isOutput = pinAttrs.type == 2;
        Direction dir = pinAttrs.facing;
        Graphics g = c.getGraphics();
        if (isOutput) {
            if (outputIcon != null) {
                Icons.paintRotated(g, x + 2, y + 2, dir, outputIcon, c.getDestination());
                return;
            }
        } else if (inputIcon != null) {
            Icons.paintRotated(g, x + 2, y + 2, dir, inputIcon, c.getDestination());
            return;
        }
        int pinx = x + 16;
        int piny = y + 9;
        if (dir != Direction.EAST) {
            if (dir == Direction.WEST) {
                pinx = x + 4;
            } else if (dir == Direction.NORTH) {
                pinx = x + 9;
                piny = y + 4;
            } else if (dir == Direction.SOUTH) {
                pinx = x + 9;
                piny = y + 16;
            }
        }
        g.setColor(Color.black);
        if (isOutput) {
            g.drawOval(x + 4, y + 4, 13, 13);
        } else {
            g.drawRect(x + 4, y + 4, 13, 13);
        }
        g.setColor(Value.TRUE.getColor());
        g.fillOval(x + 7, y + 7, 8, 8);
        g.fillOval(pinx, piny, 3, 3);
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        PinAttributes pinAttrs = (PinAttributes)attrs;
        Graphics g = context.getGraphics();
        Bounds bds = this.getOffsetBounds(attrs);
        g.setColor(color);
        GraphicsUtil.switchToWidth(g, 2);
        if (pinAttrs.type == 2) {
            if (pinAttrs.width == BitWidth.ONE) {
                g.drawOval(x + bds.getX() + 1, y + bds.getY() + 1, bds.getWidth() - 1, bds.getHeight() - 1);
            } else {
                g.drawRoundRect(x + bds.getX() + 1, y + bds.getY() + 1, bds.getWidth() - 1, bds.getHeight() - 1, 6, 6);
            }
        } else {
            g.drawRect(x + bds.getX() + 1, y + bds.getY() + 1, bds.getWidth() - 1, bds.getHeight() - 1);
        }
    }
}

