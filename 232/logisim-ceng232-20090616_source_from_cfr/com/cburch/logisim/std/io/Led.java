/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.io;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.comp.TextFieldCaret;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.comp.TextFieldListener;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeEvent;
import com.cburch.logisim.data.AttributeListener;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.std.io.Io;
import com.cburch.logisim.std.io.Strings;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.TextEditable;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import javax.swing.Icon;

class Led
extends ManagedComponent
implements AttributeListener,
TextFieldListener,
TextEditable,
Loggable {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Io.ATTR_FACING, Io.ATTR_COLOR, Io.ATTR_LABEL, Io.ATTR_LABEL_LOC, Io.ATTR_LABEL_FONT, Io.ATTR_LABEL_COLOR};
    private static final Object[] DEFAULTS = new Object[]{Direction.WEST, new Color(240, 0, 0), "", Io.LABEL_CENTER, Io.DEFAULT_LABEL_FONT, Color.black};
    private static final Icon toolIcon = Icons.getIcon("led.gif");
    private TextField field;

    private Led(Location loc, AttributeSet attrs) {
        super(loc, attrs, 1);
        this.setEnd(0, this.getLocation(), BitWidth.ONE, 1);
        attrs.addAttributeListener(this);
        String text = (String)attrs.getValue(Io.ATTR_LABEL);
        if (text != null && !text.equals("")) {
            this.createTextField();
        }
    }

    private void createTextField() {
        AttributeSet attrs = this.getAttributeSet();
        Direction facing = (Direction)attrs.getValue(Io.ATTR_FACING);
        Object labelLoc = attrs.getValue(Io.ATTR_LABEL_LOC);
        Bounds bds = this.getBounds();
        int x = bds.getX() + bds.getWidth() / 2;
        int y = bds.getY() + bds.getHeight() / 2;
        int halign = 0;
        int valign = 0;
        if (labelLoc == Direction.NORTH) {
            y = bds.getY() - 2;
            valign = 2;
        } else if (labelLoc == Direction.SOUTH) {
            y = bds.getY() + bds.getHeight() + 2;
            valign = -1;
        } else if (labelLoc == Direction.EAST) {
            x = bds.getX() + bds.getWidth() + 2;
            halign = -1;
        } else if (labelLoc == Direction.WEST) {
            x = bds.getX() - 2;
            halign = 1;
        }
        if (labelLoc == facing) {
            if (labelLoc == Direction.NORTH || labelLoc == Direction.SOUTH) {
                x += 2;
                halign = -1;
            } else {
                y -= 2;
                valign = 2;
            }
        }
        if (this.field == null) {
            this.field = new TextField(x, y, halign, valign, (Font)attrs.getValue(Io.ATTR_LABEL_FONT));
            this.field.addTextFieldListener(this);
        } else {
            this.field.setLocation(x, y, halign, valign);
            this.field.setFont((Font)attrs.getValue(Io.ATTR_LABEL_FONT));
        }
        String text = (String)attrs.getValue(Io.ATTR_LABEL);
        this.field.setText(text == null ? "" : text);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value val = state.getValue(this.getEndLocation(0));
        state.setData(this, val);
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == Io.ATTR_FACING) {
            Location loc = this.getLocation();
            this.setBounds(this.getFactory().getOffsetBounds(this.getAttributeSet()).translate(loc.getX(), loc.getY()));
            if (this.field != null) {
                this.createTextField();
            }
        } else if (attr == Io.ATTR_LABEL) {
            String val = (String)e.getValue();
            if (val == null || val.equals("")) {
                this.field = null;
            } else if (this.field == null) {
                this.createTextField();
            } else {
                this.field.setText(val);
            }
        } else if (attr == Io.ATTR_LABEL_LOC) {
            if (this.field != null) {
                this.createTextField();
            }
        } else if (attr == Io.ATTR_LABEL_FONT && this.field != null) {
            this.createTextField();
        }
    }

    @Override
    public void textChanged(TextFieldEvent e) {
        String next;
        AttributeSet attrs = this.getAttributeSet();
        String prev = (String)attrs.getValue(Io.ATTR_LABEL);
        if (!prev.equals(next = e.getText())) {
            attrs.setValue(Io.ATTR_LABEL, next);
        }
    }

    @Override
    public Bounds getBounds(Graphics g) {
        Bounds ret = super.getBounds();
        if (this.field != null) {
            ret = ret.add(this.field.getBounds(g));
        }
        return ret;
    }

    @Override
    public boolean contains(Location pt, Graphics g) {
        return super.contains(pt) || this.field != null && this.field.getBounds(g).contains(pt);
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Value val = (Value)context.getCircuitState().getData(this);
        Color color = (Color)this.getAttributeSet().getValue(Io.ATTR_COLOR);
        Bounds bds = this.getBounds().expand(-1);
        Graphics g = context.getGraphics();
        if (context.getShowState()) {
            g.setColor(val == Value.TRUE ? color : Color.darkGray);
            g.fillOval(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        }
        g.setColor(Color.BLACK);
        GraphicsUtil.switchToWidth(g, 2);
        g.drawOval(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight());
        GraphicsUtil.switchToWidth(g, 1);
        if (this.field != null) {
            g.setColor((Color)this.getAttributeSet().getValue(Io.ATTR_LABEL_COLOR));
            this.field.draw(g);
        }
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Loggable.class) {
            return this;
        }
        if (key == TextEditable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getTextCaret(ComponentUserEvent event) {
        int x;
        int y;
        Graphics g = event.getCanvas().getGraphics();
        if (this.field == null) {
            this.createTextField();
            return this.field.getCaret(g, 0);
        }
        Bounds bds = this.field.getBounds(g);
        if (bds.getWidth() < 4 || bds.getHeight() < 4) {
            Location loc = this.getLocation();
            bds = bds.add(Bounds.create(loc).expand(2));
        }
        if (bds.contains(x = event.getX(), y = event.getY())) {
            return this.field.getCaret(g, x, y);
        }
        return null;
    }

    @Override
    public Object[] getLogOptions(CircuitState state) {
        return null;
    }

    @Override
    public String getLogName(Object option) {
        return (String)this.getAttributeSet().getValue(Io.ATTR_LABEL);
    }

    @Override
    public Value getLogValue(CircuitState state, Object option) {
        Value ret = (Value)state.getData(this);
        return ret == Value.TRUE ? Value.TRUE : Value.FALSE;
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "LED";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("ledComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Led(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Io.ATTR_FACING);
            return Bounds.create(0, -10, 20, 20).rotate(Direction.WEST, facing, 0, 0);
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            if (toolIcon != null) {
                toolIcon.paintIcon(context.getDestination(), g, x + 2, y + 2);
            }
        }

        @Override
        public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            Bounds bds = this.getOffsetBounds(attrs);
            GraphicsUtil.switchToWidth(g, 2);
            g.setColor(color);
            g.drawOval(x + bds.getX() + 1, y + bds.getY() + 1, bds.getWidth() - 2, bds.getHeight() - 2);
        }

        @Override
        public Object getFeature(Object key, AttributeSet attrs) {
            if (key == FACING_ATTRIBUTE_KEY) {
                return Io.ATTR_FACING;
            }
            return super.getFeature(key, attrs);
        }
    }

}

