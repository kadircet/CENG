/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.legacy;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentUserEvent;
import com.cburch.logisim.comp.ManagedComponent;
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.legacy.Strings;
import com.cburch.logisim.tools.AbstractCaret;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.KeyEvent;

class Register
extends AbstractComponentFactory {
    public static Register instance = new Register();
    private static final Bounds OFFSET_BOUNDS = Bounds.create(-30, -5, 30, 90);
    private static final int CK = 16;

    private Register() {
    }

    @Override
    public String getName() {
        return "Logisim 1.0 Register";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("registerComponent");
    }

    @Override
    public AttributeSet createAttributeSet() {
        return AttributeSets.EMPTY;
    }

    @Override
    public Component createComponent(Location loc, AttributeSet attrs) {
        return new Comp(loc, attrs);
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

    private static void setOutput(CircuitState circState, Comp comp, int val) {
        for (int i = 0; i < 8; ++i) {
            circState.setValue(comp.getEndLocation(8 + i), (val >> i & 1) == 1 ? Value.TRUE : Value.FALSE, comp, 8);
        }
    }

    private static String toHexString(int value) {
        String ret = Integer.toHexString(value);
        int len = ret.length();
        if (len < 2) {
            return "0" + ret;
        }
        if (len > 2) {
            return ret.substring(len - 2);
        }
        return ret;
    }

    private class Comp
    extends ManagedComponent
    implements Pokable {
        public Comp(Location loc, AttributeSet attrs) {
            int i;
            super(loc, attrs, 4);
            for (i = 0; i < 8; ++i) {
                this.setEnd(i, loc.translate(-30, 10 * i), BitWidth.ONE, 1);
            }
            for (i = 0; i < 8; ++i) {
                this.setEnd(8 + i, loc.translate(0, 10 * i), BitWidth.ONE, 2);
            }
            this.setEnd(16, loc.translate(-30, 80), BitWidth.ONE, 1);
        }

        @Override
        public ComponentFactory getFactory() {
            return Register.instance;
        }

        @Override
        public void propagate(CircuitState state) {
            State myState = this.getState(state);
            Value ckValue = state.getValue(this.getEndLocation(16));
            if (myState.lastClock == Value.FALSE && ckValue == Value.TRUE) {
                int value = 0;
                for (int i = 7; i >= 0; --i) {
                    Value inValue = state.getValue(this.getEndLocation(i));
                    value = value * 2 + (inValue == Value.TRUE ? 1 : 0);
                }
                myState.value = value;
            }
            myState.lastClock = ckValue;
            Register.setOutput(state, this, myState.value);
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
        public void draw(ComponentDrawContext context) {
            Graphics g = context.getGraphics();
            Bounds bds = this.getBounds();
            State state = this.getState(context.getCircuitState());
            context.drawBounds(this);
            for (int i = 0; i < 16; ++i) {
                context.drawPin(this, i);
            }
            context.drawClock(this, 16, Direction.EAST);
            if (context.getShowState()) {
                GraphicsUtil.drawText(g, Register.toHexString(state.value), bds.getX() + 15, bds.getY() + 4, 0, -1);
                if (state.isEditing) {
                    g.setColor(Color.RED);
                    g.drawRect(bds.getX() + 5, bds.getY() + 4, 20, 15);
                    g.setColor(Color.BLACK);
                }
            }
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
            Bounds bds = this.getBounds();
            CircuitState circState = event.getCircuitState();
            State state = this.getState(circState);
            CompCaret ret = new CompCaret(this, state, circState);
            ret.setBounds(bds);
            state.isEditing = true;
            return ret;
        }
    }

    private static class CompCaret
    extends AbstractCaret {
        Comp comp;
        State state;
        int initValue;
        int curValue;
        CircuitState circState;

        CompCaret(Comp comp, State state, CircuitState circState) {
            this.comp = comp;
            this.state = state;
            this.circState = circState;
            this.curValue = this.initValue = state.value;
        }

        @Override
        public void stopEditing() {
            this.state.isEditing = false;
        }

        @Override
        public void cancelEditing() {
            this.state.value = this.initValue;
            this.state.isEditing = false;
        }

        @Override
        public void keyTyped(KeyEvent e) {
            int val = Character.digit(e.getKeyChar(), 16);
            if (val < 0) {
                return;
            }
            this.curValue = this.curValue * 16 + val & 255;
            this.state.value = this.curValue;
            Register.setOutput(this.circState, this.comp, this.state.value);
        }
    }

    private static class State {
        private int value = 0;
        private Value lastClock = Value.FALSE;
        private boolean isEditing = false;
    }

}

