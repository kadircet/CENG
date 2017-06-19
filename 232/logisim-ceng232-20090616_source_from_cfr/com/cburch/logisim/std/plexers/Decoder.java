/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.plexers;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
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

class Decoder
extends ManagedComponent
implements AttributeListener,
ToolTipMaker {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Plexers.facing_attr, Plexers.select_attr, Plexers.threeState_attr};
    private static final Object[] VALUES = new Object[]{Direction.EAST, Plexers.select_dflt, Plexers.threeState_dflt};
    private static final Icon toolIcon = Icons.getIcon("decoder.gif");

    private Decoder(Location loc, AttributeSet attrs) {
        super(loc, attrs, 4);
        attrs.setReadOnly(Plexers.facing_attr, true);
        attrs.setReadOnly(Plexers.select_attr, true);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        BitWidth data = BitWidth.ONE;
        Direction facing = (Direction)this.getAttributeSet().getValue(Plexers.facing_attr);
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        int outputs = 1 << select.getWidth();
        Location pt = this.getLocation();
        if (outputs == 2) {
            Location end1;
            Location end0;
            if (facing == Direction.WEST) {
                end0 = pt.translate(-10, -30);
                end1 = pt.translate(-10, -10);
            } else if (facing == Direction.NORTH) {
                end0 = pt.translate(10, -10);
                end1 = pt.translate(30, -10);
            } else if (facing == Direction.SOUTH) {
                end0 = pt.translate(10, 10);
                end1 = pt.translate(30, 10);
            } else {
                end0 = pt.translate(10, -30);
                end1 = pt.translate(10, -10);
            }
            this.setEnd(0, end0, data, 2);
            this.setEnd(1, end1, data, 2);
        } else {
            int dx = 0;
            int ddx = 10;
            int dy = (- outputs) * 10;
            int ddy = 10;
            if (facing == Direction.WEST) {
                dx = -20;
                ddx = 0;
            } else if (facing == Direction.NORTH) {
                dy = -20;
                ddy = 0;
            } else if (facing == Direction.SOUTH) {
                dy = 20;
                ddy = 0;
            } else {
                dx = 20;
                ddx = 0;
            }
            for (int i = 0; i < outputs; ++i) {
                this.setEnd(i, pt.translate(dx, dy), data, 2);
                dx += ddx;
                dy += ddy;
            }
        }
        this.setEnd(outputs, pt, select, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        BitWidth data = BitWidth.ONE;
        BitWidth select = (BitWidth)this.getAttributeSet().getValue(Plexers.select_attr);
        Boolean threeState = (Boolean)this.getAttributeSet().getValue(Plexers.threeState_attr);
        int outputs = 1 << select.getWidth();
        Value sel = state.getValue(this.getEndLocation(outputs));
        Value others = threeState != false ? Value.UNKNOWN : Value.FALSE;
        int outIndex = -1;
        Value out = null;
        if (sel.isFullyDefined()) {
            outIndex = sel.toIntValue();
            out = Value.TRUE;
        } else {
            others = sel.isErrorValue() ? Value.createError(data) : Value.createUnknown(data);
        }
        for (int i = 0; i < outputs; ++i) {
            state.setValue(this.getEndLocation(i), i == outIndex ? out : others, this, 3);
        }
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == Plexers.select_attr) {
            this.setPins();
        } else if (attr == Plexers.threeState_attr) {
            this.fireComponentInvalidated(new ComponentEvent(this));
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
        int outputs = 1 << select.getWidth();
        if (outputs == 2) {
            GraphicsUtil.switchToWidth(g, 3);
            EndData e = this.getEnd(outputs);
            Location pt = e.getLocation();
            if (context.getShowState()) {
                CircuitState state = context.getCircuitState();
                g.setColor(state.getValue(pt).getColor());
            }
            boolean vertical = facing == Direction.NORTH || facing == Direction.SOUTH;
            int dx = vertical ? 3 : 0;
            int dy = vertical ? 0 : -3;
            g.drawLine(pt.getX(), pt.getY(), pt.getX() + dx, pt.getY() + dy);
            GraphicsUtil.switchToWidth(g, 1);
            g.setColor(Color.BLACK);
        }
        Plexers.drawTrapezoid(g, this.getBounds(), facing.reverse(), outputs == 2 ? 10 : 20);
        Bounds bds = this.getBounds();
        if (facing == Direction.WEST) {
            x0 = 3;
            y0 = 15;
            halign = -1;
        } else if (facing == Direction.NORTH) {
            x0 = 10;
            y0 = 15;
            halign = 0;
        } else if (facing == Direction.SOUTH) {
            x0 = 10;
            y0 = bds.getHeight() - 3;
            halign = 0;
        } else {
            x0 = bds.getWidth() - 3;
            y0 = 15;
            halign = 1;
        }
        GraphicsUtil.drawText(g, "0", bds.getX() + x0, bds.getY() + y0, halign, 1);
        g.setColor(Color.BLACK);
        GraphicsUtil.drawCenteredText(g, "Decd", bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
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
            return Strings.get("decoderSelectTip");
        }
        if (end >= 0 && end < outputs) {
            return StringUtil.format(Strings.get("decoderOutTip"), "" + end);
        }
        return null;
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Decoder";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("decoderComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, VALUES);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Decoder(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Plexers.facing_attr);
            BitWidth select = (BitWidth)attrs.getValue(Plexers.select_attr);
            int outputs = 1 << select.getWidth();
            if (outputs == 2) {
                boolean reversed = facing == Direction.WEST || facing == Direction.NORTH || facing == Direction.SOUTH;
                int y = reversed ? 0 : -40;
                return Bounds.create(-20, y, 30, 40).rotate(Direction.EAST, facing, 0, 0);
            }
            boolean reversed = facing == Direction.NORTH || facing == Direction.SOUTH || facing == Direction.WEST;
            int x = -20;
            int y = reversed ? -10 : - outputs * 10 + 10;
            return Bounds.create(x, y, 40, outputs * 10 + 20).rotate(Direction.EAST, facing, 0, 0);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Plexers.facing_attr);
            BitWidth select = (BitWidth)attrs.getValue(Plexers.select_attr);
            Graphics g = context.getGraphics();
            g.setColor(color);
            Plexers.drawTrapezoid(g, this.getOffsetBounds(attrs).translate(x, y), facing.reverse(), select.getWidth() == 1 ? 10 : 20);
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

