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
import com.cburch.logisim.tools.AbstractCaret;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.TextEditable;
import com.cburch.logisim.util.Icons;
import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import javax.swing.Icon;

class Button
extends ManagedComponent
implements AttributeListener,
TextFieldListener,
Pokable,
TextEditable,
Loggable {
    public static final ComponentFactory factory = new Factory();
    private static final Attribute[] ATTRIBUTES = new Attribute[]{Io.ATTR_FACING, Io.ATTR_COLOR, Io.ATTR_LABEL, Io.ATTR_LABEL_LOC, Io.ATTR_LABEL_FONT, Io.ATTR_LABEL_COLOR};
    private static final Object[] DEFAULTS = new Object[]{Direction.EAST, Color.white, "", Io.LABEL_CENTER, Io.DEFAULT_LABEL_FONT, Color.black};
    private static final Icon toolIcon = Icons.getIcon("button.gif");
    private static final int DEPTH = 3;
    private TextField field;

    private Button(Location loc, AttributeSet attrs) {
        super(loc, attrs, 1);
        this.setEnd(0, this.getLocation(), BitWidth.ONE, 2);
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
        if (labelLoc == Io.LABEL_CENTER) {
            x = bds.getX() + (bds.getWidth() - 3) / 2;
            y = bds.getY() + (bds.getHeight() - 3) / 2;
        } else if (labelLoc == Direction.NORTH) {
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
        Value val = (Value)state.getData(this);
        if (val == null) {
            val = Value.FALSE;
        }
        state.setValue(this.getLocation(), val, this, 1);
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
        int depress;
        Bounds bds = this.getBounds();
        int x = bds.getX();
        int y = bds.getY();
        int w = bds.getWidth();
        int h = bds.getHeight();
        Value val = context.getShowState() ? (Value)context.getCircuitState().getData(this) : Value.FALSE;
        Color color = (Color)this.getAttributeSet().getValue(Io.ATTR_COLOR);
        Graphics g = context.getGraphics();
        if (val == Value.TRUE) {
            depress = 3;
            g.setColor(color);
            g.fillRect(x += 3, y += 3, w - 3, h - 3);
            g.setColor(Color.black);
            g.drawRect(x, y, w - 3, h - 3);
        } else {
            depress = 0;
            int[] xp = new int[]{x, x + w - 3, x + w, x + w, x + 3, x};
            int[] yp = new int[]{y, y, y + 3, y + h, y + h, y + h - 3};
            g.setColor(color.darker());
            g.fillPolygon(xp, yp, xp.length);
            g.setColor(color);
            g.fillRect(x, y, w - 3, h - 3);
            g.setColor(Color.black);
            g.drawRect(x, y, w - 3, h - 3);
            g.drawLine(x + w - 3, y + h - 3, x + w, y + h);
            g.drawPolygon(xp, yp, xp.length);
        }
        if (this.field != null) {
            g.setColor((Color)this.getAttributeSet().getValue(Io.ATTR_LABEL_COLOR));
            g.translate(depress, depress);
            this.field.draw(g);
            g.translate(- depress, - depress);
        }
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Pokable.class) {
            return this;
        }
        if (key == TextEditable.class) {
            return this;
        }
        if (key == Loggable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        Bounds bds = this.getBounds();
        CircuitState circState = event.getCircuitState();
        PokeCaret ret = new PokeCaret(circState);
        ret.setBounds(bds);
        ret.mousePressed(null);
        return ret;
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
        return ret == null ? Value.FALSE : ret;
    }

    private class PokeCaret
    extends AbstractCaret {
        CircuitState circState;

        PokeCaret(CircuitState circState) {
            this.circState = circState;
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.circState.setData(Button.this, Value.TRUE);
            this.circState.setValue(Button.this.getLocation(), Value.TRUE, Button.this, 1);
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            this.circState.setData(Button.this, Value.FALSE);
            this.circState.setValue(Button.this.getLocation(), Value.FALSE, Button.this, 1);
        }
    }

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Button";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("buttonComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public com.cburch.logisim.comp.Component createComponent(Location loc, AttributeSet attrs) {
            return new Button(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            Direction facing = (Direction)attrs.getValue(Io.ATTR_FACING);
            return Bounds.create(-20, -10, 20, 20).rotate(Direction.EAST, facing, 0, 0);
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
                return Io.ATTR_FACING;
            }
            return super.getFeature(key, attrs);
        }
    }

}

