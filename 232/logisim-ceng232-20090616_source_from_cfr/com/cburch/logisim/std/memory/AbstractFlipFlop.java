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
import com.cburch.logisim.data.AttributeSet;
import com.cburch.logisim.data.AttributeSets;
import com.cburch.logisim.data.BitWidth;
import com.cburch.logisim.data.Bounds;
import com.cburch.logisim.data.Direction;
import com.cburch.logisim.data.Location;
import com.cburch.logisim.data.Value;
import com.cburch.logisim.gui.log.Loggable;
import com.cburch.logisim.std.memory.Strings;
import com.cburch.logisim.tools.Caret;
import com.cburch.logisim.tools.CaretListener;
import com.cburch.logisim.tools.Pokable;
import com.cburch.logisim.tools.ToolTipMaker;
import com.cburch.logisim.util.GraphicsUtil;
import com.cburch.logisim.util.StringGetter;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;

abstract class AbstractFlipFlop
extends AbstractComponentFactory {
    private String name;
    private StringGetter desc;
    private Bounds offsetBounds = Bounds.create(-40, -10, 40, 40);

    protected AbstractFlipFlop(String name, StringGetter desc) {
        this.name = name;
        this.desc = desc;
    }

    @Override
    public String getName() {
        return this.name;
    }

    @Override
    public String getDisplayName() {
        return this.desc.get();
    }

    @Override
    public AttributeSet createAttributeSet() {
        return AttributeSets.EMPTY;
    }

    @Override
    public Component createComponent(Location loc, AttributeSet attrs) {
        return new Comp(loc, attrs, this);
    }

    @Override
    public Bounds getOffsetBounds(AttributeSet attrs) {
        return this.offsetBounds;
    }

    @Override
    public void drawGhost(ComponentDrawContext context, Color color, int x, int y, AttributeSet attrs) {
        Graphics g = context.getGraphics();
        g.setColor(color);
        Bounds bds = this.getOffsetBounds(attrs);
        context.drawRectangle(this, x + bds.getX(), y + bds.getY(), bds.getWidth(), bds.getHeight(), "");
    }

    @Override
    public abstract void paintIcon(ComponentDrawContext var1, int var2, int var3, AttributeSet var4);

    protected abstract int getNumInputs();

    protected abstract String getInputName(int var1);

    protected abstract Value computeValue(Value[] var1, Value var2);

    private class Comp
    extends ManagedComponent
    implements Pokable,
    ToolTipMaker,
    Loggable {
        private AbstractFlipFlop src;

        public Comp(Location loc, AttributeSet attrs, AbstractFlipFlop src) {
            super(loc, attrs, 4);
            this.src = src;
            Location pt = this.getLocation();
            BitWidth w = BitWidth.ONE;
            int n = AbstractFlipFlop.this.getNumInputs();
            if (n == 1) {
                this.setEnd(0, pt.translate(-40, 20), w, 1);
                this.setEnd(1, pt.translate(-40, 0), w, 1);
            } else if (n == 2) {
                this.setEnd(0, pt.translate(-40, 0), w, 1);
                this.setEnd(1, pt.translate(-40, 20), w, 1);
                this.setEnd(2, pt.translate(-40, 10), w, 1);
            } else {
                throw new RuntimeException("flip-flop input > 1");
            }
            this.setEnd(n + 1, pt, w, 2);
            this.setEnd(n + 2, pt.translate(0, 20), w, 2);
            this.setEnd(n + 3, pt.translate(-10, 30), w, 1);
            this.setEnd(n + 4, pt.translate(-30, 30), w, 1);
        }

        @Override
        public ComponentFactory getFactory() {
            return this.src;
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
            int n = AbstractFlipFlop.this.getNumInputs();
            Value lastClock = myState.lastClock;
            myState.lastClock = clock = state.getValue(this.getEndLocation(n));
            if (state.getValue(this.getEndLocation(n + 3)) == Value.TRUE) {
                changed |= myState.curValue != Value.FALSE;
                myState.curValue = Value.FALSE;
            } else if (state.getValue(this.getEndLocation(n + 4)) == Value.TRUE) {
                changed |= myState.curValue != Value.TRUE;
                myState.curValue = Value.TRUE;
            } else if (lastClock == Value.FALSE && clock == Value.TRUE) {
                Value[] inputs = new Value[n];
                for (int i = 0; i < n; ++i) {
                    inputs[i] = state.getValue(this.getEndLocation(i));
                }
                Value newVal = AbstractFlipFlop.this.computeValue(inputs, myState.curValue);
                if (newVal == Value.TRUE || newVal == Value.FALSE) {
                    changed |= myState.curValue != newVal;
                    myState.curValue = newVal;
                }
            }
            Location loc = this.getLocation();
            if (changed || !loc.equals(myState.propLocation)) {
                myState.propLocation = loc;
                state.setValue(this.getEndLocation(n + 1), myState.curValue, this, 5);
                state.setValue(this.getEndLocation(n + 2), myState.curValue.not(), this, 5);
            }
        }

        @Override
        public void draw(ComponentDrawContext context) {
            StateData myState;
            Graphics g = context.getGraphics();
            AbstractFlipFlop src = (AbstractFlipFlop)this.getFactory();
            Location loc = this.getLocation();
            Bounds bds = this.getBounds();
            context.drawRectangle(bds.getX(), bds.getY(), bds.getWidth(), bds.getHeight(), "");
            if (context.getShowState() && (myState = (StateData)context.getCircuitState().getData(this)) != null) {
                int x = loc.getX();
                int y = loc.getY();
                g.setColor(myState.curValue.getColor());
                g.fillOval(x - 26, y + 4, 13, 13);
                g.setColor(Color.WHITE);
                GraphicsUtil.drawCenteredText(g, myState.curValue.toDisplayString(), x - 19, y + 9);
                g.setColor(Color.BLACK);
            }
            int n = src.getNumInputs();
            for (int i = 0; i < n; ++i) {
                context.drawPin(this, i, src.getInputName(i), Direction.EAST);
            }
            context.drawClock(this, n, Direction.EAST);
            context.drawPin(this, n + 1, "Q", Direction.WEST);
            context.drawPin(this, n + 2);
            context.drawPin(this, n + 3);
            context.drawPin(this, n + 4);
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
            if (this.getBounds().contains(event.getX(), event.getY())) {
                PokeCaret ret = new PokeCaret(this, event.getCircuitState());
                ret.isPressed = ret.isInside(event.getX(), event.getY());
                return ret;
            }
            return null;
        }

        @Override
        public String getToolTip(ComponentUserEvent e) {
            int n;
            int end = -1;
            for (int i = this.getEnds().size() - 1; i >= 0; --i) {
                if (this.getEndLocation(i).manhattanDistanceTo(e.getX(), e.getY()) >= 10) continue;
                end = i;
                break;
            }
            if (end == (n = AbstractFlipFlop.this.getNumInputs())) {
                return Strings.get("flipFlopClockTip");
            }
            if (end == n + 1) {
                return Strings.get("flipFlopQTip");
            }
            if (end == n + 2) {
                return Strings.get("flipFlopNotQTip");
            }
            if (end == n + 3) {
                return Strings.get("flipFlopResetTip");
            }
            if (end == n + 4) {
                return Strings.get("flipFlopPresetTip");
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
            StateData s = (StateData)state.getData(this);
            return s == null ? Value.FALSE : s.curValue;
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
                AbstractFlipFlop src = (AbstractFlipFlop)this.comp.getFactory();
                StateData myState = (StateData)this.state.getData(this.comp);
                if (myState == null) {
                    return;
                }
                myState.curValue = myState.curValue.not();
                this.state.setValue(this.comp.getLocation(), myState.curValue, this.comp, 1);
                this.state.setValue(this.comp.getEndLocation(src.getNumInputs() + 2), myState.curValue.not(), this.comp, 1);
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
            int dy;
            Location loc = this.comp.getLocation();
            int dx = x - (loc.getX() - 20);
            int d2 = dx * dx + (dy = y - (loc.getY() + 10)) * dy;
            return d2 < 64;
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

