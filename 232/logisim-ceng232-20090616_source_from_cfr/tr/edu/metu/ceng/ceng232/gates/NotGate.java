/*
 * Decompiled with CFR 0_114.
 */
package tr.edu.metu.ceng.ceng232.gates;

import com.cburch.logisim.analyze.model.Expression;
import com.cburch.logisim.analyze.model.Expressions;
import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.ExpressionComputer;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import com.cburch.logisim.util.IntegerFactory;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Component;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.util.Map;
import javax.swing.Icon;
import tr.edu.metu.ceng.ceng232.gates.DinShape;
import tr.edu.metu.ceng.ceng232.gates.GateAttributes;
import tr.edu.metu.ceng.ceng232.gates.Strings;

class NotGate
extends ManagedComponent
implements AttributeListener,
ExpressionComputer {
    public static ComponentFactory factory = new Factory();
    public static final AttributeOption SIZE_NARROW = new AttributeOption(IntegerFactory.create(20), Strings.getter("gateSizeNarrowOpt"));
    public static final AttributeOption SIZE_WIDE = new AttributeOption(IntegerFactory.create(30), Strings.getter("gateSizeWideOpt"));
    public static final Attribute size_attr = Attributes.forOption("size", Strings.getter("gateSizeAttr"), new AttributeOption[]{SIZE_NARROW, SIZE_WIDE});
    private static final Attribute[] ATTRIBUTES = new Attribute[]{GateAttributes.facing_attr, GateAttributes.width_attr, size_attr};
    private static final Object[] DEFAULTS = new Object[]{Direction.EAST, BitWidth.ONE, SIZE_WIDE};
    private static final String RECT_LABEL = "1";
    private static final Icon toolIcon = Icons.getIcon("notGate.gif");
    private static final Icon toolIconRect = Icons.getIcon("notGateRect.gif");
    private static final Icon toolIconDin = Icons.getIcon("dinNotGate.gif");

    public NotGate(Location loc, AttributeSet attrs) {
        super(loc, attrs, 2);
        attrs.setReadOnly(size_attr, true);
        attrs.setReadOnly(GateAttributes.facing_attr, true);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        AttributeSet attrs = this.getAttributeSet();
        Direction dir = (Direction)attrs.getValue(GateAttributes.facing_attr);
        BitWidth w = (BitWidth)attrs.getValue(GateAttributes.width_attr);
        Object size = attrs.getValue(size_attr);
        Location loc0 = this.getLocation();
        Location loc1 = loc0.translate(dir.reverse(), size == SIZE_NARROW ? 20 : 30);
        this.setEnd(0, loc0, w, 2);
        this.setEnd(1, loc1, w, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value in = state.getValue(this.getEndLocation(1));
        state.setValue(this.getEndLocation(0), in.not(), this, 1);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == GateAttributes.width_attr) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        Location loc = this.getLocation();
        int x = loc.getX();
        int y = loc.getY();
        g.setColor(Color.BLACK);
        NotGate.drawBase(context, x, y, this.getAttributeSet());
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
        Expression e = (Expression)expressionMap.get(this.getEndLocation(1));
        if (e != null) {
            expressionMap.put(this.getEndLocation(0), Expressions.not(e));
        }
    }

    private static void drawBase(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Object shape;
        Graphics oldG = context.getGraphics();
        Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
        Graphics g = oldG;
        if (facing != Direction.EAST && oldG instanceof Graphics2D) {
            Graphics2D g2 = (Graphics2D)g.create();
            g2.rotate(- facing.toRadians(), x, y);
            g = g2;
        }
        if ((shape = context.getGateShape()) == "rectangular") {
            NotGate.drawRectangularBase(g, x, y, attrs);
        } else if (shape == "din40700") {
            int width = attrs.getValue(size_attr) == SIZE_NARROW ? 20 : 30;
            DinShape.draw(context, x, y, width, 18, true, 0);
        } else {
            GraphicsUtil.switchToWidth(g, 2);
            if (attrs.getValue(size_attr) == SIZE_NARROW) {
                GraphicsUtil.switchToWidth(g, 2);
                int[] xp = new int[4];
                int[] yp = new int[4];
                xp[0] = x - 6;
                yp[0] = y;
                xp[1] = x - 19;
                yp[1] = y - 6;
                xp[2] = x - 19;
                yp[2] = y + 6;
                xp[3] = x - 6;
                yp[3] = y;
                g.drawPolyline(xp, yp, 4);
                g.drawOval(x - 6, y - 3, 6, 6);
            } else {
                int[] xp = new int[4];
                int[] yp = new int[4];
                xp[0] = x - 10;
                yp[0] = y;
                xp[1] = x - 29;
                yp[1] = y - 7;
                xp[2] = x - 29;
                yp[2] = y + 7;
                xp[3] = x - 10;
                yp[3] = y;
                g.drawPolyline(xp, yp, 4);
                g.drawOval(x - 9, y - 4, 9, 9);
            }
        }
    }

    private static void drawRectangularBase(Graphics g, int x, int y, AttributeSet attrs) {
        GraphicsUtil.switchToWidth(g, 2);
        if (attrs.getValue(size_attr) == SIZE_NARROW) {
            g.drawRect(x - 20, y - 9, 14, 18);
            GraphicsUtil.drawCenteredText(g, "1", x - 13, y);
            g.drawOval(x - 6, y - 3, 6, 6);
        } else {
            g.drawRect(x - 30, y - 9, 20, 18);
            GraphicsUtil.drawCenteredText(g, "1", x - 20, y);
            g.drawOval(x - 10, y - 5, 9, 9);
        }
        GraphicsUtil.switchToWidth(g, 1);
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "NOT Gate";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("notGateComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new NotGate(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Object value = attrs.getValue(NotGate.size_attr);
            if (value == NotGate.SIZE_NARROW) {
                Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
                if (facing == Direction.SOUTH) {
                    return Bounds.create(-9, -20, 18, 20);
                }
                if (facing == Direction.NORTH) {
                    return Bounds.create(-9, 0, 18, 20);
                }
                if (facing == Direction.WEST) {
                    return Bounds.create(0, -9, 20, 18);
                }
                return Bounds.create(-20, -9, 20, 18);
            }
            Direction facing = (Direction)attrs.getValue(GateAttributes.facing_attr);
            if (facing == Direction.SOUTH) {
                return Bounds.create(-9, -30, 18, 30);
            }
            if (facing == Direction.NORTH) {
                return Bounds.create(-9, 0, 18, 30);
            }
            if (facing == Direction.WEST) {
                return Bounds.create(0, -9, 30, 18);
            }
            return Bounds.create(-30, -9, 30, 18);
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            g.setColor(color);
            NotGate.drawBase(context, x, y, attrs);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            g.setColor(Color.black);
            if (context.getGateShape() == "rectangular") {
                if (toolIconRect != null) {
                    toolIconRect.paintIcon(context.getDestination(), g, x + 2, y + 2);
                } else {
                    g.drawRect(x, y + 2, 16, 16);
                    GraphicsUtil.drawCenteredText(g, "1", x + 8, y + 8);
                    g.drawOval(x + 16, y + 8, 4, 4);
                }
            } else if (context.getGateShape() == "din40700") {
                if (toolIconDin != null) {
                    toolIconDin.paintIcon(context.getDestination(), g, x + 2, y + 2);
                } else {
                    g.drawRect(x, y + 2, 16, 16);
                    GraphicsUtil.drawCenteredText(g, "1", x + 8, y + 8);
                    g.drawOval(x + 16, y + 8, 4, 4);
                }
            } else if (toolIcon != null) {
                toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            } else {
                int[] xp = new int[4];
                int[] yp = new int[4];
                xp[0] = x + 15;
                yp[0] = y + 10;
                xp[1] = x + 1;
                yp[1] = y + 3;
                xp[2] = x + 1;
                yp[2] = y + 17;
                xp[3] = x + 15;
                yp[3] = y + 10;
                g.drawPolyline(xp, yp, 4);
                g.drawOval(x + 15, y + 8, 4, 4);
            }
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return GateAttributes.facing_attr;
            }
            return super.getFeature(key, attrs);
        }
    }

}

