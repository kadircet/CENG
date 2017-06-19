/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.Pin;
import com.cburch.logisim.circuit.ProbeAttributes;
import com.cburch.logisim.circuit.ProbeFactory;
import com.cburch.logisim.circuit.RadixOption;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentEvent;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.comp.TextFieldCaret;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.comp.TextFieldListener;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.TextEditable;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;

class Probe
extends ManagedComponent
implements TextFieldListener,
TextEditable,
Loggable {
    private TextField field;
    private BitWidth width = BitWidth.ONE;

    public Probe(Location loc, AttributeSet attrs) {
        super(loc, attrs, 1);
        ProbeAttributes probeAttrs = (ProbeAttributes)attrs;
        probeAttrs.component = this;
        String text = probeAttrs.label;
        if (text != null && !text.equals("")) {
            this.createTextField();
        }
        this.setEnd(0, this.getLocation(), BitWidth.UNKNOWN, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return ProbeFactory.instance;
    }

    @Override
    public void propagate(CircuitState state) {
        Value newValue = state.getValue(this.getEndLocation(0));
        state.setData(this, newValue);
        if (newValue.getBitWidth() != this.width) {
            this.width = newValue.getBitWidth();
            ProbeAttributes attrs = (ProbeAttributes)this.getAttributeSet();
            Bounds bds = ProbeFactory.getOffsetBounds(attrs.facing, this.width, attrs.radix);
            Location loc = this.getLocation();
            this.setBounds(bds.translate(loc.getX(), loc.getY()));
            if (this.field != null) {
                this.createTextField();
            }
            this.fireComponentInvalidated(new ComponentEvent(this));
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

    public Direction getDirection() {
        ProbeAttributes attrs = (ProbeAttributes)this.getAttributeSet();
        return attrs.facing;
    }

    public String getLabel() {
        return this.field == null ? null : this.field.getText();
    }

    Value getValue(CircuitState state) {
        Value ret = (Value)state.getData(this);
        return ret == null ? Value.NIL : ret;
    }

    @Override
    public void draw(ComponentDrawContext context) {
        CircuitState circuitState = context.getCircuitState();
        Value value = circuitState == null ? Value.NIL : this.getValue(circuitState);
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        int x = bds.getX();
        int y = bds.getY();
        g.setColor(Color.WHITE);
        g.fillRect(x + 5, y + 5, bds.getWidth() - 10, bds.getHeight() - 10);
        g.setColor(Color.GRAY);
        if (value.getWidth() <= 1) {
            g.drawOval(x + 1, y + 1, bds.getWidth() - 2, bds.getHeight() - 2);
        } else {
            g.drawRoundRect(x + 1, y + 1, bds.getWidth() - 2, bds.getHeight() - 2, 6, 6);
        }
        g.setColor(Color.BLACK);
        if (this.field != null) {
            this.field.draw(g);
        }
        if (!context.getShowState()) {
            if (value.getWidth() > 0) {
                GraphicsUtil.drawCenteredText(g, "x" + value.getWidth(), bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
            }
        } else {
            this.drawValue(context, value);
        }
        context.drawPins(this);
    }

    void drawValue(ComponentDrawContext context, Value value) {
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        RadixOption radix = (RadixOption)this.getAttributeSet().getValue(RadixOption.ATTRIBUTE);
        if (radix == null || radix == RadixOption.RADIX_2) {
            int x = bds.getX();
            int y = bds.getY();
            int wid = value.getWidth();
            if (wid == 0) {
                GraphicsUtil.switchToWidth(g, 2);
                g.drawLine(x - 4, y, (x += bds.getWidth() / 2) + 4, y += bds.getHeight() / 2);
                return;
            }
            int x0 = bds.getX() + bds.getWidth() - 5;
            int compWidth = wid * 10;
            if (compWidth < bds.getWidth() - 3) {
                x0 = bds.getX() + (bds.getWidth() + compWidth) / 2 - 5;
            }
            int cx = x0;
            int cy = bds.getY() + bds.getHeight() - 12;
            int cur = 0;
            for (int k = 0; k < wid; ++k) {
                GraphicsUtil.drawCenteredText(g, value.get(k).toDisplayString(), cx, cy);
                if (++cur == 8) {
                    cur = 0;
                    cx = x0;
                    cy -= 20;
                    continue;
                }
                cx -= 10;
            }
        } else {
            String text = radix.toString(value);
            GraphicsUtil.drawCenteredText(g, text, bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
        }
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
        ProbeAttributes attrs = (ProbeAttributes)this.getAttributeSet();
        String ret = attrs.label;
        if (ret == null || ret.equals("")) {
            return null;
        }
        return ret;
    }

    @Override
    public Value getLogValue(CircuitState state, Object option) {
        return this.getValue(state);
    }

    TextField getTextField() {
        return this.field;
    }

    void createTextField() {
        int x;
        int valign;
        int halign;
        int y;
        ProbeAttributes attrs = (ProbeAttributes)this.getAttributeSet();
        Direction labelloc = attrs.labelloc;
        Bounds bds = this.getBounds();
        if (labelloc == Direction.NORTH) {
            halign = 0;
            valign = 2;
            x = bds.getX() + bds.getWidth() / 2;
            y = bds.getY() - 2;
            if (attrs.facing == labelloc) {
                halign = -1;
                x += 2;
            }
        } else if (labelloc == Direction.SOUTH) {
            halign = 0;
            valign = -1;
            x = bds.getX() + bds.getWidth() / 2;
            y = bds.getY() + bds.getHeight() + 2;
            if (attrs.facing == labelloc) {
                halign = -1;
                x += 2;
            }
        } else if (labelloc == Direction.EAST) {
            halign = -1;
            valign = 0;
            x = bds.getX() + bds.getWidth() + 2;
            y = bds.getY() + bds.getHeight() / 2;
            if (attrs.facing == labelloc) {
                valign = 2;
                y -= 2;
            }
        } else {
            halign = 1;
            valign = 0;
            x = bds.getX() - 2;
            y = bds.getY() + bds.getHeight() / 2;
            if (attrs.facing == labelloc) {
                valign = 2;
                y -= 2;
            }
        }
        if (this.field == null) {
            this.field = new TextField(x, y, halign, valign, attrs.labelfont);
            this.field.addTextFieldListener(this);
        } else {
            this.field.setLocation(x, y, halign, valign);
            this.field.setFont(attrs.labelfont);
        }
        String text = attrs.label;
        this.field.setText(text == null ? "" : text);
    }

    void attributeValueChanged(ProbeAttributes attrs, Attribute attr, Object value) {
        if (attr == Pin.label_attr) {
            String val = (String)value;
            if (val == null || val.equals("")) {
                this.field = null;
            } else if (this.field == null) {
                this.createTextField();
            } else {
                this.field.setText(val);
            }
            this.fireComponentInvalidated(new ComponentEvent(this, null, value));
        } else if (attr == Pin.labelloc_attr) {
            if (this.field != null) {
                this.createTextField();
            }
        } else if (attr == Pin.labelfont_attr) {
            if (this.field != null) {
                this.createTextField();
            }
        } else if (attr == Pin.facing_attr || attr == RadixOption.ATTRIBUTE) {
            Location loc = this.getLocation();
            Bounds offs = ProbeFactory.getOffsetBounds(attrs.facing, this.width, attrs.radix);
            this.setBounds(offs.translate(loc.getX(), loc.getY()));
            if (this.field != null) {
                this.createTextField();
            }
        }
    }

    @Override
    public void textChanged(TextFieldEvent e) {
        ProbeAttributes attrs = (ProbeAttributes)this.getAttributeSet();
        String next = e.getText();
        if (!attrs.label.equals(next)) {
            attrs.setValue(Pin.label_attr, next);
        }
    }
}

