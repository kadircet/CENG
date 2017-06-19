/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.std.memory;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
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
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.tools.AbstractCaret;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

class Register
extends ManagedComponent
implements AttributeListener,
Pokable,
ToolTipMaker,
Loggable {
    public static final Attribute width_attr = Attributes.forBitWidth("width", Strings.getter("registerWidthAttr"), 1, 32);
    public static ComponentFactory factory = new Factory();
    private static Attribute[] ATTRIBUTES = new Attribute[]{width_attr};
    private static Object[] DEFAULTS = new Object[]{BitWidth.create(8)};
    private static final Bounds OFFSET_BOUNDS = Bounds.create(-30, -20, 30, 40);
    private static final int DELAY = 8;
    private static final int OUT = 0;
    private static final int IN = 1;
    private static final int CK = 2;
    private static final int CLR = 3;
    private BitWidth dataWidth;

    public Register(Location loc, AttributeSet attrs) {
        super(loc, attrs, 4);
        attrs.addAttributeListener(this);
        this.setPins();
    }

    private void setPins() {
        Location loc = this.getLocation();
        this.dataWidth = (BitWidth)this.getAttributeSet().getValue(width_attr);
        this.setEnd(0, loc, this.dataWidth, 2);
        this.setEnd(1, loc.translate(-30, 0), this.dataWidth, 1);
        this.setEnd(2, loc.translate(-20, 20), BitWidth.ONE, 1);
        this.setEnd(3, loc.translate(-10, 20), BitWidth.ONE, 1);
    }

    @Override
    public ComponentFactory getFactory() {
        return factory;
    }

    @Override
    public void propagate(CircuitState state) {
        Value inValue;
        State myState = this.getState(state);
        Value ckValue = state.getValue(this.getEndLocation(2));
        Value clrValue = state.getValue(this.getEndLocation(3));
        if (clrValue == Value.TRUE) {
            myState.value = 0;
        } else if (myState.lastClock == Value.FALSE && ckValue == Value.TRUE && (inValue = state.getValue(this.getEndLocation(1))).isFullyDefined()) {
            myState.value = inValue.toIntValue();
        }
        myState.lastClock = ckValue;
        state.setValue(this.getEndLocation(0), Value.createKnown(this.dataWidth, myState.value), this, 8);
    }

    private State getState(CircuitState state) {
        State myState = (State)state.getData(this);
        if (myState == null) {
            myState = new State();
            state.setData(this, myState);
        }
        return myState;
    }

    @Override
    public void attributeListChanged(AttributeEvent e) {
    }

    @Override
    public void attributeValueChanged(AttributeEvent e) {
        Attribute attr = e.getAttribute();
        if (attr == width_attr) {
            this.setPins();
        }
    }

    @Override
    public void draw(ComponentDrawContext context) {
        Graphics g = context.getGraphics();
        Bounds bds = this.getBounds();
        State state = this.getState(context.getCircuitState());
        int width = this.dataWidth.getWidth();
        context.drawBounds(this);
        if (width <= 16 || !context.getShowState()) {
            context.drawPin(this, 1, "D", Direction.EAST);
            context.drawPin(this, 0, "Q", Direction.WEST);
        } else {
            context.drawPin(this, 1);
            context.drawPin(this, 0);
        }
        g.setColor(Color.GRAY);
        context.drawPin(this, 3, "clr", Direction.SOUTH);
        g.setColor(Color.BLACK);
        context.drawClock(this, 2, Direction.NORTH);
        if (context.getShowState()) {
            String str = StringUtil.toHexString(width, state.value);
            if (str.length() <= 4) {
                GraphicsUtil.drawText(g, str, bds.getX() + 15, bds.getY() + 4, 0, -1);
            } else {
                int split = str.length() - 4;
                GraphicsUtil.drawText(g, str.substring(0, split), bds.getX() + 15, bds.getY() + 3, 0, -1);
                GraphicsUtil.drawText(g, str.substring(split), bds.getX() + 15, bds.getY() + 15, 0, -1);
            }
        }
    }

    @Override
    public Object getFeature(Object key) {
        if (key == Pokable.class) {
            return this;
        }
        if (key == Loggable.class) {
            return this;
        }
        if (key == ToolTipMaker.class) {
            return this;
        }
        return super.getFeature(key);
    }

    @Override
    public Caret getPokeCaret(ComponentUserEvent event) {
        Bounds bds = this.getBounds();
        CircuitState circState = event.getCircuitState();
        State state = this.getState(circState);
        CompCaret ret = new CompCaret(state, circState);
        ret.setBounds(bds);
        return ret;
    }

    @Override
    public String getToolTip(ComponentUserEvent e) {
        int end = -1;
        for (int i = this.getEnds().size() - 1; i >= 0; --i) {
            if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
            end = i;
            break;
        }
        switch (end) {
            case 0: {
                return Strings.get("registerQTip");
            }
            case 1: {
                return Strings.get("registerDTip");
            }
            case 2: {
                return Strings.get("registerClkTip");
            }
            case 3: {
                return Strings.get("registerClrTip");
            }
        }
        return null;
    }

    @Override
    public Object[] getLogOptions(CircuitState state) {
        return null;
    }

    @Override
    public String getLogName(Object option) {
        return null;
    }

    @Override
    public Value getLogValue(CircuitState state, Object option) {
        return Value.createKnown(this.dataWidth, this.getState(state).value);
    }

    private class CompCaret
    extends AbstractCaret {
        State state;
        int initValue;
        int curValue;
        CircuitState circState;

        CompCaret(State state, CircuitState circState) {
            this.state = state;
            this.circState = circState;
            this.curValue = this.initValue = state.value;
        }

        @Override
        public void draw(Graphics g) {
            Bounds bds = Register.this.getBounds();
            int len = (Register.this.dataWidth.getWidth() + 3) / 4;
            g.setColor(Color.RED);
            if (len > 4) {
                g.drawRect(bds.getX(), bds.getY() + 3, bds.getWidth(), 25);
            } else {
                int wid = 7 * len + 2;
                g.drawRect(bds.getX() + (bds.getWidth() - wid) / 2, bds.getY() + 4, wid, 15);
            }
            g.setColor(Color.BLACK);
        }

        @Override
        public void stopEditing() {
        }

        @Override
        public void cancelEditing() {
            this.state.value = this.initValue;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            int val = Character.digit(e.getKeyChar(), 16);
            if (val < 0) {
                return;
            }
            this.curValue = this.curValue * 16 + val & Register.this.dataWidth.getMask();
            this.state.value = this.curValue;
            this.circState.setValue(Register.this.getEndLocation(0), Value.createKnown(Register.this.dataWidth, this.state.value), Register.this, 1);
        }
    }

    private static class State
    implements ComponentState,
    Cloneable {
        private int value = 0;
        private Value lastClock = Value.FALSE;

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

    private static class Factory
    extends AbstractComponentFactory {
        private Factory() {
        }

        @Override
        public String getName() {
            return "Register";
        }

        @Override
        public String getDisplayName() {
            return Strings.get("registerComponent");
        }

        @Override
        public AttributeSet createAttributeSet() {
            return AttributeSets.fixedSet(ATTRIBUTES, DEFAULTS);
        }

        @Override
        public Component createComponent(Location loc, AttributeSet attrs) {
            return new Register(loc, attrs);
        }

        @Override
        public Bounds getOffsetBounds(AttributeSet attrs) {
            return OFFSET_BOUNDS;
        }

        @Override
        public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
            Graphics g = context.getGraphics();
            Font old = g.getFont();
            g.setFont(old.deriveFont(9.0f));
            GraphicsUtil.drawCenteredText(g, "Reg", x + 10, y + 9);
            g.setFont(old);
            g.drawRect(x, y + 4, 19, 12);
            for (int dx = 2; dx < 20; dx += 5) {
                g.drawLine(x + dx, y + 2, x + dx, y + 4);
                g.drawLine(x + dx, y + 16, x + dx, y + 18);
            }
        }
    }

}

