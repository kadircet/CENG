/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.plexers;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Attributes;
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
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import javax.swing.Icon;

class BitSelector
extends ManagedComponent
implements AttributeListener,
ToolTipMaker {
    public static final ComponentFactory factory = new Factory();
    public static final Attribute GROUP_ATTR = Attributes.forBitWidth("group", Strings.getter("bitSelectorGroupAttr"));
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Plexers.facing_attr, Plexers.data_attr, GROUP_ATTR};
    private static final Object[] VALUES = new Object[]{Direction.EAST, BitWidth.create(8), BitWidth.ONE};
    private static final Icon toolIcon = Icons.getIcon("bitSelector.gif");

    private BitSelector(Location loc, AttributeSet attrs) {
        super(loc, attrs, 3);
        attrs.addAttributeListener(this);
        attrs.setReadOnly(Plexers.facing_attr, true);
        this.setPins();
    }

    private void setPins() {
        Location selPt;
        Location inPt;
        Direction facing = (Direction)this.getAttributeSet().getValue(Plexers.facing_attr);
        BitWidth data = (BitWidth)this.getAttributeSet().getValue(Plexers.data_attr);
        BitWidth group = (BitWidth)this.getAttributeSet().getValue(GROUP_ATTR);
        int groups = (data.getWidth() + group.getWidth() - 1) / group.getWidth() - 1;
        int selectBits = 1;
        if (groups > 0) {
            while (groups != 1) {
                groups >>= 1;
                ++selectBits;
            }
        }
        BitWidth select = BitWidth.create(selectBits);
        Location outPt = this.getLocation();
        if (facing == Direction.WEST) {
            inPt = outPt.translate(30, 0);
            selPt = outPt.translate(10, 10);
        } else if (facing == Direction.NORTH) {
            inPt = outPt.translate(0, 30);
            selPt = outPt.translate(-10, 10);
        } else if (facing == Direction.SOUTH) {
            inPt = outPt.translate(0, -30);
            selPt = outPt.translate(-10, -10);
        } else {
            inPt = outPt.translate(-30, 0);
            selPt = outPt.translate(-10, 10);
        }
        this.setEnd(0, outPt, group, 2);
        this.setEnd(1, inPt, data, 1);
        this.setEnd(2, selPt, select, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value group;
        Value data = state.getValue(this.getEndLocation(1));
        Value select = state.getValue(this.getEndLocation(2));
        BitWidth groupBits = (BitWidth)this.getAttributeSet().getValue(GROUP_ATTR);
        if (!select.isFullyDefined()) {
            group = Value.createUnknown(groupBits);
        } else {
            int shift = select.toIntValue() * groupBits.getWidth();
            if (shift >= data.getWidth()) {
                group = Value.createKnown(groupBits, 0);
            } else if (groupBits.getWidth() == 1) {
                group = data.get(shift);
            } else {
                Value[] bits = new Value[groupBits.getWidth()];
                for (int i = 0; i < bits.length; ++i) {
                    bits[i] = shift + i >= data.getWidth() ? Value.FALSE : data.get(shift + i);
                }
                group = Value.create(bits);
            }
        }
        state.setValue(this.getEndLocation(0), group, this, 3);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == Plexers.data_attr) {
            this.setPins();
        } else if (attr == GROUP_ATTR) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        Direction facing = (Direction)this.getAttributeSet().getValue(Plexers.facing_attr);
        Plexers.drawTrapezoid(g, this.getBounds(), facing, 9);
        Bounds bds = this.getBounds();
        g.setColor(Color.BLACK);
        GraphicsUtil.drawCenteredText(g, "Sel", bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
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
        if (end == 0) {
            return Strings.get("bitSelectorOutputTip");
        }
        if (end == 1) {
            return Strings.get("bitSelectorDataTip");
        }
        if (end == 2) {
            return Strings.get("bitSelectorSelectTip");
        }
        return null;
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "BitSelector";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("bitSelectorComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, VALUES);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new BitSelector(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Plexers.facing_attr);
            return Bounds.create(-30, -15, 30, 30).rotate(Direction.EAST, facing, 0, 0);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Plexers.facing_attr);
            Graphics g = context.getGraphics();
            g.setColor(color);
            Plexers.drawTrapezoid(g, this.getOffsetBounds(attrs).translate(x, y), facing, 9);
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

