/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.Probe;
import com.cburch.logisim.circuit.ProbeAttributes;
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
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

public class ProbeFactory
extends AbstractComponentFactory {
    public static ProbeFactory instance = new ProbeFactory();
    private static final Icon icon = Icons.getIcon("probe.gif");

    @Override
    public String getName() {
        return "Probe";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("probeComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return new ProbeAttributes();
    }

    @Override
    public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
        return new Probe(loc, attrs);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        ProbeAttributes pinAttrs = (ProbeAttributes)attrs;
        return ProbeFactory.getOffsetBounds(pinAttrs.facing, BitWidth.ONE, pinAttrs.radix);
    }

    @Override
    public void paintIcon(ComponentDrawContext c, int x, int y, AttributeSet attrs) {
        ProbeAttributes pinAttrs = (ProbeAttributes)attrs;
        Direction dir = pinAttrs.facing;
        Graphics g = c.getGraphics();
        if (icon != null) {
            Icons.paintRotated(g, x + 2, y + 2, dir, icon, c.getDestination());
        } else {
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
            g.setColor(Color.BLACK);
            g.drawOval(x + 4, y + 4, 13, 13);
            g.fillOval(pinx - 1, piny - 1, 3, 3);
        }
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        Bounds bds = this.getOffsetBounds(attrs);
        g.setColor(color);
        g.drawOval(x + bds.getX() + 1, y + bds.getY() + 1, bds.getWidth() - 1, bds.getHeight() - 1);
    }

    @Override
    public Object getFeature(Object key, AttributeSet attrs) {
        if (key == FACING_ATTRIBUTE_KEY) {
            return Pin.facing_attr;
        }
        return super.getFeature(key, attrs);
    }

    static Bounds getOffsetBounds(Direction dir, BitWidth width, RadixOption radix) {
        int len;
        Bounds ret = null;
        int n = len = radix == null || radix == RadixOption.RADIX_2 ? width.getWidth() : radix.getMaxLength(width);
        if (dir == Direction.EAST) {
            switch (len) {
                case 0: 
                case 1: {
                    ret = Bounds.create(-20, -10, 20, 20);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-20, -10, 20, 20);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-30, -10, 30, 20);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-40, -10, 40, 20);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-50, -10, 50, 20);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-60, -10, 60, 20);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-70, -10, 70, 20);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-80, -10, 80, 20);
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    ret = Bounds.create(-80, -20, 80, 40);
                    break;
                }
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    ret = Bounds.create(-80, -30, 80, 60);
                    break;
                }
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: {
                    ret = Bounds.create(-80, -40, 80, 80);
                }
            }
        } else if (dir == Direction.WEST) {
            switch (len) {
                case 0: 
                case 1: {
                    ret = Bounds.create(0, -10, 20, 20);
                    break;
                }
                case 2: {
                    ret = Bounds.create(0, -10, 20, 20);
                    break;
                }
                case 3: {
                    ret = Bounds.create(0, -10, 30, 20);
                    break;
                }
                case 4: {
                    ret = Bounds.create(0, -10, 40, 20);
                    break;
                }
                case 5: {
                    ret = Bounds.create(0, -10, 50, 20);
                    break;
                }
                case 6: {
                    ret = Bounds.create(0, -10, 60, 20);
                    break;
                }
                case 7: {
                    ret = Bounds.create(0, -10, 70, 20);
                    break;
                }
                case 8: {
                    ret = Bounds.create(0, -10, 80, 20);
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    ret = Bounds.create(0, -20, 80, 40);
                    break;
                }
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    ret = Bounds.create(0, -30, 80, 60);
                    break;
                }
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: {
                    ret = Bounds.create(0, -40, 80, 80);
                }
            }
        } else if (dir == Direction.SOUTH) {
            switch (len) {
                case 0: 
                case 1: {
                    ret = Bounds.create(-10, -20, 20, 20);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-10, -20, 20, 20);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-15, -20, 30, 20);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-20, -20, 40, 20);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-25, -20, 50, 20);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-30, -20, 60, 20);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-35, -20, 70, 20);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-40, -20, 80, 20);
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    ret = Bounds.create(-40, -40, 80, 40);
                    break;
                }
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    ret = Bounds.create(-40, -60, 80, 60);
                    break;
                }
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: {
                    ret = Bounds.create(-40, -80, 80, 80);
                }
            }
        } else if (dir == Direction.NORTH) {
            switch (len) {
                case 0: 
                case 1: {
                    ret = Bounds.create(-10, 0, 20, 20);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-10, 0, 20, 20);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-15, 0, 30, 20);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-20, 0, 40, 20);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-25, 0, 50, 20);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-30, 0, 60, 20);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-35, 0, 70, 20);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-40, 0, 80, 20);
                    break;
                }
                case 9: 
                case 10: 
                case 11: 
                case 12: 
                case 13: 
                case 14: 
                case 15: 
                case 16: {
                    ret = Bounds.create(-40, 0, 80, 40);
                    break;
                }
                case 17: 
                case 18: 
                case 19: 
                case 20: 
                case 21: 
                case 22: 
                case 23: 
                case 24: {
                    ret = Bounds.create(-40, 0, 80, 60);
                    break;
                }
                case 25: 
                case 26: 
                case 27: 
                case 28: 
                case 29: 
                case 30: 
                case 31: 
                case 32: {
                    ret = Bounds.create(-40, 0, 80, 80);
                }
            }
        }
        if (ret == null) {
            ret = Bounds.create(0, -10, 20, 20);
        }
        return ret;
    }
}

