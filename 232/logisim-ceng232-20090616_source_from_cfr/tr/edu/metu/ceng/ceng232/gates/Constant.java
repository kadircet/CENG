/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ExpressionComputer;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.AbstractAttributeSet;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.IntegerFactory;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class Constant
extends ManagedComponent
implements AttributeListener,
ExpressionComputer {
    public static ComponentFactory factory = new Factory();
    public static final Attribute facing_attr = Attributes.forDirection("facing", Strings.getter("constantFacingAttr"));
    public static final Attribute width_attr = Attributes.forBitWidth("width", Strings.getter("constantBitWidthAttr"));
    public static final Attribute value_attr = Attributes.forHexInteger("value", Strings.getter("constantValueAttr"));
    private static final Color backgroundColor = new Color(230, 230, 230);
    private static final List ATTRIBUTES = Arrays.asList(facing_attr, width_attr, value_attr);

    private static Bounds getOffsetBounds(ConstantAttributes attrs) {
        Direction facing = attrs.facing;
        int chars = (attrs.width.getWidth() + 3) / 4;
        Bounds ret = null;
        if (facing == Direction.EAST) {
            switch (chars) {
                case 1: {
                    ret = Bounds.create(-16, -8, 16, 16);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-16, -8, 16, 16);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-26, -8, 26, 16);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-36, -8, 36, 16);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-46, -8, 46, 16);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-56, -8, 56, 16);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-66, -8, 66, 16);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-76, -8, 76, 16);
                }
            }
        } else if (facing == Direction.WEST) {
            switch (chars) {
                case 1: {
                    ret = Bounds.create(0, -8, 16, 16);
                    break;
                }
                case 2: {
                    ret = Bounds.create(0, -8, 16, 16);
                    break;
                }
                case 3: {
                    ret = Bounds.create(0, -8, 26, 16);
                    break;
                }
                case 4: {
                    ret = Bounds.create(0, -8, 36, 16);
                    break;
                }
                case 5: {
                    ret = Bounds.create(0, -8, 46, 16);
                    break;
                }
                case 6: {
                    ret = Bounds.create(0, -8, 56, 16);
                    break;
                }
                case 7: {
                    ret = Bounds.create(0, -8, 66, 16);
                    break;
                }
                case 8: {
                    ret = Bounds.create(0, -8, 76, 16);
                }
            }
        } else if (facing == Direction.SOUTH) {
            switch (chars) {
                case 1: {
                    ret = Bounds.create(-8, -16, 16, 16);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-8, -16, 16, 16);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-13, -16, 26, 16);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-18, -16, 36, 16);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-23, -16, 46, 16);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-28, -16, 56, 16);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-33, -16, 66, 16);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-38, -16, 76, 16);
                }
            }
        } else if (facing == Direction.NORTH) {
            switch (chars) {
                case 1: {
                    ret = Bounds.create(-8, 0, 16, 16);
                    break;
                }
                case 2: {
                    ret = Bounds.create(-8, 0, 16, 16);
                    break;
                }
                case 3: {
                    ret = Bounds.create(-13, 0, 26, 16);
                    break;
                }
                case 4: {
                    ret = Bounds.create(-18, 0, 36, 16);
                    break;
                }
                case 5: {
                    ret = Bounds.create(-23, 0, 46, 16);
                    break;
                }
                case 6: {
                    ret = Bounds.create(-28, 0, 56, 16);
                    break;
                }
                case 7: {
                    ret = Bounds.create(-33, 0, 66, 16);
                    break;
                }
                case 8: {
                    ret = Bounds.create(-38, 0, 76, 16);
                }
            }
        }
        if (ret == null) {
            throw new IllegalArgumentException("unrecognized arguments " + facing + " " + attrs.width);
        }
        return ret;
    }

    public Constant(Location loc, AttributeSet attrs) {
        super(loc, attrs, 1);
        attrs.addAttributeListener(this);
        this.setPins((ConstantAttributes)attrs);
    }

    private void setPins(ConstantAttributes attrs) {
        this.setEnd(0, this.getLocation(), attrs.width, 2);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        ConstantAttributes attrs = (ConstantAttributes)this.getAttributeSet();
        state.setValue(this.getLocation(), attrs.value, this, 1);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == width_attr) {
            ConstantAttributes attrs = (ConstantAttributes)this.getAttributeSet();
            Location loc = this.getLocation();
            Bounds offset = Constant.getOffsetBounds(attrs);
            this.setBounds(offset.translate(loc.getX(), loc.getY()));
            this.setPins(attrs);
        } else if (attr == facing_attr) {
            ConstantAttributes attrs = (ConstantAttributes)this.getAttributeSet();
            Location loc = this.getLocation();
            Bounds offset = Constant.getOffsetBounds(attrs);
            this.setBounds(offset.translate(loc.getX(), loc.getY()));
            this.fireComponentInvalidated(new ComponentEvent(this));
        } else if (attr == value_attr) {
            this.fireComponentInvalidated(new ComponentEvent(this));
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Bounds bds = this.getBounds();
        ConstantAttributes attrs = (ConstantAttributes)this.getAttributeSet();
        Value v = attrs.value;
        Graphics g = context.getGraphics();
        if (context.shouldDrawColor()) {
            g.setColor(backgroundColor);
            g.fillRect(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        }
        if (v.getWidth() == 1) {
            if (context.shouldDrawColor()) {
                g.setColor(v.getColor());
            }
            GraphicsUtil.drawCenteredText(g, v.toString(), bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2 - 2);
        } else {
            g.setColor(Color.BLACK);
            GraphicsUtil.drawCenteredText(g, v.toHexString(), bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2 - 2);
        }
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == ExpressionComputer.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public void computeExpression(Map expressionMap) {
        ConstantAttributes attrs = (ConstantAttributes)this.getAttributeSet();
        expressionMap.put(this.getLocation(), Expressions.constant(attrs.value.toIntValue()));
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Constant";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("constantComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return new ConstantAttributes();
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new Constant(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            ConstantAttributes myAttrs = (ConstantAttributes)attrs;
            return Constant.getOffsetBounds(myAttrs);
        }

        @Override
        public void paintIcon(ComponentDrawContext c, int x, int y, AttributeSet attrs) {
            int w = ((BitWidth)attrs.getValue(Constant.width_attr)).getWidth();
            int pinx = x + 16;
            int piny = y + 9;
            Direction dir = (Direction)attrs.getValue(Constant.facing_attr);
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
            Graphics g = c.getGraphics();
            if (w == 1) {
                int v = (Integer)attrs.getValue(Constant.value_attr);
                g.setColor((v == 0 ? Value.FALSE : Value.TRUE).getColor());
                GraphicsUtil.drawCenteredText(g, "" + v, x + 10, y + 9);
            } else {
                g.setFont(g.getFont().deriveFont(9.0f));
                GraphicsUtil.drawCenteredText(g, "x" + w, x + 10, y + 9);
            }
            g.fillOval(pinx, piny, 3, 3);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            int v = (Integer)attrs.getValue(Constant.value_attr);
            String vStr = Integer.toHexString(v);
            Bounds bds = this.getOffsetBounds(attrs);
            Graphics g = context.getGraphics();
            GraphicsUtil.switchToWidth(g, 2);
            g.setColor(color);
            g.fillOval(x - 2, y - 2, 5, 5);
            GraphicsUtil.drawCenteredText(g, vStr, x + bds.getX() + bds.getWidth() / 2, y + bds.getY() + bds.getHeight() / 2);
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return Constant.facing_attr;
            }
            return super.getFeature(key, attrs);
        }
    }

    private static class ConstantAttributes
    extends AbstractAttributeSet {
        private Direction facing = Direction.EAST;
        private BitWidth width = BitWidth.ONE;
        private Value value = Value.TRUE;

        private ConstantAttributes() {
        }

        @Override
        protected void copyInto(AbstractAttributeSet destObj) {
            ConstantAttributes dest = (ConstantAttributes)destObj;
            dest.facing = this.facing;
            dest.width = this.width;
            dest.value = this.value;
        }

        @Override
        public List getAttributes() {
            return ATTRIBUTES;
        }

        @Override
        public Object getValue(Attribute attr) {
            if (attr == Constant.facing_attr) {
                return this.facing;
            }
            if (attr == Constant.width_attr) {
                return this.width;
            }
            if (attr == Constant.value_attr) {
                return IntegerFactory.create(this.value.toIntValue());
            }
            return null;
        }

        @Override
        public void setValue(Attribute attr, Object value) {
            if (attr == Constant.facing_attr) {
                this.facing = (Direction)value;
            } else if (attr == Constant.width_attr) {
                this.width = (BitWidth)value;
                this.value = this.value.extendWidth(this.width.getWidth(), this.value.get(this.value.getWidth() - 1));
            } else if (attr == Constant.value_attr) {
                int val = (Integer)value;
                this.value = Value.createKnown(this.width, val);
            } else {
                throw new IllegalArgumentException("unknown attribute");
            }
            this.fireAttributeValueChanged(attr, value);
        }
    }

}

