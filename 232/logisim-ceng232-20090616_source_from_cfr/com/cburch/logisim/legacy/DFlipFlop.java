/*
 * Decompiled with CFR 0_114.
 */
package com.cburch.logisim.legacy;

import com.cburch.logisim.circuit.CircuitState;
import com.cburch.logisim.comp.AbstractComponentFactory;
import com.cburch.logisim.comp.Component;
import com.cburch.logisim.comp.ComponentDrawContext;
import com.cburch.logisim.comp.ComponentFactory;
import com.cburch.logisim.comp.ComponentState;
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
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretListener;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.util.GraphicsUtil;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

class DFlipFlop
extends AbstractComponentFactory {
    static final DFlipFlop instance = new DFlipFlop();
    private static final Bounds OFFSET_BOUNDS = Bounds.create(-30, -5, 30, 30);
    private static final int D = 0;
    private static final int CK = 1;
    private static final int Q = 2;
    private static final int Qnot = 3;

    protected DFlipFlop() {
    }

    @Override
    public String getName() {
        return "Logisim 1.0 D Flip-Flop";
    }

    @Override
    public String getDisplayName() {
        return Strings.get("dFlipFlopComponent");
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
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.setColor(color);
        Bounds bds = OFFSET_BOUNDS;
        context.drawRectangle(this, x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight(), "");
    }

    @Override
    public void paintIcon(ComponentDrawContext context, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.drawRect(x + 2, y + 2, 16, 16);
        GraphicsUtil.drawCenteredText(g, "D", x + 10, y + 8);
    }

    private class Comp
    extends ManagedComponent
    implements Pokable {
        public Comp(Location loc, AttributeSet attrs) {
            super(loc, attrs, 4);
            Location pt = this.getLocation();
            BitWidth w = BitWidth.ONE;
            this.setEnd(0, pt.translate(-30, 0), w, 1);
            this.setEnd(1, pt.translate(-30, 20), w, 1);
            this.setEnd(2, pt, w, 2);
            this.setEnd(3, pt.translate(0, 20), w, 2);
        }

        @Override
        public ComponentFactory getFactory() {
            return DFlipFlop.instance;
        }

        @Override
        public void propagate(CircuitState state) {
            Value clock;
            boolean changed = false;
            StateData myState = (StateData)state.getData(this);
            if (myState == null) {
                changed = true;
                myState = new StateData();
                state.setData(this, myState);
            }
            Value lastClock = myState.lastClock;
            myState.lastClock = clock = state.getValue(this.getEndLocation(1));
            if (lastClock == Value.FALSE && clock == Value.TRUE) {
                Value newVal = state.getValue(this.getEndLocation(0));
                changed |= myState.curValue != newVal;
                myState.curValue = newVal;
            }
            Location loc = this.getLocation();
            if (changed || !loc.equals(myState.propLocation)) {
                myState.propLocation = loc;
                state.setValue(this.getEndLocation(2), myState.curValue, this, 6);
                state.setValue(this.getEndLocation(3), myState.curValue.not(), this, 6);
            }
        }

        @Override
        public void draw(ComponentDrawContext context) {
            Bounds bds = this.getBounds();
            context.drawRectangle(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(), "");
            context.drawPin(this, 0, "D", Direction.EAST);
            context.drawClock(this, 1, Direction.EAST);
            context.drawPin(this, 2, "Q", Direction.WEST);
            context.drawPin(this, 3);
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
                PokeCaret ret = new PokeCaret(this, event.getCircuitState());
                ret.isPressed = ret.isInside(event.getX(), event.getY());
                return ret;
            }
            return null;
        }
    }

    private static class PokeCaret
    implements Caret {
        Comp comp;
        CircuitState state;
        boolean isPressed = true;

        PokeCaret(Comp comp, CircuitState state) {
            this.comp = comp;
            this.state = state;
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
            return this.comp.getBounds();
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
            this.isPressed = this.isInside(e.getX(), e.getY());
        }

        @Override
        public void mouseDragged(MouseEvent e) {
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            if (this.isPressed && this.isInside(e.getX(), e.getY())) {
                StateData myState = (StateData)this.state.getData(this.comp);
                if (myState == null) {
                    return;
                }
                myState.curValue = myState.curValue.not();
                this.state.setValue(this.comp.getEndLocation(2), myState.curValue, this.comp, 1);
                this.state.setValue(this.comp.getEndLocation(3), myState.curValue.not(), this.comp, 1);
            }
            this.isPressed = false;
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

        private boolean isInside(int x, int y) {
            return this.comp.getBounds().contains(x, y);
        }
    }

    private static class StateData
    implements ComponentState,
    Cloneable {
        Value lastClock = Value.FALSE;
        Value curValue = Value.FALSE;
        Location propLocation = null;

        private StateData() {
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

