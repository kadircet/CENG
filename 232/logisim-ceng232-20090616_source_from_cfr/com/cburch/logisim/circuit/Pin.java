/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.circuit;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.circuit.PinAttributes;
import com.cburch.logisim.circuit.PinFactory;
import com.cburch.logisim.circuit.Probe;
import com.cburch.logisim.circuit.ProbeAttributes;
import com.cburch.logisim.circuit.Strings;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.TextField;
import com.cburch.logisim.comp.TextFieldEvent;
import com.cburch.logisim.data.Attribute;
import com.cburch.logisim.data.AttributeOption;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.Attributes;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.main.Canvas;
import com.cburch.logisim.gui.main.Frame;
import com.cburch.logisim.proj.Project;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretListener;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import javax.swing.JOptionPane;

public class Pin
extends Probe
implements Pokable {
    public static final Attribute facing_attr = Attributes.forDirection("facing", Strings.getter("pinFacingAttr"));
    public static final Attribute threeState_attr = Attributes.forBoolean("tristate", Strings.getter("pinThreeStateAttr"));
    public static final Attribute width_attr = Attributes.forBitWidth("width", Strings.getter("pinBitWidthAttr"));
    public static final Attribute type_attr = Attributes.forBoolean("output", Strings.getter("pinOutputAttr"));
    public static final Attribute label_attr = Attributes.forString("label", Strings.getter("pinLabelAttr"));
    public static final Attribute labelloc_attr = Attributes.forDirection("labelloc", Strings.getter("pinLabelLocAttr"));
    public static final Attribute labelfont_attr = Attributes.forFont("labelfont", Strings.getter("pinLabelFontAttr"));
    public static final Object pull_none = new AttributeOption("none", Strings.getter("pinPullNoneOption"));
    public static final Object pull_up = new AttributeOption("up", Strings.getter("pinPullUpOption"));
    public static final Object pull_down = new AttributeOption("down", Strings.getter("pinPullDownOption"));
    private static final Object[] pull_options = new Object[]{pull_none, pull_up, pull_down};
    public static final Attribute pull_attr = Attributes.forOption("pull", Strings.getter("pinPullAttr"), pull_options);

    public Pin(Location loc, AttributeSet attrs) {
        super(loc, attrs);
        this.setEnd((PinAttributes)attrs);
    }

    private void setEnd(PinAttributes attrs) {
        int endType = attrs.type;
        if (attrs.type == 2) {
            endType = 1;
        } else if (attrs.type == 1) {
            endType = 2;
        }
        this.setEnd(0, this.getLocation(), attrs.width, endType);
    }

    @Override
    public ComponentFactory getFactory() {
        return PinFactory.instance;
    }

    @Override
    public void propagate(CircuitState state) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        Location pt = this.getEndLocation(0);
        Value val = state.getValue(pt);
        State q = this.getState(state);
        if (attrs.type == 2) {
            q.sending = val;
            q.receiving = val;
            state.setValue(pt, Value.createUnknown(attrs.width), this, 1);
        } else {
            q.receiving = val;
            if (!val.equals(q.sending)) {
                state.setValue(pt, q.sending, this, 1);
            }
        }
    }

    public BitWidth getWidth() {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        return attrs.width;
    }

    public int getType() {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        return attrs.type;
    }

    public boolean isInputPin() {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        return attrs.type != 2;
    }

    @Override
    Value getValue(CircuitState state) {
        return this.getState((CircuitState)state).sending;
    }

    public void changeValue(CircuitState state, Value value) {
        this.setValue(state, value);
        State myState = this.getState(state);
        state.setValue(this.getLocation(), myState.sending, this, 1);
    }

    void setValue(CircuitState state, Value value) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        Object pull = attrs.pull;
        if (pull != pull_none && pull != null && !value.isFullyDefined()) {
            Value[] bits = value.getAll();
            if (pull == pull_up) {
                for (int i = 0; i < bits.length; ++i) {
                    if (bits[i] == Value.FALSE) continue;
                    bits[i] = Value.TRUE;
                }
            } else if (pull == pull_down) {
                for (int i = 0; i < bits.length; ++i) {
                    if (bits[i] == Value.TRUE) continue;
                    bits[i] = Value.FALSE;
                }
            }
            value = Value.create(bits);
        }
        State myState = this.getState(state);
        myState.sending = value == Value.NIL ? Value.createUnknown(attrs.width) : value;
    }

    private State getState(CircuitState state) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        BitWidth width = attrs.width;
        State ret = (State)state.getData(this);
        if (ret == null) {
            Value val;
            ret = new State();
            Value value = val = attrs.threeState ? Value.UNKNOWN : Value.FALSE;
            if (width.getWidth() > 1) {
                Object[] arr = new Value[width.getWidth()];
                Arrays.fill(arr, val);
                val = Value.create((Value[])arr);
            }
            ret.sending = val;
            ret.receiving = val;
            state.setData(this, ret);
        }
        if (ret.sending.getWidth() != width.getWidth()) {
            ret.sending = ret.sending.extendWidth(width.getWidth(), attrs.threeState ? Value.UNKNOWN : Value.FALSE);
        }
        if (ret.receiving.getWidth() != width.getWidth()) {
            ret.receiving = ret.receiving.extendWidth(width.getWidth(), Value.UNKNOWN);
        }
        return ret;
    }

    @Override
    public void draw(ComponentDrawContext context) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        int x = bds.getX();
        int y = bds.getY();
        GraphicsUtil.switchToWidth(g, 2);
        g.setColor(Color.black);
        if (attrs.type == 2) {
            if (attrs.width.getWidth() == 1) {
                g.drawOval(x + 1, y + 1, bds.getWidth() - 1, bds.getHeight() - 1);
            } else {
                g.drawRoundRect(x + 1, y + 1, bds.getWidth() - 1, bds.getHeight() - 1, 6, 6);
            }
        } else {
            g.drawRect(x + 1, y + 1, bds.getWidth() - 1, bds.getHeight() - 1);
        }
        TextField field = this.getTextField();
        if (field != null) {
            field.draw(g);
        }
        if (!context.getShowState()) {
            g.setColor(Color.black);
            GraphicsUtil.drawCenteredText(g, "x" + attrs.width.getWidth(), bds.getX() + bds.getWidth() / 2, bds.getY() + bds.getHeight() / 2);
        } else {
            CircuitState circ_state = context.getCircuitState();
            State state = this.getState(circ_state);
            if (attrs.width.getWidth() <= 1) {
                Value receiving = state.receiving;
                g.setColor(receiving.getColor());
                g.fillOval(x + 4, y + 4, 13, 13);
                if (attrs.width.getWidth() == 1) {
                    g.setColor(Color.white);
                    GraphicsUtil.drawCenteredText(g, state.sending.toDisplayString(), x + 11, y + 9);
                }
            } else {
                this.drawValue(context, state.sending);
            }
        }
        context.drawPins(this);
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Pokable.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        if (this.getBounds().contains(event.getX(), event.getY())) {
            return new PokeCaret(this, event.getCanvas());
        }
        return null;
    }

    @Override
    public String getLogName(Object option) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        String ret = attrs.label;
        if (ret == null || ret.equals("")) {
            String type = attrs.type == 1 ? Strings.get("pinInputName") : Strings.get("pinOutputName");
            return type + this.getLocation();
        }
        return ret;
    }

    @Override
    public Value getLogValue(CircuitState state, Object option) {
        State s = this.getState(state);
        return s.sending;
    }

    @Override
    void attributeValueChanged(ProbeAttributes probeAttrs, Attribute attr, Object value) {
        PinAttributes attrs = (PinAttributes)probeAttrs;
        if (attr == width_attr || attr == type_attr) {
            this.setEnd(attrs);
        } else {
            super.attributeValueChanged(attrs, attr, value);
        }
        if (attr == width_attr || attr == facing_attr) {
            Location loc = this.getLocation();
            this.setBounds(PinFactory.instance.getOffsetBounds(attrs).translate(loc.getX(), loc.getY()));
            if (this.getTextField() != null) {
                this.createTextField();
            }
        }
    }

    @Override
    public void textChanged(TextFieldEvent e) {
        PinAttributes attrs = (PinAttributes)this.getAttributeSet();
        String next = e.getText();
        if (!attrs.label.equals(next)) {
            attrs.setValue(label_attr, next);
        }
    }

    private static class PokeCaret
    implements Caret {
        Pin pin;
        Canvas canvas;
        int bit_pressed = -1;

        PokeCaret(Pin pin, Canvas canvas) {
            this.pin = pin;
            this.canvas = canvas;
        }

        @Override
        public void addCaretListener(CaretListener e) {
        }

        @Override
        public void removeCaretListener(CaretListener e) {
        }

        @Override
        public String getText() {
            return "";
        }

        @Override
        public Bounds getBounds(Graphics g) {
            return this.pin.getBounds();
        }

        @Override
        public void draw(Graphics g) {
        }

        @Override
        public void commitText(String text) {
        }

        @Override
        public void cancelEditing() {
        }

        @Override
        public void stopEditing() {
        }

        @Override
        public void mousePressed(MouseEvent e) {
            this.bit_pressed = this.getBit(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (!this.pin.isInputPin()) {
                return;
            }
            int bit = this.getBit(e.getX(), e.getY());
            if (bit < 0 || bit != this.bit_pressed) {
                this.bit_pressed = -1;
                return;
            }
            this.bit_pressed = -1;
            CircuitState state = this.canvas.getCircuitState();
            if (state.isSubstate()) {
                int choice = JOptionPane.showConfirmDialog(this.canvas.getProject().getFrame(), Strings.get("pinFrozenQuestion"), Strings.get("pinFrozenTitle"), 2, 2);
                if (choice != 0) {
                    return;
                }
                state = state.cloneState();
                this.canvas.getProject().setCircuitState(state);
            }
            State pin_state = this.pin.getState(state);
            Value val = pin_state.sending.get(bit);
            if (val == Value.FALSE) {
                val = Value.TRUE;
            } else if (val == Value.TRUE) {
                PinAttributes attrs = (PinAttributes)this.pin.getAttributeSet();
                val = attrs.threeState ? Value.UNKNOWN : Value.FALSE;
            } else {
                val = Value.FALSE;
            }
            pin_state.sending = pin_state.sending.set(bit, val);
            state.setValue(this.pin.getLocation(), pin_state.sending, this.pin, 1);
        }

        @Override
        public void keyPressed(KeyEvent e) {
        }

        @Override
        public void keyReleased(KeyEvent e) {
        }

        @Override
        public void keyTyped(KeyEvent e) {
        }

        private int getBit(int x, int y) {
            BitWidth width = this.pin.getWidth();
            if (width.getWidth() == 1) {
                return 0;
            }
            Bounds bds = this.pin.getBounds();
            int i = (bds.getX() + bds.getWidth() - x) / 10;
            int j = (bds.getY() + bds.getHeight() - y) / 20;
            int which = 8 * j + i;
            if (which < 0 || which >= width.getWidth()) {
                return -1;
            }
            return which;
        }
    }

    private static class State
    implements ComponentState,
    Cloneable {
        Value sending;
        Value receiving;

        private State() {
        }

        @Override
        public Object clone() {
            try {
                return super.clone();
            }
            catch (CloneNotSupportedException e) {
                return null;
            }
        }
    }

}

