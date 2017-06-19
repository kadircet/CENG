/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.plexers;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.EndData;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.std.plexers.Plexers;
import com.cburch.logisim.std.plexers.Strings;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class Multiplexer
extends ManagedComponent
implements AttributeListener,
ToolTipMaker {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Plexers.facing_attr, Plexers.select_attr, Plexers.data_attr};
    private static final Object[] VALUES = new Object[]{Direction.EAST, Plexers.select_dflt, Plexers.data_dflt};
    private static final Icon toolIcon = Icons.getIcon("multiplexer.gif");

    private Multiplexer(Location loc, AttributeSet attrs) {
        super(loc, attrs, 4);
        attrs.setReadOnly(Plexers.facing_attr, true);
        attrs.setReadOnly(Plexers.select_attr, true);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        Object dir = this.getAttributeSet().getValue(Plexers.facing_attr);
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Plexers.data_attr);
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        int inputs = 1 << select.getWidth();
        Location pt = this.getLocation();
        if (inputs == 2) {
            Location end1;
            Location end2;
            Location end0;
            if (dir == Direction.WEST) {
                end0 = pt.translate(30, -10);
                end1 = pt.translate(30, 10);
                end2 = pt.translate(20, 20);
            } else if (dir == Direction.NORTH) {
                end0 = pt.translate(-10, 30);
                end1 = pt.translate(10, 30);
                end2 = pt.translate(-20, 20);
            } else if (dir == Direction.SOUTH) {
                end0 = pt.translate(-10, -30);
                end1 = pt.translate(10, -30);
                end2 = pt.translate(-20, -20);
            } else {
                end0 = pt.translate(-30, -10);
                end1 = pt.translate(-30, 10);
                end2 = pt.translate(-20, 20);
            }
            this.setEnd(0, end0, data, 1);
            this.setEnd(1, end1, data, 1);
            this.setEnd(2, end2, select, 1);
        } else {
            Location selLoc;
            int dx = (- inputs / 2) * 10;
            int ddx = 10;
            int dy = (- inputs / 2) * 10;
            int ddy = 10;
            if (dir == Direction.WEST) {
                dx = 40;
                ddx = 0;
                selLoc = pt.translate(20, dy + 10 * inputs);
            } else if (dir == Direction.NORTH) {
                dy = 40;
                ddy = 0;
                selLoc = pt.translate(dx, 20);
            } else if (dir == Direction.SOUTH) {
                dy = -40;
                ddy = 0;
                selLoc = pt.translate(dx, -20);
            } else {
                dx = -40;
                ddx = 0;
                selLoc = pt.translate(-20, dy + 10 * inputs);
            }
            for (int i = 0; i < inputs; ++i) {
                this.setEnd(i, pt.translate(dx, dy), data, 1);
                dx += ddx;
                dy += ddy;
            }
            this.setEnd(inputs, selLoc, select, 1);
        }
        this.setEnd(inputs + 1, pt, data, 2);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Plexers.data_attr);
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        int inputs = 1 << select.getWidth();
        Value sel = state.getValue(this.getEndLocation(inputs));
        Value out = sel.isFullyDefined() ? state.getValue(this.getEndLocation(sel.toIntValue())) : (sel.isErrorValue() ? Value.createError(data) : Value.createUnknown(data));
        state.setValue(this.getEndLocation(inputs + 1), out, this, 3);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == Plexers.data_attr) {
            this.setPins();
        } else if (attr == Plexers.select_attr) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        int x0;
        int y0;
        int halign;
        Graphics g = context.getGraphics();
        Direction facing = (Direction)this.getAttributeSet().getValue(Plexers.facing_attr);
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        int inputs = 1 << select.getWidth();
        if (inputs == 2) {
            GraphicsUtil.switchToWidth(g, 3);
            EndData e = this.getEnd(inputs);
            Location pt = e.getLocation();
            if (context.getShowState()) {
                CircuitState state = context.getCircuitState();
                g.setColor(state.getValue(pt).getColor());
            }
            boolean vertical = facing != Direction.NORTH && facing != Direction.SOUTH;
            int dx = vertical ? 0 : -3;
            int dy = vertical ? 3 : 0;
            g.drawLine(pt.getX() - dx, pt.getY() - dy, pt.getX(), pt.getY());
            GraphicsUtil.switchToWidth(g, 1);
            g.setColor(Color.BLACK);
        }
        Plexers.drawTrapezoid(g, this.getBounds(), facing, select.getWidth() == 1 ? 10 : 20);
        Bounds bds = this.getBounds();
        g.setColor(Color.GRAY);
        if (facing == Direction.WEST) {
            x0 = bds.getX() + bds.getWidth() - 3;
            y0 = bds.getY() + 15;
            halign = 1;
        } else if (facing == Direction.NORTH) {
            x0 = bds.getX() + 10;
            y0 = bds.getY() + bds.getHeight() - 2;
            halign = 0;
        } else if (facing == Direction.SOUTH) {
            x0 = bds.getX() + 10;
            y0 = bds.getY() + 12;
            halign = 0;
        } else {
            x0 = bds.getX() + 3;
            y0 = bds.getY() + 15;
            halign = -1;
        }
        GraphicsUtil.drawText(g, "0", x0, y0, halign, 1);
        g.setColor(Color.BLACK);
        GraphicsUtil.drawCenteredText(g, "MUX", bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == ToolTipMaker.class) {
            return this;
        }
        return null;
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        int end = -1;
        for (int i = this.getEnds().size() - 1; i >= 0; --i) {
            if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            end = i;
            break;
        }
        if (end < 0) {
            return null;
        }
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        int outputs = 1 << select.getWidth();
        if (end == outputs) {
            return Strings.get("multiplexerSelectTip");
        }
        if (end == outputs + 1) {
            return Strings.get("multiplexerOutTip");
        }
        if (end >= 0 && end < outputs) {
            return StringUtil.format(Strings.get("multiplexerInTip"), "" + end);
        }
        return null;
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Multiplexer";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("multiplexerComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, VALUES);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Multiplexer(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction dir = (Direction)attrs.getValue(Plexers.facing_attr);
            BitWidth select = (BitWidth)attrs.getValue(Plexers.select_attr);
            int inputs = 1 << select.getWidth();
            if (inputs == 2) {
                return Bounds.create(-30, -20, 30, 40).rotate(Direction.EAST, dir, 0, 0);
            }
            int offs = (- inputs / 2) * 10 - 10;
            int length = inputs * 10 + 20;
            return Bounds.create(-40, offs, 40, length).rotate(Direction.EAST, dir, 0, 0);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Plexers.facing_attr);
            BitWidth select = (BitWidth)attrs.getValue(Plexers.select_attr);
            Graphics g = context.getGraphics();
            g.setColor(color);
            Plexers.drawTrapezoid(g, this.getOffsetBounds(attrs).translate(x, y), facing, select.getWidth() == 1 ? 10 : 20);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            if (toolIcon != null) {
                toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            }
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return Plexers.facing_attr;
            }
            return super.getFeature(key, attrs);
        }
    }

}

